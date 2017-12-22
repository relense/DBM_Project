package sample;

import MyModels.BookStore.Author;
import MyModels.BookStore.Book;
//import MyModels.Person.Person;
import javafx.application.Application;
import javafx.stage.Stage;
import Scene.SceneChanger;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application {

    SceneChanger sceneChanger = new SceneChanger();

    @Override
    public void start(Stage primaryStage) throws SQLException {

        sceneChanger.mainMenu(primaryStage);

    }

    public static void main(String[] args) throws SQLException {
        launch(args);

        testORM1To1();
        testORM1ToN();
    }

    public static void testORM1To1() throws SQLException {

        // Criar novas pessoas
        System.out.println("Criei o João");
        Author author = new Author("João");
        author.save();

        System.out.println("criei um novo livro - Algo");
        Book book = new Book();
        book.setTitle("Algo");
        book.setQuantity(2);
        book.save();

        System.out.println("criei um novo livro - Livro 2");
        Book book2 = new Book();
        book2.setTitle("Livro 2");
        book2.setQuantity(1);
        book2.save();
        System.out.println("ID = " + book.getBookId());

        System.out.println("Adicionei os livros ao author com o id = " + author.getAuthorId());
        author.setBook(book);
        author.setBook(book2);

        System.out.println("---GetAllBooks---");
        ArrayList<Book> booksList = Book.all();
        for (Book b : booksList) {
            System.out.println("Id = " + b.getBookId() + " Title = " + b.getTitle() + " Quantity = " + b.getQuantity() + " Author Id = " + b.getAuthor().getAuthorId());
        }

        System.out.println("");
        System.out.println("---GetAllBooks by Author With id = " + author.getAuthorId() + "---");
        ArrayList<Book> books = author.getAllBook(author.getAuthorId());
        for (Book b : books) {
            System.out.println(b.getBookId() + " - " + b.getTitle() + " - " + b.getQuantity() + " Author Id = " + b.getAuthor().getAuthorId());
        }

    }

    public static void testORM1ToN() throws SQLException {
/*
        // Criar novos autores
        System.out.println("Criei o João");
        Author author = new Author("João");
        author.save();

        Author author2 = new Author("Manuel");
        author2.save();

        System.out.println("criei um novo livro - Algo");
        Book book = new Book();
        book.setTitle("Algo");
        book.setQuantity(2);
        book.save();

        System.out.println("criei um novo livro - Livro 2");
        Book book2 = new Book();
        book2.setTitle("Livro 2");
        book2.setQuantity(1);
        book2.save();

        System.out.println("Adicionei os livros ao author com o id = " + author.getAuthorId());
        author.setBook(book);
        author2.setBook(book2);

        System.out.println("---GetAllBooks---");
        ArrayList<Book> booksList = Book.all();
        for (Book b : booksList) {
            System.out.println("Id = " + b.getBookId() + " Title = " + b.getTitle() + " Quantity = " + b.getQuantity());
        }

        System.out.println("---GetAllAuthors---");
        ArrayList<Author> authorList = Author.all();
        for (Author a : authorList) {
            System.out.println("AuthorId: " + a.getAuthorId() + "| First_name: " + a.getFirst_name()+ "| LastName: " + a.getLast_name());
        }

        System.out.println("");
        System.out.println("---GetAllAuthors by Book With id = " + author.getAuthorId() + "---");
        ArrayList<Author> authorList2 = book.getAllAuthor(author.getAuthorId());
        for (Author a : authorList2) {
            System.out.println(a.getAuthorId() + " - " + a.getFirst_name()+ " - " + a.getBook().getBookId());
       }
*/
    }

    }
