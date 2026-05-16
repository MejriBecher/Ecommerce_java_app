package ui.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class RegisterView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(RegisterView.class.getName());

    public RegisterView(Navigator nav) {
        Label title = new Label("Inscription");
        title.getStyleClass().add("title");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField mdpField = new PasswordField();
        mdpField.setPromptText("Mot de passe (min 4 caractères)");

        Button registerBtn = new Button("S'inscrire");
        registerBtn.setDefaultButton(true);
        registerBtn.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String email = emailField.getText().trim();
            String mdp = mdpField.getText();
            LOG.info("Tentative d'inscription: " + email);
            boolean ok = nav.getAuth().inscrire(nom, email, mdp);
            if (ok) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inscription réussie ! Connectez-vous.");
                alert.show();
                nav.showLogin();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Inscription échouée. Vérifiez les informations.");
                alert.show();
            }
        });

        Hyperlink loginLink = new Hyperlink("Déjà un compte ? Connectez-vous");
        loginLink.setOnAction(e -> nav.showLogin());

        VBox form = new VBox(12, nomField, emailField, mdpField, registerBtn, loginLink);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(320);

        VBox center = new VBox(title, form);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(24);
        center.setPadding(new Insets(40));
        setCenter(center);
    }
}
