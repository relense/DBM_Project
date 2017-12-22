package Menu;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import Scene.SceneChanger;

import java.sql.SQLException;

/**
 * Created by Relense on 02/05/2017.
 */
public class BarMenu {

    private SceneChanger sceneChanger;

    public BarMenu() {
        sceneChanger = new SceneChanger();
    }

    /**
     * Method to create the menu bar
     * This bar will provide some options and will be present in almost all scenes
     *
     * @param primaryStage stage we are currently in
     * @return menubar
     */
    public MenuBar PainelComMenu(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        Menu menuConfigurar = new Menu("Menu");

        MenuItem back = new MenuItem("Back");
        back.setOnAction(e -> {
            try {
                sceneChanger.mainMenu(primaryStage);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        menuConfigurar.getItems().addAll(back);
        MenuItem menuFechar = new MenuItem("Fechar");
        menuFechar.setOnAction(e -> Platform.exit());
        menuBar.getMenus().addAll(menuConfigurar);

        return menuBar;
    }

}
