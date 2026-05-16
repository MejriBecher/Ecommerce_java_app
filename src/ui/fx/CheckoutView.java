package ui.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.LigneCommande;
import model.Panier;

import java.util.logging.Logger;

public class CheckoutView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(CheckoutView.class.getName());

    public CheckoutView(Navigator nav) {
        Label title = new Label("Confirmation de commande");
        title.getStyleClass().add("title");

        Panier panier = nav.getPanierCourant();

        VBox itemsBox = new VBox(6);
        itemsBox.setPadding(new Insets(12));
        if (panier != null && !panier.estVide()) {
            for (LigneCommande lc : panier.getItems()) {
                Label itemLabel = new Label(String.format("%s x%d = %.2f DT",
                        lc.getProduit().getNom(), lc.getQuantite(), lc.getSousTotal()));
                itemsBox.getChildren().add(itemLabel);
            }
        }

        Label totalLabel = new Label();
        totalLabel.getStyleClass().add("total-label");
        if (panier != null) {
            totalLabel.setText(String.format("Total à payer: %.2f DT", panier.calculerTotal()));
        }

        Label confirmLabel = new Label("Confirmer la commande ?");
        confirmLabel.setStyle("-fx-font-size: 14; -fx-padding: 12 0 0 0;");

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(16, 0, 0, 0));

        Button confirmBtn = new Button("Confirmer");
        confirmBtn.setDefaultButton(true);
        confirmBtn.setOnAction(e -> {
            int userId = nav.getAuth().getUtilisateurConnecte().getId();
            LOG.info("Validation commande pour user ID: " + userId);
            boolean ok = nav.getCommandeService().passerCommande(userId, panier);
            if (ok) {
                nav.getPanierDAO().vider(userId);
                nav.getPanierCourant().viderSilencieux();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Commande validée !");
                alert.show();
                LOG.info("Commande validée avec succès");
                nav.showProducts();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la validation.");
                alert.show();
                LOG.warning("Échec validation commande");
            }
        });

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setOnAction(e -> nav.showCart());

        actions.getChildren().addAll(confirmBtn, cancelBtn);

        VBox center = new VBox(8, title, itemsBox, totalLabel, confirmLabel, actions);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(24));

        setCenter(center);
        setPadding(new Insets(12));
    }
}
