package MyModels.BookStore;
import java.util.Date;
import utils.sqlite.SQLiteConn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Book {
    private int bookId;
    private String title;
    private Date pubDate;
    private double price;
    private int quantity;
    private Author author;

    // Empty constructor
    public Book() {

    }

    /**
     *Constructor with all atributes and id
     */
    public Book(int bookId,String title, Date pubDate, double price, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.pubDate = pubDate;
        this.price = price;
        this.quantity = quantity;
        this.author = new Author();
    }

    public int getBookId(){
        return bookId;
    }

    public void setBookId(int bookId){
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Author getAuthor() {
         return author;
    }

    public void setAuthor(Author author) {
         this.author = author;
         saveAuthor();
    }

    /**
     * Method to create an Book in the database or update/save
     */
    public void save() {

         SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");

          if (this.bookId == 0){
           int id = sqLiteConn.executeUpdate("INSERT INTO Book(title, pubDate, price, quantity) " +
                " VALUES ('" + this.title + "' , " + this.pubDate + " , " + this.price + " , " + this.quantity + " );");

           this.bookId = id;

            }else{
                 sqLiteConn.executeUpdate("UPDATE Book SET " +
                 " Title = '" + this.title + "' , " +
                 " PubDate = " + this.pubDate + " , " +
                 " Price = " + this.price + " , " +
                 " Quantity = " + this.quantity + " " +
                 " WHERE bookId =" + bookId);

            }

          sqLiteConn.close();
    }

    /**
     * Method that return a list of all the Books that exist in the database
     * @return ArrayList of Books
     * @throws SQLException
     */
    public static ArrayList<Book> all() throws SQLException {

        return getBookResultSet("src/MyModels/BookStore/tables.db","SELECT * FROM Book");
    }

    /**
     * Method get. Used to get a Book object from an id
     * @param bookId to get the book
     * @return a Book
     * @throws SQLException
     */
    public static Book get(int bookId) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM Book WHERE bookId = " + bookId);

        if (rs == null || rs.isClosed()) {
            System.out.println("No Book found, creating new Book");
            sqLiteConn.close();
            return new Book();

        }else{
            Book book = new Book();
            book.bookId = rs.getInt(rs.getMetaData().getColumnName(1));
            book.title = rs.getString(rs.getMetaData().getColumnName(2));
            book.pubDate = rs.getDate(rs.getMetaData().getColumnName(3));
            book.price = rs.getDouble(rs.getMetaData().getColumnName(4));
            book.quantity = rs.getInt(rs.getMetaData().getColumnName(5));

            sqLiteConn.close();
            return book ;
        }
    }

    /**
    * Method to get something specific from the database
    * @param condition sql expression to get a ResultSet
    * @return an ArrayList of Books that match the condition searched
    * @throws SQLException
    */
    public static ArrayList<Book> where(String condition) throws SQLException {

        return getBookResultSet("src/MyModels/BookStore/tables.db", "SELECT * FROM Book WHERE " + condition);
    }

    /**
     * Method to delete a book from the database
     * @throws SQLException
     */
    public void delete() throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT COUNT(*) FROM Book WHERE bookId = " + this.bookId);

        if (rs.getMetaData().getColumnCount() == 1) {
            sqLiteConn.executeUpdate("DELETE FROM Book WHERE bookId = " + this.bookId);
            System.out.println("Success");

        } else {
            System.out.println("Doesnt Exist Book with that id");
            return;
        }

        sqLiteConn.close();
    }

    /**
     *Method to get the elements in case exists an 1 to N or N to N relation
     */
    public ArrayList<Author> getAllAuthor(int fk_bookId) throws SQLException{

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM Author WHERE FK_bookId = " + fk_bookId);
        ArrayList<Author> authorList = new ArrayList<>();

        if (rs.getMetaData().getColumnCount() == 0) {
            System.out.println("No Results");
            return null;
        }

        while (rs.next()) {
                Author author = new Author(rs.getInt(rs.getMetaData().getColumnName(1)),
                rs.getString(rs.getMetaData().getColumnName(2)),
                                 rs.getString(rs.getMetaData().getColumnName(3)),
                                 rs.getString(rs.getMetaData().getColumnName(4)));
                author.getBook().setBookId(fk_bookId);

        authorList.add(author);

        }

        sqLiteConn.close();
        return authorList;
    }
     /**
     * Method to save information regarding the fk Author, this will save in the class Book the fk of the Book
     */
     public void saveAuthor() {

         SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
          if(this.author.getAuthorId() == -1){
                     return;
                 }
                 sqLiteConn.executeUpdate("UPDATE Book SET " +
                                  " Title = '" + this.title + "' , " +
                                  " PubDate = " + this.pubDate + " , " +
                                  " Price = " + this.price + " , " +
                                  " Quantity = " + this.quantity + " , " +
                                  "Fk_authorId = " + this.author.getAuthorId() +
                                  " WHERE bookId =" + bookId);

                 sqLiteConn.executeUpdate("UPDATE Author SET " +
                                " First_name = '" + this.author.getFirst_name() + "' , " +
                                " Last_name = '" + this.author.getLast_name() + "' , " +
                                " Email = '" + this.author.getEmail() + "' " +
                                ", FK_bookId = " + getBookId() +
                                " WHERE authorId =" + author.getAuthorId());

                 sqLiteConn.close();
     }


    private static ArrayList<Book> getBookResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
        ArrayList<Book> bookList = new ArrayList<>();

               if (rs.getMetaData().getColumnCount() == 0) {
                    System.out.println("No Results");
                    return null;
               }

               while (rs.next()) {
                      Book book = new Book(rs.getInt(rs.getMetaData().getColumnName(1)), 
                      rs.getString(rs.getMetaData().getColumnName(2)),
                                            rs.getDate(rs.getMetaData().getColumnName(3)),
                                            rs.getDouble(rs.getMetaData().getColumnName(4)),
                                            rs.getInt(rs.getMetaData().getColumnName(5)));
					  bookList.add(book);
                    }

        sqLiteConn.close();
        return bookList;
    }
}