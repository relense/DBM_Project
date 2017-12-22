package Scene;

import Menu.BarMenu;
import MyModels.MyModels;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import utils.builder.Builder;
import utils.transformations.M2M;

import javax.xml.soap.Text;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Relense on 07/05/2017.
 */
public class ImportScene {

    public ImportScene() {

    }

    /**
     * Method to create the import Scene where we can choose an action. ImportXML or ImportXMI
     * @param primaryStage the stage we are currently in
     * @param myModels our list of models
     * @return a scene
     */
    public Scene importScene(Stage primaryStage, MyModels myModels){
        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(importLayout(myModels));

        Scene scene = new Scene(painel, 300, 200);
        primaryStage.setTitle("Import From XML or XMI");
        primaryStage.setScene(scene);
        primaryStage.show();

        return scene;
    }

    /**
     * Method to create the layout to choose wich action we want to make, importXML or importXMI.
     * @param myModels our list with all the models
     * @return a VBox with the layout
     */
    public VBox importLayout(MyModels myModels) {
        VBox vb = new VBox();

        Button importFromXML = importFromXMLButton(myModels);
        Button importFromXMI = importFromXMIButton(myModels);

        vb.getChildren().addAll(importFromXML, importFromXMI);
        vb.setPadding(new Insets(10, 50, 50, 50));
        vb.setSpacing(25);
        vb.setAlignment(Pos.CENTER);

        return vb;
    }

    /**
     * Method to create the button to import from the XML
     * @param myModels list of models
     * @return button to import from xml
     */
    public Button importFromXMLButton(MyModels myModels){
        Button importFromXMLButton = new Button("Import From XML");
        importFromXMLButton.setOnAction(e ->{
            importFromXMLScene(myModels, false);
        });

        return importFromXMLButton;
    }

    /**
     * Method to create the button to import from the XMI
     * @param myModels list of models
     * @return button to import from xmi
     */
    public Button importFromXMIButton(MyModels myModels){
        Button importFromXMIButton = new Button("Import From XMI");
        importFromXMIButton.setOnAction(e ->{
            importFromXMIScene(myModels, true);
        });

        return importFromXMIButton;
    }

    /**
     * Method to create the scene to insert the file path for the xml
     * @param myModels the list of all models
     * @param xmi boolean to know if the user wants to import xmi or xml
     */
    public void importFromXMLScene(MyModels myModels, boolean xmi){
        Stage primaryStage = new Stage();
        BorderPane painel = new BorderPane();

        painel.setCenter(insertFilePath(primaryStage, xmi, myModels));

        Scene scene = new Scene(painel, 350, 80);
        primaryStage.setTitle("Import From XML");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Method to create the scene to insert the fiel path
     * @param myModels the list of models
     * @param xmi boolean to know if the user wants to import xmi or xml
     */
    public void importFromXMIScene(MyModels myModels, boolean xmi){
        Stage primaryStage = new Stage();
        BorderPane painel = new BorderPane();

        painel.setCenter(insertFilePath(primaryStage, xmi, myModels));

        Scene scene = new Scene(painel, 350, 80);
        primaryStage.setTitle("Import From XMI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Method to create the layout to insert the path to find the xml/xmi file
     * @param primaryStage the stage we are currently in
     * @param xmi boolean that tells us if the user wants to import a xmi or xml
     * @param myModels the list of all models
     * @return and HBox with the needed to get the model from the path
     */
    public HBox insertFilePath(Stage primaryStage, boolean xmi, MyModels myModels){
        HBox hb = new HBox();

        Label filePathLabel = new Label("File Path: ");
        TextField filaPathTextField = new TextField();
        Button submitButotn = submitButton(primaryStage, filaPathTextField, xmi, myModels);

        hb.getChildren().addAll(filePathLabel, filaPathTextField, submitButotn);
        hb.setPadding(new Insets(10, 0, 50, 10));
        hb.setSpacing(25);

        return hb;
    }

    /**
     * Methodd to create the submit button.
     * This button will be used to create a model from a xmi or a xml
     * @param primaryStage stage were we are currently
     * @param filePathTextField the string with the path to our xml/xmi
     * @param xmi a boolean that tells us if we are currently importing a xmi or a xml
     * @param myModels to add the new model in the end
     * @return the submit button
     */
    public Button submitButton(Stage primaryStage, TextField filePathTextField, boolean xmi, MyModels myModels){
        Button submitButton = new Button("Submit");
        Builder builder = new Builder();
        submitButton.setOnAction(e ->{
            Model model;
            if(!xmi){
                model = M2M.getModel(filePathTextField.getText() + ".xml");

            }else{
                model = M2M.getXMIModel(filePathTextField.getText() + ".xmi");
            }
            try {
                builder.buildModel(model);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            myModels.addModel(model);
            primaryStage.close();
        });

        return submitButton;
    }
}
