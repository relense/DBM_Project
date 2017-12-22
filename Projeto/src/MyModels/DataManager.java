package MyModels;

import javafx.scene.control.TextField;
import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;
import utils.sqlite.SQLiteConn;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miguel on 04/05/2017.
 */
public class DataManager {

    public DataManager() {

    }

    /**
     * Method to get all the atributes from a class
     *
     * @param modelName the name of the model
     * @param className the name of the class
     * @return list with the objects keept in the database regarding that class
     * @throws SQLException
     */
    public ArrayList<String> all(String modelName, String className) throws SQLException {

        return getResultSet("src/MyModels/" + modelName + "/tables.db", "SELECT * FROM " + className);
    }

    /**
     * Method to get an element by its id
     *
     * @param modelName the name of the model we are currently acessing
     * @param className the name of the class we are currently acessing
     * @param textField the textField were the user inserts the condition
     * @return the element
     * @throws SQLException
     */
    public String getElementById(String modelName, String className, TextField textField) throws SQLException {
        ArrayList<String> lista = new ArrayList<>();
        if(textField.getText().equalsIgnoreCase("")){
            return "No input to search";
        }
        if (textField.getText() != null || !textField.getText().isEmpty()) {
            lista = getResultSet("src/MyModels/" + modelName + "/tables.db", "SELECT * FROM " + className + " WHERE " + className.toLowerCase() + "Id = " + Integer.parseInt(textField.getText()));
        }

        if(lista.size() == 0){
            return "Nothing was found in the database";
        }

        return lista.get(0);

    }

    /**
     * Method to get a list of elements from a condition
     *
     * @param modelName the name of the model
     * @param className the name of the class
     * @param textField the textfield where the condition is
     * @return a list of elements
     * @throws SQLException
     */
    public List<String> getElementsByICondition(String modelName, String className, TextField textField) throws SQLException {
        ArrayList<String> lista = new ArrayList<>();

        if (textField.getText() != null || !textField.getText().isEmpty()) {
            lista = getResultSet("src/MyModels/" + modelName + "/tables.db", "SELECT * FROM " + className + " WHERE " + textField.getText());
        }

        return lista;
    }

    /**
     * Method to add an element to the database from a certain class
     *
     * @param modelName        the name of the model
     * @param className        the name of the class
     * @param objectAttributes the attributes of the class we want to save
     * @param listaValores     list of values that we want to save
     */
    public void save(String modelName, String className, ArrayList<Attribute> objectAttributes, List<String> listaValores) {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/" + modelName + "/tables.db");

        String query = "INSERT INTO " + className + "(";

        for (int i = 1; i < objectAttributes.size(); i++) {
            if (i == objectAttributes.size() - 1) {
                query += objectAttributes.get(i).getName() + ") ";
            } else {
                query += objectAttributes.get(i).getName() + ", ";
            }
        }

        query += "VALUES (";

        for (int j = 1; j < objectAttributes.size(); j++) {
            if (objectAttributes.get(j).getType().equalsIgnoreCase("TEXT")) {
                if (j == objectAttributes.size() - 1) {
                    query += "'" + listaValores.get(j - 1) + "');";
                } else {
                    query += "'" + listaValores.get(j - 1) + "',";
                }

            } else {
                if (j == objectAttributes.size() - 1) {
                    query += listaValores.get(j - 1) + ");";
                } else {
                    query += listaValores.get(j - 1) + " ,";
                }
            }
        }

        System.out.println(query);
        sqLiteConn.executeUpdate(query);

        sqLiteConn.close();
    }

    /**
     * Method to delete a model from the database
     *
     * @param index     the index of the model
     * @param modelName the name of the model
     */
    public void deleteModel(int index, String modelName) {
        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/tables.db");

        sqLiteConn.executeUpdate("DELETE FROM Model WHERE modeId = " + index);
        sqLiteConn.executeUpdate("DELETE FROM Class WHERE FKmodelId = " + index);

        sqLiteConn.close();

        deleteDirectory(new File("src/MyModels/" + modelName));
    }

