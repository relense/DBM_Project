package Scene;

import Menu.BarMenu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import metamodels.Attribute;
import metamodels.Class;
import MyModels.MyModels;
import MyModels.DataManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Relense on 02/05/2017.
 */
public class NewCreateClassScene {

    /**
     * Method to create the Scene to create Classes
     *
     * @param name model name
     * @return Scene the create class scene
     */
    public Scene createSceneClasses(String name, MyModels myModels, DataManager dataManager, ListView<String> classList, ObservableList<String> items) {
        Stage primaryStage = new Stage();

        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(classesSceneLayout(name, primaryStage, myModels, dataManager, classList, items));

        Scene scene = new Scene(painel, 850, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        return scene;

    }

    /**
     * Method to create a scene to add a class to our model
     * @param modelName the name of the model
     * @param primaryStage the stage we currently are
     * @param myModels our list of models
     * @param dataManager the access to the dabase and manipulation
     * @param classList a ListView of type class that we can manipulate and get info from the selected option
     * @param items the items in the classList
     * @return a scrollpane
     */
    public ScrollPane classesSceneLayout(String modelName, Stage primaryStage, MyModels myModels, DataManager dataManager, ListView<String> classList, ObservableList<String> items) {

        List<VBox> listVBox = new ArrayList<>();
        VBox vb = new VBox();
        List<Attribute> attributes = new ArrayList<>(); //Guardar os atributos de cada class

        ScrollPane sp = new ScrollPane(vb);
        VBox vb2 = new VBox();
        //Hbox para prencher o nome da classe
        HBox hbox = new HBox();

        Label className = new Label("Class Name: ");
        TextField classNameTextField = new TextField();

        //Create AttributeLayout
        VBox vb3 = createAttributeLayout(attributes);
        hbox.getChildren().addAll(className, classNameTextField);

        vb2.getChildren().addAll(hbox, vb3);
        vb2.setPadding(new Insets(10, 50, 10, 10));

        listVBox.add(vb2);

        for (int i = 0; i < listVBox.size(); i++) {
            vb.getChildren().add(listVBox.get(i));
        }

        HBox submitBox = createSubmitLayout(primaryStage, classNameTextField, modelName, attributes, dataManager, myModels, classList, items);

        vb.getChildren().add(submitBox);

        return sp;
    }

    /**
     * Method to create the add attribute layout
     * @param attributes list of attributes
     * @return a VBox
     */
    public VBox createAttributeLayout(List<Attribute> attributes) {

        //Vbox para preencher os attributos
        VBox vb3 = new VBox();

        Label attribute = new Label("Attributes");

        //Hbox para guardar a Label "Name:", o respetivo textFied, a Label "Type" e respetivo TextField e uma vBox com o botão de adicionar e remover atributo
        HBox hb3 = new HBox();
        Label name = new Label("Name: ");
        TextField nameTextField = new TextField();
        Label type = new Label("Type: ");
        TextField typeTextField = new TextField();

        Label required = new Label("Required");
        ComboBox<String> requiredBox = new ComboBox<>();
        ObservableList<String> items2 = FXCollections.observableArrayList();
        items2.addAll("true", "false");
        requiredBox.setItems(items2);

        //Lista em que podemos ver os atributos que estão a ser adicionados ou removidos.
        ListView<String> list = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList();
        list.setItems(items);
        list.setPrefSize(150, 70);

        VBox vb4 = attributeLayoutButtons(nameTextField, typeTextField, requiredBox, items, attributes, list);

        hb3.getChildren().addAll(name, nameTextField, type, typeTextField, required, requiredBox, vb4, list);
        hb3.setSpacing(10);
        vb3.getChildren().addAll(attribute, hb3);

        return vb3;
    }

    /**
     * Method to create the layout of the to manage the attribute buttons
     * @param nameTextField the name of the attribute
     * @param typeTextField the type of the attribute
     * @param requiredBox if its required or not (true or false)
     * @param items the items in our ListView list
     * @param attributes a list of our attributes
     * @param list the ListView with our items
     * @return
     */
    public VBox attributeLayoutButtons(TextField nameTextField, TextField typeTextField, ComboBox<String> requiredBox, ObservableList<String> items, List<Attribute> attributes, ListView<String> list) {
        //VBox para o botão de adicionar e de apagar atributo
        VBox vb4 = new VBox();

        Button addButton = new Button("Add Attribute");
        addButton.setOnAction(e -> {

            if (validateType(typeTextField.getText()) && requiredBox.getSelectionModel().getSelectedItem() != null) {
                items.add(nameTextField.getText() + ";" + typeTextField.getText() + ";" + requiredBox.getSelectionModel().getSelectedItem());
                attributes.add(new Attribute(nameTextField.getText(), typeTextField.getText(), requiredBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("true") ? true : false));
                nameTextField.clear();
                typeTextField.clear();
            } else {
                System.out.println("Invalid Type");
            }
        });

        Button removeButton = new Button("Remove Attribute");
        removeButton.setOnAction(e -> {

            items.remove(list.getSelectionModel().getSelectedIndex());
            if(list.getSelectionModel().getSelectedIndex() == -1){
                attributes.remove(list.getSelectionModel().getSelectedIndex() + 1);

            }else{
                attributes.remove(list.getSelectionModel().getSelectedIndex());

            }
        });

        vb4.getChildren().addAll(addButton, removeButton);
        vb4.setSpacing(10);

        return vb4;
    }

    /**
     * Method to create the Layout for the submit/next and cancel buttons
     *
     * @param primaryStage the stage we are currently in
     * @param classNameTextField to get the name of the class
     * @param modelName the model name
     * @param attributes a list of the classes attributes
     * @return an HBox
     */
    public HBox createSubmitLayout(Stage primaryStage, TextField classNameTextField, String modelName, List<Attribute> attributes, DataManager dataManager, MyModels myModels, ListView<String> classList, ObservableList<String> items) {

        //Horizontal Box with the submit/next and cancel buttons
        HBox submitBox = new HBox();
        // Para usar o metodo createModel

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            primaryStage.close();
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            addClass(classNameTextField, modelName, attributes, myModels, dataManager, classList, items);

            primaryStage.close();
        });

