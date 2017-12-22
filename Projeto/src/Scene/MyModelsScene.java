package Scene;

import Menu.BarMenu;
import MyModels.MyModels;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;
import MyModels.DataManager;
import metamodels.Relation;
import utils.builder.Builder;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Relense on 02/05/2017.
 */
public class MyModelsScene {

    /**
     *
     * Scenes
     *
     */

    /**
     * Method to get to the Scene to visualize Models
     * @param primaryStage stage were we have the current scene and we are going to change for another scene
     * @return a Scene
     */
    public Scene myModels(Stage primaryStage, MyModels myModels, DataManager dataManager) throws SQLException {
        BorderPane painel = new BorderPane();
        BarMenu barMenu = new BarMenu();

        painel.setTop(barMenu.PainelComMenu(primaryStage));
        painel.setCenter(myModelsScene(myModels, dataManager));

        Scene scene = new Scene(painel, 850, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        return scene;

    }

    /**
     * Create a scene to add a relation
     * @param myModels our list of models
     * @param modelsList the ListView with the selected item from the models list
     * @param classList the ListView with the selected item from the classList
     */
    public void createAddRelationScene(MyModels myModels, ListView<String> modelsList, ListView<String> classList){
        Stage primaryStage = new Stage();
        BorderPane painel = new BorderPane();

        painel.setCenter(addRelationBox(primaryStage, myModels, modelsList, classList));

        Scene scene = new Scene(painel, 450, 100);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     *
     * Layout
     *
     */

    /**
     * Method that return the layout of MyModels Scene
     * @param myModels our list of models
     * @return a vbox with the layout
     */
    public VBox myModelsScene(MyModels myModels, DataManager dataManager) {
        VBox vb = new VBox();

        ListView<String> modelsList = new ListView<>();
        modelsList.setPrefSize(150, 80);

        ListView<String> classList = new ListView<>();
        classList.setPrefSize(150, 100);

        ListView<String> searchList = new ListView<>();
        searchList.setPrefSize(300, 100);


        //View Model and Class, add objects
        HBox hb = modelClassField(myModels, modelsList, classList, searchList, dataManager);

        //Search Field
        HBox hb2 = searchField(modelsList, classList, dataManager, searchList);


        vb.getChildren().addAll(hb, hb2);

        return vb;
    }

    /**
     * Method to call the new window to add an atribute to a specific class
     * @param modelName name of the model
     * @param className name of the class we want to add an attribute
     * @return a Vbox with the layout for the attribute Scene
     * @throws SQLException
     */
    public VBox addAttributeScene(String modelName, String className, Stage primaryStage) throws SQLException {
        VBox vb = new VBox();
        DataManager dataManager = new DataManager();

        ArrayList<Attribute> lista = dataManager.getColumnNumber(modelName, className);
        List<TextField> textList = new ArrayList<>();

        for (int i = 1; i < lista.size(); i++) {
            HBox hb1 = new HBox();
            Label label = new Label(lista.get(i).getName().toString());
            TextField textField = new TextField();
            textList.add(textField);

            hb1.getChildren().addAll(label, textField);
            hb1.setPadding(new Insets(10, 10, 10, 10));
            hb1.setSpacing(15);
            vb.getChildren().add(hb1);
        }

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {

            List<String> listaValores = new ArrayList<>();
            for (int j = 0; j < textList.size(); j++) {
                if (textList.get(j).getText().equalsIgnoreCase("int")
                        || textList.get(j).getText().equalsIgnoreCase("String")
                        || textList.get(j).getText().equalsIgnoreCase("double")
                        || textList.get(j).getText().equalsIgnoreCase("Class")
                        || textList.get(j).getText().equalsIgnoreCase("model")) {
                    return;
                }
                listaValores.add(textList.get(j).getText());
            }
            dataManager.save(modelName, className, lista, listaValores);
            primaryStage.close();
        });

        vb.getChildren().add(submit);

        return vb;
    }

    /**Method to create the layout to add a relation
     * @param primaryStage the stage we are current in
     * @param myModels our list of models
     * @param modelsList the ListView with the selected item from the models list
     * @param classList the ListView with the selected item from the classList
     * @return hbox with the layout
     */
    public HBox addRelationBox(Stage primaryStage, MyModels myModels, ListView<String> modelsList, ListView<String> classList){
        HBox hb = new HBox();
        Label classRelation = new Label("Available Classes: ");
        ComboBox<String> classesBox = new ComboBox<>();
        ObservableList<String> items2 = FXCollections.observableArrayList();

        Label relationType = new Label("Relation Type: ");
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "1To1",
                "1ToN",
                "NToN"
        ));

