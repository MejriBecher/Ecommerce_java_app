package ui.fx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Commande;
import model.LigneCommande;

import java.util.List;
import java.util.logging.Logger;

public class OrderView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(OrderView.class.getName());
    private final TableView<Commande> table;
    private final ObservableList<Commande> data;
    private final Navigator nav;
    private final boolean isAdmin;

    public OrderView(Navigator nav) {
        this.nav = nav;
        this.isAdmin = nav.getAuth().estAdmin();

        Label title = new Label(isAdmin ? "Toutes les commandes" : "Mes commandes");
        title.getStyleClass().add("title");
        BorderPane.setAlignment(title, Pos.CENTER);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Commande, Integer> idCol = new TableColumn<>("Commande #");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(
                c.getValue().getId()).asObject());

        if (isAdmin) {
            TableColumn<Commande, Integer> userCol = new TableColumn<>("Utilisateur ID");
            userCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(
                    c.getValue().getUserId()).asObject());
            table.getColumns().add(userCol);
        }

        TableColumn<Commande, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDate().toString()));

        TableColumn<Commande, Double> totalCol = new TableColumn<>("Total (DT)");
        totalCol.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getTotal()).asObject());

        TableColumn<Commande, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatut().name()));

        table.getColumns().addAll(idCol, dateCol, totalCol, statutCol);

        if (isAdmin) {
            TableColumn<Commande, Commande> actionCol = new TableColumn<>("Actions");
            actionCol.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue()));
            actionCol.setCellFactory(col -> new TableCell<>() {
                private final Button validerBtn = new Button("Valider");
                private final Button annulerBtn = new Button("Annuler");
                private final HBox pane = new HBox(6, validerBtn, annulerBtn);

                {
                    validerBtn.setOnAction(e -> {
                        Commande cmd = getTableView().getItems().get(getIndex());
                        if (nav.getCommandeService().validerCommande(cmd.getId(), nav.getAuth())) {
                            chargerDonnees();
                        }
                    });
                    annulerBtn.setOnAction(e -> {
                        Commande cmd = getTableView().getItems().get(getIndex());
                        if (nav.getCommandeService().annulerCommande(cmd.getId(), nav.getAuth())) {
                            chargerDonnees();
                        }
                    });
                    setAlignment(Pos.CENTER);
                }

                @Override
                protected void updateItem(Commande item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        boolean enAttente = item.getStatut() == Commande.Statut.EN_ATTENTE;
                        validerBtn.setVisible(enAttente);
                        annulerBtn.setVisible(enAttente);
                        setGraphic(pane);
                    }
                }
            });
            table.getColumns().add(actionCol);
        }

        table.setRowFactory(tv -> {
            TableRow<Commande> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    afficherDetail(row.getItem());
                }
            });
            return row;
        });

        data = FXCollections.observableArrayList();
        table.setItems(data);
        chargerDonnees();

        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button detailBtn = new Button("Voir détail");
        detailBtn.setOnAction(e -> {
            Commande c = table.getSelectionModel().getSelectedItem();
            if (c != null) afficherDetail(c);
        });

        Button backBtn = new Button("Retour");
        backBtn.setOnAction(e -> nav.showProducts());

        actions.getChildren().addAll(detailBtn, backBtn);

        VBox top = new VBox(8, title);
        top.setPadding(new Insets(16, 16, 0, 16));

        setTop(top);
        setCenter(table);
        setBottom(actions);
        setPadding(new Insets(12));
    }

    private void chargerDonnees() {
        List<Commande> commandes;
        if (isAdmin) {
            commandes = nav.getCommandeDAO().getToutesLesCommandes();
        } else {
            int userId = nav.getAuth().getUtilisateurConnecte().getId();
            commandes = nav.getCommandeDAO().getCommandesByUser(userId);
        }
        data.setAll(commandes);
        LOG.info("Chargement commandes: " + commandes.size() + " trouvées");
    }

    private void afficherDetail(Commande c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Commande #").append(c.getId()).append("\n");
        sb.append("Date: ").append(c.getDate()).append("\n");
        sb.append("Statut: ").append(c.getStatut().name()).append("\n\n");
        sb.append("Articles:\n");
        for (LigneCommande lc : c.getLignes()) {
            sb.append("  - ").append(lc.getProduit().getNom())
              .append(" x").append(lc.getQuantite())
              .append(" = ").append(String.format("%.2f", lc.getSousTotal()))
              .append(" DT\n");
        }
        sb.append("\nTotal: ").append(String.format("%.2f DT", c.getTotal()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString());
        alert.setTitle("Détail commande #" + c.getId());
        alert.setHeaderText(null);
        alert.getDialogPane().setMinWidth(400);
        alert.show();
    }
}
