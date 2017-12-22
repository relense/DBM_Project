package metamodels;

import com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the definintion of an object of type Model
 * It's used to represent a group of classes
 */
public class Model {
    private int modelId;
    private String name;
    private List<Class> classes;
    private HashMap<Class, Class> relation;

    public Model(String name) {
        this.name = name;
        this.classes = new ArrayList<>();
        this.relation = new HashMap<>();
    }

    public int getId() {
        return modelId;
    }

    public void setId(int modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public HashMap<Class, Class> getRelation() {
        return relation;
    }

    public void setRelation(HashMap<Class, Class> relation) {
        this.relation = relation;
    }

    public void addClass(Class clazz) {
        this.classes.add(clazz);
    }

    public void addRelation(Class clazz, Class clazz1) {
        this.relation.put(clazz, clazz1);
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", classes=" + classes +
                '}';
    }
}