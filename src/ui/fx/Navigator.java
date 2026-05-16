package ui.fx;

import dao.CommandeDAO;
import dao.PanierDAO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Panier;
import service.AuthService;
import service.CommandeService;
import service.ProduitService;

import java.util.logging.Logger;

public class Navigator {

    private static final Logger LOG = Logger.getLogger(Navigator.class.getName());

    private final Stage stage;
    private final StackPane root;
    private final AuthService auth;
    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final PanierDAO panierDAO;
    private final CommandeDAO commandeDAO;
    private Panier panierCourant;

    public Navigator(Stage stage) {
        this.stage = stage;
        this.root = new StackPane();
        this.auth = new AuthService();
        this.produitService = new ProduitService();
        this.commandeService = new CommandeService();
        this.panierDAO = new PanierDAO();
        this.commandeDAO = new CommandeDAO();

        Scene scene = new Scene(root, 960, 680);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("E-Commerce");
        stage.setResizable(true);

        showLogin();
    }

    void showLogin() {
        LOG.info("Navigation -> LoginView");
        setView(new LoginView(this));
    }

    void showRegister() {
        LOG.info("Navigation -> RegisterView");
        setView(new RegisterView(this));
    }

    void showProducts() {
        LOG.info("Navigation -> ProductView");
        int userId = auth.getUtilisateurConnecte().getId();
        panierCourant = panierDAO.chargerPanier(userId);
        setView(new ProductView(this));
    }

    void showCart() {
        LOG.info("Navigation -> CartView");
        setView(new CartView(this));
    }

    void showCheckout() {
        LOG.info("Navigation -> CheckoutView");
        setView(new CheckoutView(this));
    }

    void showOrders() {
        LOG.info("Navigation -> OrderView");
        setView(new OrderView(this));
    }

    private void setView(Parent view) {
        root.getChildren().setAll(view);
    }

    AuthService getAuth() { return auth; }
    ProduitService getProduitService() { return produitService; }
    CommandeService getCommandeService() { return commandeService; }
    PanierDAO getPanierDAO() { return panierDAO; }
    CommandeDAO getCommandeDAO() { return commandeDAO; }
    Panier getPanierCourant() { return panierCourant; }
    void setPanierCourant(Panier p) { this.panierCourant = p; }
}
