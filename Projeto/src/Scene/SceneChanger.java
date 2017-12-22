package Scene;

import Menu.BarMenu;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import MyModels.MyModels;
import MyModels.DataManager;

import javax.xml.crypto.Data;

/**
 * Created by Relense on 02/05/2017.
 */
public class SceneChanger {
    private MyModels myModels;
    private DataManager dataManager;

    public SceneChanger() {
        myModels = new MyModels();
        dataManager = new DataManager();
    }

    /**
     * Method to created the main menu scene
     *
     * @param primaryStage stahge we are currently in
     */
    public void mainMenu(Stage primaryStage) throws SQLException {
        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();
        myModels.setMyModels(myModels.getModels());

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(menuInicial(primaryStage));

        Scene scene = new Scene(painel, 300, 200);

        primaryStage.setTitle("Model Creator");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Method to create the center layout of the main menu scene
     *
     * @param primaryStage the stage we are currently in
     * @return a vbox with the layout
     */
    public VBox menuInicial(Stage primaryStage) {

        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 50, 50, 50));
        vb.setSpacing(25);

        Button visualizarModelos = visualizarModelosButton(primaryStage);
        Button preMadeModels = preMadeModelsButton(primaryStage);
        Button importScene = importSceneButton(primaryStage);

        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(visualizarModelos, preMadeModels, importScene);

        return vb;
    }

    /**
     * Method to create button to acess the MyModels scene
     *
     * @param primaryStage the stage we are currently in
     * @return button
     */
    public Button visualizarModelosButton(Stage primaryStage) {
        MyModelsScene myModelsScene = new MyModelsScene();

        Button visualizarModelos = new Button("My Models");
        visualizarModelos.setOnAction(event -> {
                    try {
                        myModelsScene.myModels(primaryStage, myModels, dataManager);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );

        return visualizarModelos;
    }

    /**
     * Method to access the preMade models scene
     *
     * @param primaryStage
     * @return a button
     */
    public Button preMadeModelsButton(Stage primaryStage) {
        PreMadeScene preMadeScene = new PreMadeScene();

        Button preMadeModels = new Button("Create PreMade Models");
        preMadeModels.setOnAction(e -> {
            preMadeScene.createPreMadeScene(primaryStage, myModels);
        });

        return preMadeModels;
    }

    /**
     * Method to create the button to acess the import XML/XMI scene
     *
     * @param primaryStage the stage we are currently in
     * @return a button
     */
    public Button importSceneButton(Stage primaryStage) {
        {
            Button importSceneButton = new Button("Import From File");
            ImportScene importScene = new ImportScene();
            importSceneButton.setOnAction(e -> {
                importScene.importScene(primaryStage, myModels);
            });

            return importSceneButton;
        }
    }
}
