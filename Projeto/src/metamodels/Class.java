package metamodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the definintion of an object of type Class
 */
public class Class {
    private int classId;
    private String name;
    private List<Attribute> attributes;
    private List<Attribute> required;
    private List<String> foreignKeys;
    private String pkg;
    private List<Relation> relations;
    private List<Class> hiddenKey;

    public Class(){
        this.name = "";
        this.attributes = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
        this.pkg = "";
        this.required = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.hiddenKey = new ArrayList<>();
    }

    public Class(String name, String pkg) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
        this.pkg = pkg;
        this.required = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.hiddenKey = new ArrayList<>();
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<String> foreignKeys) {

        this.foreignKeys = foreignKeys;
    }

    public String getPkg() {
        return this.pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public List<Attribute> getRequired() {
        return this.required;
    }

    public void setRequired(List<Attribute> required) {
        this.required = required;
    }

    public List<Relation> getRelations() {
        return this.relations;
    }

    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }

    public List<Class> getHiddenKey(){
        return this.hiddenKey;
    }

    public void setHiddenKey(List<Class> hiddenKey){
        this.hiddenKey = hiddenKey;
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);

        if(attribute.getRequired())
            this.required.add(attribute);
    }

    public void addForeignKey(String name) {
        this.foreignKeys.add(name);
    }

    /**
     * Method to remove a certain relation from the relations list
     * @param name the name of the relation
     * @param type the type of relation
     */
    public void resolveRelation(String name, String type){
        for(int i = 0; i < relations.size(); i++){
            if(relations.get(i).getName().equalsIgnoreCase(name) && relations.get(i).getType().equalsIgnoreCase(type)){
                System.out.println("Removed " + name + " with type " + type + " da " + this.name);
                relations.remove(i);
            }
        }
    }

    public String getForeignKey(int id){
        return this.foreignKeys.get(id);
    }

    public void addRelations(String name, String type){
            this.relations.add(new Relation(name, type));
    }

    @Override
    public String toString() {
        return "Class{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", foreignKeys=" + foreignKeys +
                ", pkg='" + pkg + '\'' +
                '}';
    }
}