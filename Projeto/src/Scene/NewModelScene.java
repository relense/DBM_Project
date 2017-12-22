package Scene;

import Menu.BarMenu;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import MyModels.DataManager;
import metamodels.Model;
import MyModels.MyModels;
import utils.builder.Builder;

import java.io.IOException;

/**
 * Created by Relense on 02/05/2017.
 */
public class NewModelScene {

    /**
     * Method to create the scene to create a new model
     * @return the scene to create a new model
     */
    public Scene createNewModelScene(ListView<String> modelsList, ObservableList<String> items, MyModels myModels) {

        Stage primaryStage = new Stage();
        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(newModelLayout(primaryStage, modelsList, items, myModels));

        Scene scene = new Scene(painel, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        return scene;

    }

    /**
     * Method to create the layout of the create new model Scene
     * @return a VBox with the layout
     */
    public VBox newModelLayout(Stage primaryStage, ListView<String> modelsList, ObservableList<String> items, MyModels myModels) {

        VBox vb = new VBox();
        HBox hb = new HBox();

        Label modelName = new Label("Model Name: ");
        TextField textField = new TextField();

        hb.getChildren().addAll(modelName, textField);
        hb.setPadding(new Insets(10, 50, 10, 10));
        hb.setSpacing(10);

        HBox hb3 = new HBox();

        Button submitButton = submitButton(primaryStage, textField, modelsList, items, myModels);

        hb3.getChildren().addAll(submitButton);
        hb3.setPadding(new Insets(10, 50, 10, 10));
        hb3.setSpacing(10);

        vb.getChildren().addAll(hb, hb3);

        return vb;
    }

    /**
     * Method to create the button submit
     * @param textField to get the name of the model
     * @return a button
     */
    public Button submitButton(Stage primaryStage, TextField textField, ListView<String> modelsList, ObservableList<String> items, MyModels myModels){
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {

            if (textField.getText() != null && !textField.getText().isEmpty()){

                String name = textField.getText();
                Model model = new Model(name);
                myModels.addModel(model);

                items.add(model.getId() + " - " + name);
                modelsList.refresh();
                primaryStage.close();

                //newCreateClassScene.createSceneClasses(nClasses, name, primaryStage, 0, classList, myModels);

            } else {
                System.out.println("Incomplete");
            }
        });

        return submitButton;
    }
}