    /**
     * Method to delete a class from the database and also the file.
     *
     * @param modelIndex      the index of the model we want to acess
     * @param classIndexDB    the index of the class in the database
     * @param classIndexLocal the index of the class localy
     * @param modelName       the name of the model we are currently acessing
     * @param className       the name of the class we are currently acessing
     * @param myModels        my models list so we can remove the classe localy
     */
    public void deleteClass(int modelIndex, int classIndexDB, int classIndexLocal, String modelName, String className, List<Model> myModels) {
        if (classIndexDB == -1 || classIndexLocal == -1) {
            return;
        }

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/tables.db");
        sqLiteConn.executeUpdate("DELETE FROM Class WHERE classId = " + classIndexDB);
        sqLiteConn.close();

        myModels.get(modelIndex).getClasses().remove(classIndexLocal);
        deleteFile(new File("src/MyModels/" + modelName + "/" + className + ".java"));

    }

    /**
     * Method to delete an attribute from the database
     *
     * @param modelName   the name of the model
     * @param className   the name of the class
     * @param attributeId the id of the attribute to delete
     */
    public void deleteAttribute(String modelName, String className, int attributeId) {
        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/" + modelName + "/tables.db");
        sqLiteConn.executeUpdate("DELETE FROM " + className + " WHERE " + className.toLowerCase() + "Id = " + attributeId);

        sqLiteConn.close();
    }

    /**
     * Method to delete a directory
     *
     * @param element the file with the path of what we want to delete
     */
    public static void deleteDirectory(File element) {
        if (element.isDirectory()) {
            for (File sub : element.listFiles()) {
                deleteDirectory(sub);
            }
        }
        element.delete();
    }

    /**
     * Method to delete a file
     *
     * @param element the file with the path of what we want to delete
     */
    public static void deleteFile(File element) {
        if (element.exists()) {
            element.delete();
        }
    }

    /**
     * Method to save a model and its classes in the database
     *
     * @param model model we want to save
     */
    public void saveModelWithClass(Model model) {
        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/tables.db");

        int id = sqLiteConn.executeUpdate("INSERT INTO Model (name) " +
                " VALUES ('" + model.getName() + "' );");

        model.setId(id);

        for (int i = 0; i < model.getClasses().size(); i++) {
            model.getClasses().get(i).setClassId(sqLiteConn.executeUpdate("INSERT INTO Class (name, FKmodelId) "
                    + " VALUES ('" + model.getClasses().get(i).getName() + "', " + model.getId() + " );"));

            if (!model.getClasses().get(i).getAttributes().isEmpty()) {
                for (int j = 0; j < model.getClasses().get(i).getAttributes().size(); j++) {
                    sqLiteConn.executeUpdate("INSERT INTO Attribute (name, type, FKClassId, required) "
                            + " VALUES ('" + model.getClasses().get(i).getAttributes().get(j).getName()
                            + "', '" + model.getClasses().get(i).getAttributes().get(j).getType()
                            + "', " + model.getClasses().get(i).getClassId()
                            + ", '" + model.getClasses().get(i).getAttributes().get(j).getRequired() + "');");

                }
                System.out.println("saving attributes");
            }
        }

        sqLiteConn.close();
    }

    /**
     * Method to save a model in the database
     *
     * @param model the model we want to save
     */
    public void saveModel(Model model) {
        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/tables.db");

        int id = sqLiteConn.executeUpdate("INSERT INTO Model (name) " +
                " VALUES ('" + model.getName() + "' );");

        model.setId(id);

        sqLiteConn.close();

    }

    /**
     * Method to save a classes from the model
     *
     * @param model the model we want to acess the classes
     */
    public void saveClass(Model model) {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/tables.db");

        for (int i = 0; i < model.getClasses().size(); i++) {
            model.getClasses().get(i).setClassId(sqLiteConn.executeUpdate("INSERT INTO Class (name, FKmodelId) "
                    + " VALUES ('" + model.getClasses().get(i).getName() + "', " + model.getId() + " );"));
        }
    }

