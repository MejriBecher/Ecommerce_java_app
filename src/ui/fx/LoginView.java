package ui.fx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class LoginView extends BorderPane {

    private static final Logger LOG = Logger.getLogger(LoginView.class.getName());

    public LoginView(Navigator nav) {
        Label title = new Label("Connexion");
        title.getStyleClass().add("title");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField mdpField = new PasswordField();
        mdpField.setPromptText("Mot de passe");

        Button loginBtn = new Button("Se connecter");
        loginBtn.setDefaultButton(true);
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String mdp = mdpField.getText();
            LOG.info("Tentative de connexion: " + email);
            boolean ok = nav.getAuth().connecter(email, mdp);
            if (ok) {
                if (nav.getAuth().estAdmin()) {
                    nav.showProducts();
                } else {
                    nav.showProducts();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect.");
                alert.show();
            }
        });

        Hyperlink registerLink = new Hyperlink("Créer un compte");
        registerLink.setOnAction(e -> nav.showRegister());

        VBox form = new VBox(12, emailField, mdpField, loginBtn, registerLink);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(320);

        VBox center = new VBox(title, form);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(24);
        center.setPadding(new Insets(40));
        setCenter(center);
    }
}
