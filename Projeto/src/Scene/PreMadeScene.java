package Scene;

import Menu.BarMenu;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import metamodels.Model;
import utils.builder.Builder;
import utils.builder.PreMade;

import java.io.IOException;

import MyModels.MyModels;
import MyModels.DataManager;

/**
 * Created by Relense on 02/05/2017.
 */
public class PreMadeScene {

    /**
     * Method that creates a Scene for the preMadeScene.
     * This scene allows the user to choose a preMade model
     *
     * @param primaryStage the stage we are currently in
     */
    public void createPreMadeScene(Stage primaryStage, MyModels myModels) {
        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(preMadeSceneLayout(myModels));

        Scene scene = new Scene(painel, 300, 200);

        primaryStage.setTitle("Model Creator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Method to create a VBox that is the layour of the preMade Scene
     * @return a vbox for the center layout of the pre made models scene
     */
    public VBox preMadeSceneLayout(MyModels myModels) {
        Builder builder = new Builder();
        PreMade preMade = new PreMade();

        VBox vb = new VBox();
        vb.setPadding(new Insets(10, 50, 50, 50));
        vb.setSpacing(25);

        Button createPersonModel = createPersonButton(preMade, builder, myModels);
        Button createBookStoreModel = createBookStoreButton(preMade, builder, myModels);

        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(createPersonModel, createBookStoreModel);
        return vb;
    }

    /**
     * Method to create the button to create a book store model
     * @param preMade to get the premade model
     * @param builder to build the model
     * @param myModels to add the new model to the list of models
     * @return the button to create a BookStore Model
     */
    public Button createBookStoreButton(PreMade preMade, Builder builder, MyModels myModels){
        Button createBookStoreButton = new Button("Create BookStore Model");
        createBookStoreButton.setOnAction(event -> {
            Model model2 = preMade.getBookStoreModel();
            try {
                builder.buildModel(model2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            myModels.addModel(model2);

        });

        return createBookStoreButton;
    }

    /**
     * Method that return a button to create a Person model
     * @param preMade to acess the premade model and create it
     * @param builder to build the model
     * @param myModels to add the new model to the list of models
     * @return a button to create a person model
     */
    public Button createPersonButton(PreMade preMade, Builder builder, MyModels myModels){
        Button creaPersonButton = new Button("Create Person Model");
        creaPersonButton.setOnAction(event -> {
            Model model = preMade.getPersonModel();
            myModels.addModel(model);

            try {
                builder.buildModel(model);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        return creaPersonButton;
    }
}