    /**
     * Method to get an ArrayList of type Attribute form the database
     *
     * @param modelName the name of the model
     * @param className the name of the classe
     * @return a list of attributes
     * @throws SQLException
     */
    public ArrayList<Attribute> getColumnNumber(String modelName, String className) throws SQLException {
        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/" + modelName + "/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM " + className);
        ResultSetMetaData rsmd = rs.getMetaData();
        ArrayList<Attribute> list = new ArrayList<>();

        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String name = rsmd.getColumnName(i);
            String type = rsmd.getColumnTypeName(i);
            list.add(new Attribute(name, type));
        }

        sqLiteConn.close();
        return list;
    }

    /**
     * Method to return a List of Model from a filename and a query
     *
     * @param filename filename path
     * @param query    instructions to execute
     * @return a List<Model>
     * @throws SQLException
     */
    public List<Model> getModelResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
        ArrayList<Model> modelList = new ArrayList<>();

        if (rs.getMetaData().getColumnCount() == 0) {
            System.out.println("No Results");
            return null;
        }

        while (rs.next()) {
            Model model = new Model(rs.getString(rs.getMetaData().getColumnName(2)));
            model.setId(rs.getInt(rs.getMetaData().getColumnName(1)));

            modelList.add(model);
        }

        sqLiteConn.close();

        return modelList;
    }

    /**
     * Method to return a List of Class from a filename and a query
     *
     * @param filename filename path
     * @param query    instructions to execute
     * @return a List<Class>
     * @throws SQLException
     */
    public List<Class> getClassResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
        ArrayList<Class> classlList = new ArrayList<>();

        if (rs.getMetaData().getColumnCount() == 0) {
            System.out.println("No Results");
            return null;
        }

        while (rs.next()) {
            ArrayList<Attribute> attributesList = new ArrayList<>();
            //Na realidade está a sair a foreign key associada à class na tabela e não o nome do pkg(modelo_parent)
            Class clazz = new Class();

            ResultSet rs3 = sqLiteConn.executeQuery("SELECT * FROM Model WHERE modeId = " +  rs.getInt(rs.getMetaData().getColumnName(3)));
            while(rs3.next()){
               clazz = new Class(rs.getString(rs.getMetaData().getColumnName(2)), rs3.getString(rs3.getMetaData().getColumnName(2)));
            }
            clazz.setClassId(rs.getInt(rs.getMetaData().getColumnName(1)));
            System.out.println(clazz.getClassId());

            ResultSet rs2 = sqLiteConn.executeQuery("SELECT * FROM Attribute WHERE FKclassId = " + clazz.getClassId());
            while(rs2.next()){
                if(clazz.getClassId() == rs2.getInt(rs2.getMetaData().getColumnName(4))){
                attributesList.add(new Attribute(rs2.getString(rs2.getMetaData().getColumnName(2)), rs2.getString(rs2.getMetaData().getColumnName(3))));
             }
            }

            clazz.setAttributes(attributesList);

            classlList.add(clazz);
        }

        sqLiteConn.close();

        return classlList;
    }

    /**
     * Method to return a list of strings from a query.
     *
     * @param filename the name of the file we want to acess
     * @param query    the query we want to execute
     * @return return a list of strings with information depending on the query.
     * @throws SQLException
     */
    private ArrayList<String> getResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
        ArrayList<String> list = new ArrayList<>();

        if (rs.getMetaData().getColumnCount() == 0 || rs.getMetaData() == null) {
            System.out.println("No Results");
            return null;
        }

        while (rs.next()) {
            int info = rs.getInt(rs.getMetaData().getColumnName(1));
            String information = "" + info;
            for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
                information += " - " + rs.getString(rs.getMetaData().getColumnName(i + 1));
            }

            list.add(information);
        }

        sqLiteConn.close();
        return list;
    }

}
