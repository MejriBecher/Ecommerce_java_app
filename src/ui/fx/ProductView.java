package ui.fx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Produit;

import java.util.Optional;
import java.util.logging.Logger;

public class ProductView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(ProductView.class.getName());
    private final TableView<Produit> table;
    private final ObservableList<Produit> data;
    private final Navigator nav;

    public ProductView(Navigator nav) {
        this.nav = nav;

        Label title = new Label("Catalogue Produits");
        title.getStyleClass().add("title");
        BorderPane.setAlignment(title, Pos.CENTER);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Produit, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produit, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));

        TableColumn<Produit, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));

        TableColumn<Produit, Double> prixCol = new TableColumn<>("Prix (DT)");
        prixCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrix()).asObject());

        TableColumn<Produit, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getStock()).asObject());

        table.getColumns().addAll(idCol, nomCol, descCol, prixCol, stockCol);

        data = FXCollections.observableArrayList();
        table.setItems(data);
        chargerDonnees();

        HBox actions = new HBox(8);
        actions.setPadding(new Insets(10, 0, 0, 0));
        actions.setAlignment(Pos.CENTER);

        if (nav.getAuth().estAdmin()) {
            Button addBtn = new Button("Ajouter");
            addBtn.setOnAction(e -> ajouterProduit());

            Button editBtn = new Button("Modifier");
            editBtn.setOnAction(e -> modifierProduit());

            Button delBtn = new Button("Supprimer");
            delBtn.setOnAction(e -> supprimerProduit());

            Button ordersBtn = new Button("Toutes les commandes");
            ordersBtn.setOnAction(e -> nav.showOrders());

            Button logoutBtn = new Button("Déconnexion");
            logoutBtn.setOnAction(e -> {
                nav.getAuth().deconnecter();
                nav.showLogin();
            });

            actions.getChildren().addAll(addBtn, editBtn, delBtn, ordersBtn, logoutBtn);
        } else {
            Button addCartBtn = new Button("Ajouter au panier");
            addCartBtn.setOnAction(e -> ajouterAuPanier());

            Button cartBtn = new Button("Voir panier");
            cartBtn.setOnAction(e -> nav.showCart());

            Button ordersBtn = new Button("Mes commandes");
            ordersBtn.setOnAction(e -> nav.showOrders());

            Button logoutBtn = new Button("Déconnexion");
            logoutBtn.setOnAction(e -> {
                nav.getAuth().deconnecter();
                nav.showLogin();
            });

            actions.getChildren().addAll(addCartBtn, cartBtn, ordersBtn, logoutBtn);
        }

        VBox top = new VBox(8, title);
        top.setPadding(new Insets(16, 16, 0, 16));

        setTop(top);
        setCenter(table);
        setBottom(actions);
        setPadding(new Insets(12));
    }

    private void chargerDonnees() {
        data.setAll(nav.getProduitService().listerProduits());
    }

    private Produit selection() {
        Produit p = table.getSelectionModel().getSelectedItem();
        if (p == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Sélectionnez un produit.");
            alert.show();
        }
        return p;
    }

    private void ajouterProduit() {
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un produit");
        dialog.setHeaderText("Nouveau produit");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField prixField = new TextField();
        prixField.setPromptText("Prix (DT)");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");

        VBox fields = new VBox(8, nomField, descField, prixField, stockField);
        dialog.getDialogPane().setContent(fields);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String desc = descField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());
                    return new Produit(nom, desc, prix, stock);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Produit> result = dialog.showAndWait();
        result.ifPresent(p -> {
            LOG.info("Ajout produit: " + p.getNom());
            boolean ok = nav.getProduitService().ajouterProduit(p.getNom(), p.getDescription(), p.getPrix(), p.getStock());
            Alert alert = ok ? new Alert(Alert.AlertType.INFORMATION, "Produit ajouté.")
                             : new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout.");
            alert.show();
            chargerDonnees();
        });
    }

    private void modifierProduit() {
        Produit p = selection();
        if (p == null) return;

        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Modifier un produit");
        dialog.setHeaderText("Modification: " + p.getNom());

        TextField nomField = new TextField(p.getNom());
        TextField descField = new TextField(p.getDescription());
        TextField prixField = new TextField(String.valueOf(p.getPrix()));
        TextField stockField = new TextField(String.valueOf(p.getStock()));

        VBox fields = new VBox(8, nomField, descField, prixField, stockField);
        dialog.getDialogPane().setContent(fields);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Produit(
                        p.getId(),
                        nomField.getText().trim(),
                        descField.getText().trim(),
                        Double.parseDouble(prixField.getText().trim()),
                        Integer.parseInt(stockField.getText().trim())
                    );
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Produit> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            LOG.info("Modification produit ID: " + updated.getId());
            boolean ok = nav.getProduitService().modifierProduit(
                updated.getId(), updated.getNom(), updated.getDescription(),
                updated.getPrix(), updated.getStock());
            Alert alert = ok ? new Alert(Alert.AlertType.INFORMATION, "Produit modifié.")
                             : new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification.");
            alert.show();
            chargerDonnees();
        });
    }

    private void supprimerProduit() {
        Produit p = selection();
        if (p == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + p.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            LOG.info("Suppression produit ID: " + p.getId());
            boolean ok = nav.getProduitService().supprimerProduit(p.getId());
            Alert alert = ok ? new Alert(Alert.AlertType.INFORMATION, "Produit supprimé.")
                             : new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
            alert.show();
            chargerDonnees();
        }
    }

    private void ajouterAuPanier() {
        Produit p = selection();
        if (p == null) return;

        if (p.getStock() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Produit en rupture de stock.");
            alert.show();
            return;
        }

        TextInputDialog qtyDialog = new TextInputDialog("1");
        qtyDialog.setTitle("Ajouter au panier");
        qtyDialog.setHeaderText(p.getNom());
        qtyDialog.setContentText("Quantité (max " + p.getStock() + ") :");

        Optional<String> result = qtyDialog.showAndWait();
        if (result.isPresent()) {
            try {
                int qte = Integer.parseInt(result.get().trim());
                if (qte <= 0 || qte > p.getStock()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Quantité invalide. Maximum: " + p.getStock());
                    alert.show();
                    return;
                }
                int userId = nav.getAuth().getUtilisateurConnecte().getId();
                boolean ok = nav.getPanierDAO().ajouterOuMettreAJour(userId, p.getId(), qte);
                if (ok) {
                    nav.getPanierCourant().ajouterProduit(p, qte);
                    LOG.info("Ajout au panier: " + p.getNom() + " x" + qte);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ajouté au panier !");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout au panier.");
                    alert.show();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Quantité invalide.");
                alert.show();
            }
        }
    }
}
