package ui.fx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.LigneCommande;
import model.Panier;

import java.util.Optional;
import java.util.logging.Logger;

public class CartView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(CartView.class.getName());
    private final TableView<LigneCommande> table;
    private final ObservableList<LigneCommande> data;
    private final Label totalLabel;
    private final Navigator nav;

    public CartView(Navigator nav) {
        this.nav = nav;

        Label title = new Label("Mon Panier");
        title.getStyleClass().add("title");
        BorderPane.setAlignment(title, Pos.CENTER);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<LigneCommande, String> prodCol = new TableColumn<>("Produit");
        prodCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getProduit().getNom()));

        TableColumn<LigneCommande, Double> prixCol = new TableColumn<>("Prix unitaire (DT)");
        prixCol.setCellValueFactory(cell -> new SimpleDoubleProperty(
                cell.getValue().getProduit().getPrix()).asObject());

        TableColumn<LigneCommande, Integer> qteCol = new TableColumn<>("Quantité");
        qteCol.setCellValueFactory(cell -> new SimpleIntegerProperty(
                cell.getValue().getQuantite()).asObject());

        TableColumn<LigneCommande, Double> totalCol = new TableColumn<>("Sous-total (DT)");
        totalCol.setCellValueFactory(cell -> new SimpleDoubleProperty(
                cell.getValue().getSousTotal()).asObject());

        table.getColumns().addAll(prodCol, prixCol, qteCol, totalCol);

        Panier panier = nav.getPanierCourant();
        data = FXCollections.observableArrayList();
        table.setItems(data);

        totalLabel = new Label();
        totalLabel.getStyleClass().add("total-label");

        if (panier != null && !panier.estVide()) {
            data.setAll(panier.getItems());
            totalLabel.setText(String.format("Total: %.2f DT", panier.calculerTotal()));
        } else {
            Label empty = new Label("Votre panier est vide.");
            empty.setStyle("-fx-font-size: 16; -fx-text-fill: #888;");
            table.setPlaceholder(empty);
            totalLabel.setText("Total: 0.00 DT");
        }

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button modifyBtn = new Button("Modifier quantité");
        modifyBtn.setOnAction(e -> modifierQuantite());

        Button removeBtn = new Button("Retirer");
        removeBtn.setOnAction(e -> retirer());

        Button checkoutBtn = new Button("Valider commande");
        checkoutBtn.setDefaultButton(true);
        checkoutBtn.setOnAction(e -> nav.showCheckout());

        Button backBtn = new Button("Continuer mes achats");
        backBtn.setOnAction(e -> nav.showProducts());

        actions.getChildren().addAll(modifyBtn, removeBtn, checkoutBtn, backBtn);

        VBox bottom = new VBox(8, totalLabel, actions);
        bottom.setPadding(new Insets(0, 0, 12, 0));

        VBox top = new VBox(8, title);
        top.setPadding(new Insets(16, 16, 0, 16));

        setTop(top);
        setCenter(table);
        setBottom(bottom);
        setPadding(new Insets(12));
    }

    private LigneCommande selection() {
        LigneCommande lc = table.getSelectionModel().getSelectedItem();
        if (lc == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Sélectionnez un article.");
            alert.show();
        }
        return lc;
    }

    private void modifierQuantite() {
        LigneCommande lc = selection();
        if (lc == null) return;

        int stock = lc.getProduit().getStock();
        TextInputDialog dialog = new TextInputDialog(String.valueOf(lc.getQuantite()));
        dialog.setTitle("Modifier quantité");
        dialog.setHeaderText(lc.getProduit().getNom());
        dialog.setContentText("Nouvelle quantité (0 pour retirer, max " + stock + ") :");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int qte = Integer.parseInt(result.get().trim());
                int userId = nav.getAuth().getUtilisateurConnecte().getId();
                int prodId = lc.getProduit().getId();

                nav.getPanierDAO().modifierQuantite(userId, prodId, qte);
                nav.getPanierCourant().modifierQuantite(prodId, qte);
                LOG.info("Quantité modifiée: " + lc.getProduit().getNom() + " -> " + qte);

                rafraichir();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Quantité invalide.");
                alert.show();
            }
        }
    }

    private void retirer() {
        LigneCommande lc = selection();
        if (lc == null) return;

        int userId = nav.getAuth().getUtilisateurConnecte().getId();
        int prodId = lc.getProduit().getId();

        nav.getPanierDAO().supprimer(userId, prodId);
        nav.getPanierCourant().supprimerProduit(prodId);
        LOG.info("Retiré du panier: " + lc.getProduit().getNom());
        rafraichir();
    }

    private void rafraichir() {
        Panier panier = nav.getPanierCourant();
        if (panier.estVide()) {
            data.clear();
            totalLabel.setText("Total: 0.00 DT");
        } else {
            data.setAll(panier.getItems());
            totalLabel.setText(String.format("Total: %.2f DT", panier.calculerTotal()));
        }
    }
}
