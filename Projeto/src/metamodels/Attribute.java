package metamodels;

public class Attribute {
    private String name;
    private String type;
    private boolean required;

    public Attribute (String name, String type){
        this.name = name;
        this.type = type;
        this.required = false;
    }

    public Attribute(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
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

    public boolean getRequired(){ return required; }

    public void setRequired(boolean required){ this.required = required; }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