        submitBox.getChildren().addAll(submitButton, cancelButton);


        submitBox.setPadding(new Insets(25, 50, 10, 10));
        submitBox.setSpacing(20);
        submitBox.setAlignment(Pos.CENTER);

        return submitBox;
    }

    /**
     * Method to add a class and its attributes
     *
     * @param classNameTextField class name
     * @param modelName          model name
     * @param attributes         list of atributes
     */
    public void addClass(TextField classNameTextField, String modelName, List<Attribute> attributes, MyModels myModels, DataManager dataManager, ListView<String> classList, ObservableList<String> items) {
        Class clazz = new Class(classNameTextField.getText(), modelName);
        clazz.setAttributes(attributes);
        int index = 0;

        for (int i = 0; i < myModels.getMyModels().size(); i++) {
            if (myModels.getMyModels().get(i).getName().equalsIgnoreCase(modelName)) {
                index = i;
            }
        }
        myModels.getMyModels().get(index).addClass(clazz);

        dataManager.saveClass(myModels.getMyModels().get(index));
        System.out.println("New Class " + classNameTextField.getText() + " pkg " + modelName);
        if(items.get(0).equalsIgnoreCase("No Class Available add")){
            items.clear();
        }
        items.add(clazz.getClassId() + " - " + clazz.getName());
        classList.setItems(items);
        classList.refresh();
    }

    /**
     * Method to validate the type of an Attribute
     *
     * @param type a string that represents the type of the attribute
     * @return true if valid, false if not
     */
    public boolean validateType(String type) {
        if (type.equals("String") || type.equals("double") || type.equals("Date") || type.equals("int") || type.equals("float")) {
            return true;
        }

        return false;
    }


}