        String info = classList.getSelectionModel().getSelectedItem();
        String[] parts = info.split(" - ");

        int index;
        if(modelsList.getSelectionModel().getSelectedIndex() == -1){
             index = modelsList.getSelectionModel().getSelectedIndex()  + 1;
        }else{
            index = modelsList.getSelectionModel().getSelectedIndex();
        }

        for(int i = 0; i < myModels.getMyModels().get(index).getClasses().size(); i++){
            if(!myModels.getMyModels().get(index).getClasses().get(i).getName().equalsIgnoreCase(parts[1])){
                items2.addAll(myModels.getMyModels().get(index).getClasses().get(i).getName());
            }
        }
        classesBox.setItems(items2);

        Button submitRelationButton = submitRelationButton(primaryStage, myModels, modelsList, classList, classesBox, typeBox);

        hb.getChildren().addAll(classRelation, classesBox, relationType, typeBox, submitRelationButton);
        hb.setPadding(new Insets(10, 0, 10, 10));
        hb.setSpacing(15);

        return hb;
    }

    /**
     * Method to define the layout the main view of the MyModelsScene.
     * In this scene we can see wich models exists, acess it's classes and add/remove
     * @param myModels our list of models
     * @param modelsList the ListView with the selected item from the models list
     * @param classList the ListView with the selected item from the classList
     * @return a Hbox with the layout
     */
    public HBox modelClassField(MyModels myModels, ListView<String> modelsList, ListView<String> classList, ListView<String> searchList, DataManager dataManager) {
        HBox hb = new HBox();
        hb.setPadding(new Insets(10, 50, 10, 50));
        hb.setSpacing(25);

        Label modelLabel = new Label("Models: ");
        Label classLabel = new Label("Classes: ");

        ObservableList<String> items = FXCollections.observableArrayList();

        for (int j = 0; j < myModels.getMyModels().size(); j++) {
            items.add(myModels.getMyModels().get(j).getId() + " - " + myModels.getMyModels().get(j).getName());
        }

        modelsList.setItems(items);

        ObservableList<String> items2 = FXCollections.observableArrayList();

        //This allows the buttons Open Model and Delete Model appear verticaly
        VBox modelButtons = new VBox();
        Button openModelButton = openModelButton(modelsList, items2, myModels, classList);
        Button addModelButton = createModelButton(modelsList, items, myModels);
        Button removeModelButton = removeModelButton(modelsList, items, items2, myModels);
        Button buildModelButton = buildModelButton(modelsList, classList, items2, myModels);
        modelButtons.getChildren().addAll(openModelButton, addModelButton, removeModelButton, buildModelButton);
        modelButtons.setPadding(new Insets(10, 10, 10, 10));
        modelButtons.setSpacing(15);

        VBox classButtons = new VBox();
        Button createClassButton = createClassButton(modelsList, myModels, dataManager, classList, items2);
        Button deleteButton = deleteClassButton(myModels, items2, modelsList, classList, searchList, dataManager);
        Button addRelationButton = addRelationButton(myModels, modelsList, classList);
        classButtons.getChildren().addAll(createClassButton, deleteButton, addRelationButton);
        classButtons.setPadding(new Insets(10, 10, 10, 10));
        classButtons.setSpacing(15);

        hb.getChildren().addAll(modelLabel, modelsList, modelButtons, classLabel, classList, classButtons);
        hb.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        return hb;
    }

    /**
     * Method to create the search field layout
     * @param modelsList to acess the current model we are viewing
     * @param classList  to acess the current class we are viewing
     * @param dataManager   to acess the item of the search list we are currently viewing
     * @return and HBox with the layout of the seach field
     */
    public HBox searchField(ListView<String> modelsList, ListView<String> classList, DataManager dataManager, ListView<String> searchList) {
        HBox hb = new HBox();

        Label searchBarLabel = new Label("Search Bar: ");
        TextField textFieldSearch = new TextField();

        Label searchResultLabel = new Label("Search Result:");
        ObservableList<String> items3 = FXCollections.observableArrayList();

        VBox searchPlaceBox = new VBox();

        HBox searchBox = new HBox();
        searchBox.getChildren().addAll(searchBarLabel, textFieldSearch);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.setSpacing(15);

        VBox searchButtonBox = new VBox();
        Button getElementById = getElementByIdButton(modelsList, classList, dataManager, searchList, textFieldSearch, items3);
        Button getElementsWithConditionButton = getElementsWithConditionButton(modelsList, classList, dataManager, searchList, textFieldSearch, items3);
        searchButtonBox.getChildren().addAll(getElementById, getElementsWithConditionButton);
        searchButtonBox.setSpacing(15);

        searchPlaceBox.getChildren().addAll(searchBox, searchButtonBox);
        searchPlaceBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");

        VBox vbButtons = new VBox();
        Button getAllAttributesButton = getAllAttributesButton(items3, modelsList, classList, dataManager, searchList);
        Button addAttributeButton = addAttributeButton(modelsList, classList, searchList);
        Button deleteAttributeButton = deleteAttributeButton(items3, modelsList, classList, dataManager, searchList);

        vbButtons.getChildren().addAll(getAllAttributesButton, addAttributeButton, deleteAttributeButton);
        vbButtons.setPadding(new Insets(10, 0, 10, 0));
        vbButtons.setSpacing(15);

        hb.getChildren().addAll(searchPlaceBox, searchResultLabel, searchList, vbButtons);
        hb.setPadding(new Insets(10, 0, 10, 50));
        hb.setSpacing(15);


        return hb;
    }


    /**
     *
     * Buttons
     *
     */


    /**
     * Method to open a model and see the existing classes
     * @param modelsList the ListView with the selected item from the models list
     * @param items2 items in the classList
     * @param myModels our list of models
     * @param classList the ListView with the selected item from the classList
     * @return the ListView with the selected item from the classList
     */
    public Button openModelButton(ListView<String> modelsList, ObservableList<String> items2, MyModels myModels, ListView<String> classList) {
        Button openButton = new Button("Open Model");
        openButton.setOnAction(e -> {

            if (modelsList.getSelectionModel().getSelectedItem() == null) {
                System.out.println("modelsList.getSlectionmodel().getSelectedItem == null");
                return;
            }

            items2.clear();

            List<Model> myModels2 = myModels.getMyModels();
            int index = modelsList.getSelectionModel().getSelectedIndex();
            if(myModels2.get(index).getClasses().size() != 0) {

                for (int k = 0; k < myModels2.get(index).getClasses().size(); k++) {
                    int classId = myModels2.get(index).getClasses().get(k).getClassId();

                    try {
                        if(myModels.getModels().get(index).getClasses().get(k).getRelations() == null){
                            items2.add(classId + " - " + myModels.getMyModels().get(index).getClasses().get(k).getName());
                        }else{
                            String relations = "";
                            for(int j = 0; j < myModels.getModels().get(index).getClasses().get(k).getRelations().size(); j++){
                                String name = myModels.getModels().get(index).getClasses().get(k).getRelations().get(j).getName();
                                String type = myModels.getModels().get(index).getClasses().get(k).getRelations().get(j).getType();
                                relations += name + "_" + type + " - ";
                            }
                            items2.add(classId + " - " + myModels.getMyModels().get(index).getClasses().get(k).getName() + " - " + relations);

                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                classList.setItems(items2);
            }else{
                items2.add("No Class Available add");
                classList.setItems(items2);
            }
        });

        return openButton;
    }

    /**
     * Method to create the button to acess the scene to create a new model
     * @return a button
     */
    public Button createModelButton(ListView<String> modelsList, ObservableList<String> items, MyModels myModels) {
        NewModelScene newModelScene = new NewModelScene();

        Button criarModel = new Button("Create Model");
        criarModel.setOnAction(event ->

                newModelScene.createNewModelScene(modelsList, items, myModels)
        );

        return criarModel;
    }

    /**
     * Method to remove a Model
     * @param modelsList the ListView with the selected item from the models list
     * @param items the items from the model
     * @param myModels our model list
     * @return a button
     */
    public Button removeModelButton(ListView<String> modelsList, ObservableList<String> items, ObservableList<String> items2, MyModels myModels) {
        Button removeButton = new Button("Delete Model");
        removeButton.setOnAction(e -> {

            String indexName = modelsList.getSelectionModel().getSelectedItem();
            String[] parts = indexName.split(" - ");

            items.remove(modelsList.getSelectionModel().getSelectedItem());
            items2.clear();

            myModels.removeModel(modelsList.getSelectionModel().getSelectedIndex(), Integer.parseInt(parts[0]), parts[1]);
        });

        return removeButton;
    }

    /**
     * Method to build the model, classes and relations
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @param items the items we have currently in the classeList
     * @param myModels my list of models
     * @return a button
     */
    public Button buildModelButton(ListView<String> modelsList, ListView<String> classList, ObservableList<String> items, MyModels myModels){
        Button builModelButton = new Button("Build Model");
        builModelButton.setOnAction(e ->{
            Builder builder = new Builder();
            if(modelsList.getSelectionModel().getSelectedItem() == null){
                items.clear();
                items.add("No Model Selected");
                classList.setItems(items);
                classList.refresh();
                return;
            }

            int index = modelsList.getSelectionModel().getSelectedIndex();

            if(!myModels.getMyModels().get(index).getClasses().isEmpty()){
                try {
                    builder.makeRelations(myModels.getModels().get(index));
                    builder.buildModel(myModels.getMyModels().get(index));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }else{
                items.clear();
                items.add("Need classes to build");
                classList.setItems(items);
                classList.refresh();
            }

        });

        return builModelButton;
    }

    /**
     * Method to create a button to create a class
     * @param modelsList the ListView with the selected item from the models list
     * @param myModels our list of models
     * @param dataManager acess to the datbase
     * @param classList the ListView with the selected item from the classes list
     * @param items2 the items in the classList
     * @return a button
     */
    public Button createClassButton(ListView<String> modelsList, MyModels myModels, DataManager dataManager, ListView<String> classList, ObservableList<String> items2){
        Button createClassButton = new Button("Add Class");
        NewCreateClassScene newCreateClassScene = new NewCreateClassScene();

        createClassButton.setOnAction(e ->{
            String info = modelsList.getSelectionModel().getSelectedItem();
            if(modelsList.getSelectionModel().getSelectedItem() == null){
                return;
            }
            String[] parts = info.split(" - ");

            newCreateClassScene.createSceneClasses(parts[1], myModels, dataManager, classList, items2);
        });

        return createClassButton;
    }

    /**
     * Button to delete either a class
     * @param modelsList the ListView with the selected item from the models list
     * @param classList the ListView with the selected item from the class list
     * @param searchList the ListView with the selected item from the search list
     * @param dataManager our acess to the database
     * @return a button
     */
    public Button deleteClassButton(MyModels myModels, ObservableList<String> items, ListView<String> modelsList, ListView<String> classList, ListView<String> searchList, DataManager dataManager) {
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            List<String> lista = getSelectionModel(modelsList, classList, searchList);

            String indexName = classList.getSelectionModel().getSelectedItem();
            String[] parts2 = indexName.split(" - ");//diz-me o index da class - Nome

            int classIndex = classList.getSelectionModel().getSelectedIndex();
            int modelIndex = modelsList.getSelectionModel().getSelectedIndex();

            if (parts2 == null) {
                return;
            }

            items.remove(classList.getSelectionModel().getSelectedItem());

            dataManager.deleteClass(modelIndex, Integer.parseInt(parts2[0]), classIndex, lista.get(0), lista.get(1), myModels.getMyModels());


        });

        return deleteButton;
    }

    /**
     * Method to create the addRelation Button in the class Field
     * @param myModels my list of models
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @return a button
     */
    public Button addRelationButton(MyModels myModels, ListView<String> modelsList, ListView<String> classList){
        Button addRelationButton = new Button("Add Relation");
        addRelationButton.setOnAction(e ->{

            createAddRelationScene(myModels, modelsList, classList);

        });

        return addRelationButton;
    }

    /**
     * Method to create the button to submit the relation
     * @param primaryStage the stage we are currently in
     * @param myModels my list of models
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @param classBox a comboBox with the available classes to add relation
     * @param typeBox a combox to choose the type of relation we want
     * @return a button
     */
    public Button submitRelationButton(Stage primaryStage, MyModels myModels, ListView<String> modelsList, ListView<String> classList, ComboBox<String> classBox, ComboBox<String> typeBox){
        Button submitRelationButton = new Button("Submit");

        submitRelationButton.setOnAction(e ->{

            int indexModel = modelsList.getSelectionModel().getSelectedIndex();
            int indexClass = classList.getSelectionModel().getSelectedIndex();

            String relationName = classBox.getSelectionModel().getSelectedItem().toString();
            String relationType = typeBox.getSelectionModel().getSelectedItem().toString();

            myModels.getMyModels().get(indexModel).getClasses().get(indexClass).addRelations(relationName.toLowerCase(), relationType);

            Builder builder = new Builder();
            builder.makeRelations(myModels.getMyModels().get(indexModel));

            primaryStage.close();
        });

        return submitRelationButton;
    }

    /**
     * Method to get all the information in the database regarding a choosen class
     * @param items3 the items we are including in the searchlist
     * @param modelsList to get information of what model we are currently acessing
     * @param classList to get information of what class we want to acess
     * @param dataManager to acess database
     * @param searchList ListView<String> with the info we need
     * @return a button
     */
    public Button getAllAttributesButton(ObservableList<String> items3, ListView<String> modelsList, ListView<String> classList, DataManager dataManager, ListView<String> searchList) {

        Button searchButton = new Button("Get All");
        searchButton.setOnAction(e -> {

            List<String> lista = getSelectionModel(modelsList, classList, searchList);
            items3.clear();

            try {
                ArrayList<String> listaString = dataManager.all(lista.get(0), lista.get(1));
                if (listaString.size() != 0) {
                    System.out.println(listaString.get(0));
                    for (int j = 0; j < listaString.size(); j++) {
                        items3.add(listaString.get(j));
                    }

                } else {
                    items3.add("Nothing was found");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            searchList.setItems(items3);
        });

        return searchButton;
    }

    /**
     * Method to add a button to create an objet of a selected class
     * @param modelsList ListView with models name. We need this to acess modelId and name
     * @param classList ListView with classes name. We need this to acess classId and name
     * @return a button
     */
    public Button addAttributeButton(ListView<String> modelsList, ListView<String> classList, ListView<String> searchList) {

        Button addAttributeButton = new Button("Add Attribute");
        addAttributeButton.setOnAction(e -> {
            Stage primaryStage = new Stage();
            VBox vb1 = new VBox();

            List<String> lista = getSelectionModel(modelsList, classList, searchList);

            try {
                vb1 = addAttributeScene(lista.get(0), lista.get(1), primaryStage);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            ScrollPane sp = new ScrollPane(vb1);
            sp.setFitToHeight(true);
            BorderPane painel = new BorderPane(sp);
            painel.setCenter(sp);

            Scene scene = new Scene(painel, 300, 150);

            primaryStage.setTitle("Model Creator");
            primaryStage.setScene(scene);
            primaryStage.show();
        });

        return addAttributeButton;
    }

    /**
     * Method to create a button to delete an attribute
     * @param items3     the list of items in the seachList
     * @param modelsList to get the name of the current model
     * @param classList  to get the name and id of the current classList
     * @param dataManager   to call the deleteAttribute method
     * @param searchList to find the index of the select attribute
     * @return a button
     */
    public Button deleteAttributeButton(ObservableList<String> items3, ListView<String> modelsList, ListView<String> classList, DataManager dataManager, ListView<String> searchList) {
        Button deleteAttributeButton = new Button("Delete Attribute");
        deleteAttributeButton.setOnAction(e -> {

            List<String> lista = getSelectionModel(modelsList, classList, searchList);

            String description = searchList.getSelectionModel().getSelectedItem();
            String[] parts = description.split(" - ");

            dataManager.deleteAttribute(lista.get(0), lista.get(1), Integer.parseInt(parts[0]));
            items3.remove(searchList.getSelectionModel().getSelectedIndex());
        });

        return deleteAttributeButton;
    }

    /**
     * Method to create a button to get an element from an id
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @param dataManager Object that allows us to access the database
     * @param searchList ListView to acess the id and name of the searchList
     * @param textField to get the index of the element
     * @param items the items in the searchList
     * @return a button
     */
    public Button getElementByIdButton(ListView<String> modelsList, ListView<String> classList, DataManager dataManager, ListView<String> searchList, TextField textField, ObservableList<String> items){
        Button getElement = new Button("Get Element by id");
        getElement.setOnAction(e ->{
            items.clear();
            List<String> lista = getSelectionModel(modelsList, classList, searchList);

            try {
                if(!lista.isEmpty()){
                    String element = dataManager.getElementById(lista.get(0), lista.get(1), textField);
                    items.add(element);
                }else{
                    items.add("Nothing was found");
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            searchList.setItems(items);
        });

        return getElement;
    }

    /**
     * Method to get 1 or more elements from a condition from a class
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @param dataManager Object to access the database
     * @param searchList ListView to acess the id and name of the searchList
     * @param textField the condition we need to access the database
     * @param items the items in the searchList to show the user an answer
     * @return a button
     */
    public Button getElementsWithConditionButton(ListView<String> modelsList, ListView<String> classList, DataManager dataManager, ListView<String> searchList, TextField textField, ObservableList<String> items){
        Button getElementsWithConditionButton = new Button("Get from condition");
        getElementsWithConditionButton.setOnAction(e ->{
            items.clear();
            List<String> lista = getSelectionModel(modelsList, classList, searchList);

            try {
                if(!lista.isEmpty()){
                    List<String> elements = dataManager.getElementsByICondition(lista.get(0), lista.get(1), textField);
                    items.addAll(elements);
                }else{
                    items.add("Nothing was found");
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            searchList.setItems(items);

        });

        return getElementsWithConditionButton;
    }

    /**
     *
     * Util
     *
     */

    /**
     * Method to acess what the current user is trying to view.
     * First we get the modelsList, classList and searchList.
     * We this we will get each item that the user has current selected.
     * @param modelsList ListView to acess the id and name of the model
     * @param classList ListView to acess the id and name of the class
     * @param searchList ListView to acess the id and name of the searchlist
     * @return a list of strings
     */
    public List<String> getSelectionModel(ListView<String> modelsList, ListView<String> classList, ListView<String> searchList) {
        List<String> lista = new ArrayList<>();

        if (modelsList.getSelectionModel().getSelectedItem() != null) ;
        {
            String modelName = modelsList.getSelectionModel().getSelectedItem();
            String[] parts1 = modelName.split(" - ");
            lista.add(parts1[1]);
        }

        if (classList.getSelectionModel().getSelectedItem() != null) {
            String indexName = classList.getSelectionModel().getSelectedItem();
            String[] parts2 = indexName.split(" - ");//diz-me o index da class - Nome
            lista.add(parts2[1]);
        }

        if (searchList.getSelectionModel().getSelectedItem() != null) ;
        {
            String attributeIndex = searchList.getSelectionModel().getSelectedItem();
            if (attributeIndex != null && attributeIndex != "") {
                String[] parts3 = attributeIndex.split(" - ");
                lista.add(parts3[0]);
            }

        }

        return lista;
    }

}
