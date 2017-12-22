package MyModels;

import metamodels.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metamodels.Class;

/**
 * Created by Relense on 03/05/2017.
 */
public class MyModels {

    private List<Model> myModels;
    private DataManager dataManager;

        public MyModels() {
        myModels = new ArrayList<>();
        dataManager = new DataManager();
    }

    public List<Model> getMyModels() {
        return this.myModels;
    }

    public void setMyModels(List<Model> myModels) {
        this.myModels = myModels;
    }

    /**
     * Method to add a model to MyModels list and also in the database
     *
     * @param model the model we want to add
     */
    public void addModel(Model model) {
        this.myModels.add(model);
        if(model.getClasses().isEmpty()){
            this.dataManager.saveModel(model);
        }else{
            this.dataManager.saveModelWithClass(model);
        }
    }

    /**
     * Method to remove a model, this will call a method to remove from the database
     *
     * @param index
     * @param modelId   model id
     * @param modelName model name
     */
    public void removeModel(int index, int modelId, String modelName) {
        if (index != -1) {
            if (myModels.get(index) != null) {
                this.myModels.remove(index);
                dataManager.deleteModel(modelId, modelName);
            }
        } else {
            if (myModels.get(index + 1) != null) {
                this.myModels.remove(index + 1);
                dataManager.deleteModel(modelId, modelName);
            }
        }
    }

    /**
     * Method to get all the models from the database
     *
     * @return a list with models
     * @throws SQLException
     */
    public List<Model> allModels() throws SQLException {

        return dataManager.getModelResultSet("src/MyModels/tables.db", "SELECT * FROM Model");
    }

    /**
     * Method to get all the classes from the database
     *
     * @return a list with classes
     * @throws SQLException
     */
    public List<Class> allClasses() throws SQLException {

        return dataManager.getClassResultSet("src/MyModels/tables.db", "SELECT * FROM Class");
    }

    /**
     * Method to build a list of models
     *
     * @throws SQLException
     * @return a list of type Model
     */
    public List<Model> getModels() throws SQLException {

        List<Model> myModels = allModels();
        List<Class> myClasses = allClasses();


        for (int i = 0; i < myModels.size(); i++) {
            for (int j = 0; j < myClasses.size(); j++) {

                if (myClasses.get(j).getPkg().equalsIgnoreCase(myModels.get(i).getName())) {

                    myModels.get(i).addClass(myClasses.get(j));
                }
            }
        }

        return myModels;
    }

}
