package ui.fx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class MainApp extends Application {

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    @Override
    public void start(Stage stage) {
        LOG.info("Démarrage de l'interface JavaFX");
        new Navigator(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
