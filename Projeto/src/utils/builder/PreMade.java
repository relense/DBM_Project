package utils.builder;

import javafx.scene.layout.VBox;
import metamodels.Attribute;
import metamodels.Class;
import metamodels.Model;

import java.util.List;
import java.util.Scanner;

/**
 * Created by Miguel on 02/05/2017.
 */
public class PreMade {

    private Builder builder;

    public PreMade() {

        this.builder = new Builder();

    }

    /**
     * Method to get a model for a person
     * @return a model
     */
    public Model getPersonModel() {
        Model model = new Model("Person");

        Class person = new Class("Person", "Person");
        person.addAttribute(new Attribute("name", "String", true));
        person.addAttribute(new Attribute("age", "int", true));

        model.addClass(person);
        model.setClasses(builder.makeRelations(model));

        return model;
    }

    /**
     * Method to get a bookStore model
     * @return a model
     */
    public Model getBookStoreModel() {
        Model model = new Model("BookStore");

        Class author = new Class("Author", "BookStore");
        author.addAttribute(new Attribute("first_name", "String", true));
        author.addAttribute(new Attribute("last_name", "String"));
        author.addAttribute(new Attribute("email", "String"));

        Class book = new Class("Book", "BookStore");
        book.addAttribute(new Attribute("title", "String"));
        book.addAttribute(new Attribute("pubDate", "Date"));
        book.addAttribute(new Attribute("price", "double"));
        book.addAttribute(new Attribute("quantity", "int"));
        book.addRelations("author", "1To1");

        model.addClass(author);
        model.addClass(book);
        model.setClasses(builder.makeRelations(model));

        return model;
    }

}
