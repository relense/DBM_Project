package metamodels;

/**
 * Created by Relense on 01/05/2017.
 */
public class Relation {

    private String name;
    private String type;

    public Relation (String name, String type){
        this.name = name;
        this.type = type;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
