package utils.builder;

import MyModels.DataManager;
import com.sun.org.apache.xpath.internal.SourceTree;
import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;
import metamodels.Relation;
import utils.sqlite.SQLiteConn;
import utils.transformations.Model2Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Miguel on 20/04/2017.
 */
public class Builder {

    public Builder() {

    }

    /**
     * Method that builds a model.
     * This means it will create every java file for each class.
     * Every xml file for each class and its respective database files.
     * @param model the Model we are building
     * @throws IOException
     */
    public void buildModel(Model model) throws IOException {

        DataManager dataManager = new DataManager();

        // Generate SQL tables
        Model2Text model2Text = new Model2Text("src/templates");
        String sqlTables = model2Text.render(model, "sqlite3_create.ftl");
        System.out.println(sqlTables);

        File file = new File("src/MyModels/" + model.getName());

        if (!file.exists()) {
          createFile(file, sqlTables, model, model2Text);

        }else{
            dataManager.deleteDirectory(file);
            createFile(file, sqlTables, model, model2Text);
        }
    }

    public void createFile(File file, String sqlTables, Model model, Model2Text model2Text){
        try {

            //Create Model directory, Package
            file.mkdirs();

            //Create SQL connection
            SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/" + model.getName() + "/tables.db");
            sqLiteConn.executeUpdate(sqlTables);
            sqLiteConn.close();

            if(model.getClasses() != null){
                //for each class in model, create java file
                for (Class clazz : model.getClasses()) {
                    createJavaFile(clazz, model, model2Text);

                }
            }

            //create the XML for the model
            createXML(model, model2Text);

        } catch (SecurityException se) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to create an XML file from a model.
     * @param model the model we are currently using to generate an xml file
     * @param model2Text object to make the transformation to text
     * @throws IOException
     */
    public void createXML(Model model, Model2Text model2Text) throws IOException {

        FileWriter fw;

        //Create XMLFolder
        File XMLfolder = new File("src/MyModels/" + model.getName() + "/XML");
        if (!XMLfolder.exists()) {
            XMLfolder.mkdirs();
        }

        //Create XMLFile
        File XMLFile = new File("src/MyModels/" + model.getName() + "/" + "XML/" + model.getName().toLowerCase() + ".xml");
        String xmlInfo = model2Text.render(model, "xml_create.ftl");

        fw = new FileWriter(XMLFile);
        fw.write(xmlInfo);
        fw.close();
    }

    /**
     * Method to create a class
     * @param model the current model
     * @param model2Text object to make the transformation to text
     * @throws IOException
     */
    public void createJavaFile(Class clazz, Model model, Model2Text model2Text) throws IOException {

        FileWriter fw;

        //Create Classes
        File javaClassFile = new File("src/MyModels/" + model.getName() + "/" + clazz.getName() + ".java");
        String classInfo = model2Text.render(clazz, "java_class.ftl");

        fw = new FileWriter(javaClassFile);
        if(fw != null){
            fw.write(classInfo);
        }

        fw.close();
    }

    /**
     * Method to make relations between classes.
     * This will be able to create 1To1, 1ToN and NToN relations with respetive fk.
     * @param model Model we are making the relations
     * @return the model with the relations
     */
    public List<Class> makeRelations(Model model) {

        List<Class> classList = model.getClasses();

        //Seach in every classes the relations they have
        for (int i = 0; i < classList.size(); i++) {
            //get the relation of the current class we are seaching and create a list of does relations
            if(classList.get(i).getRelations() == null || classList.get(i).getRelations().isEmpty()){
                continue;
            }else {

                List<Relation> relations = classList.get(i).getRelations();

                //Seach in the relations list for a relation and its type
                for (int j = 0; j < relations.size(); j++) {

                    // If the relations type is 1To1 we call the make1tTo1 method
                    if (relations.get(j).getType().equalsIgnoreCase("1To1")) {

                        make1To1(relations, classList, i, j);

                        // If the relations is 1ToN we call the make1ToN method
                    } else if (relations.get(j).getType().equalsIgnoreCase("1ToN")) {

                        make1ToN(relations, classList, i, j);

                        // If the relations is NToN we call the makeNToN method
                    } else if (relations.get(j).getType().equalsIgnoreCase("NToN")) { //Caso N para n

                        makeNToN(relations, classList, model, i, j);
                    }
                }
            }
        }

        return classList;
    }

    /**
     * Method to create a 1To1 Relation.
     * This method will add a fk to the main class = i, and to the class it has a relation with.
     * For this it will search the list of available classes and if its name its equal to the relations name it add a foreign key.
     * @param relations List of relations the main class = i has
     * @param classList List of classes there is in the model
     * @param i index of main class
     * @param j index of the relation we are looking in the list of relations from the main class
     */
    public void make1To1(List<Relation> relations, List<Class> classList, int i, int j) {

        //Add a foreign key to the main class with the name of the relation
        classList.get(i).addForeignKey(relations.get(j).getName().toLowerCase());

        //Search in the classList for class with the same name has the relation
        for (int h = 0; h < classList.size(); h++) {
            //When we find it, we add a foreign key we the name of the main class
            if (classList.get(h).getName().equalsIgnoreCase(relations.get(j).getName().toLowerCase())) {
                classList.get(h).addForeignKey(classList.get(i).getName().toLowerCase());
                classList.get(h).getHiddenKey().add(classList.get(i));
                classList.get(i).getHiddenKey().add(classList.get(h));
            }
        }
    }

    /**
     * Method to create a 1ToN Relation.
     * This method will add a fk to the class wich has the same name has the relation in the main class.
     * @param relations List of relations in the main class = i, were we are searching
     * @param classList List of classes from the model
     * @param i the index of the main class we are searching currently
     * @param j the index of the relation we are looking in the list of relations from the main class = i
     */
    public void make1ToN(List<Relation> relations, List<Class> classList, int i, int j) {

        //Search in the clasList for a class with the same name has the name in the relation.
        for (int k = 0; k < classList.size(); k++) {
            if (classList.get(k).getName().equalsIgnoreCase(relations.get(j).getName())) { //When we find it we add a foreign key
                classList.get(k).addForeignKey(classList.get(i).getName().toLowerCase());
                classList.get(k).getHiddenKey().add(classList.get(i));
                classList.get(i).getHiddenKey().add(classList.get(k));
            }
        }
    }

    /**
     * Method to create a NToN Relation and add it to the List of relations in our model.
     * This method will add a relation between the main class = i, and the class with the name equals to the relation = j, we are currently looking.
     * After we found the class we add the relation, and remove all relations that match the name of the main class = i
     * Finally we remove any fk that both classes might have between each other.
     * @param relations List of relations from the main class = i
     * @param classList List of classes from the model
     * @param model the model we are currently modifing, we need this to add a relation
     * @param i the index of the main class
     * @param j this index of the relation we are currently looking
     */
    public void makeNToN(List<Relation> relations, List<Class> classList, Model model, int i, int j) {

        //Search in the classList for a class with the same name has the name of the relation
        for (int k = 0; k < classList.size(); k++) {
            //If we find it, we add a relation to the model between the main class and the class we found
            if (classList.get(k).getName().equalsIgnoreCase(relations.get(j).getName())) {
                model.addRelation(classList.get(i), classList.get(k));

                //Remove all relations in the class we found with the main class, to prevent a relation to be built twice
                classList.get(k).resolveRelation(classList.get(i).getName().toLowerCase(), "1To1");
                classList.get(k).resolveRelation(classList.get(i).getName().toLowerCase(), "NToN");
                classList.get(k).resolveRelation(classList.get(i).getName().toLowerCase(), "1ToN");
            }

            //if both classes have foreign keys
            if (classList.get(k).getForeignKeys() != null || classList.get(i).getForeignKeys() != null) {
                //we seach in the class we found previous for a foreign key with the same name has the main class and remove it
                for (int l = 0; l < classList.get(k).getForeignKeys().size(); l++) {
                    if (classList.get(k).getForeignKeys().get(l).equalsIgnoreCase(classList.get(i).getName()))
                        classList.get(k).getForeignKeys().remove(l);
                }

                //We search in the main class for a foreign key with the same name has the class we found previously and remove it
                for (int p = 0; p < classList.get(i).getForeignKeys().size(); p++) {
                    if (classList.get(i).getForeignKeys().get(p).equalsIgnoreCase(classList.get(k).getName())) {
                        classList.get(i).getForeignKeys().remove(p);
                    }
                }
            }
        }
    }
}
