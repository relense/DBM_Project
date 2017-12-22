package MyModels.BookStore;
import utils.sqlite.SQLiteConn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Author {
    private int authorId;
    private String first_name;
    private String last_name;
    private String email;
    private Book book;

    // Empty constructor
    public Author() {

    }

    /**
     *Constructor for required attributes only
     */
    public Author(String first_name){
         this.first_name = first_name;
    }

    /**
     *Constructor with all atributes and id
     */
    public Author(int authorId,String first_name, String last_name, String email) {
        this.authorId = authorId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.book = new Book();
    }

    public int getAuthorId(){
        return authorId;
    }

    public void setAuthorId(int authorId){
        this.authorId = authorId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Book getBook() {
         return book;
    }

    public void setBook(Book book) {
         this.book = book;
         saveBook();
    }

    /**
     * Method to create an Author in the database or update/save
     */
    public void save() {

         SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");

          if (this.authorId == 0){
           int id = sqLiteConn.executeUpdate("INSERT INTO Author(first_name, last_name, email) " +
                " VALUES ('" + this.first_name + "' , '" + this.last_name + "' , '" + this.email + "' );");

           this.authorId = id;

            }else{
                 sqLiteConn.executeUpdate("UPDATE Author SET " +
                 " First_name = '" + this.first_name + "' , " +
                 " Last_name = '" + this.last_name + "' , " +
                 " Email = '" + this.email + "' " +
                 " WHERE authorId =" + authorId);

            }

          sqLiteConn.close();
    }

    /**
     * Method that return a list of all the Authors that exist in the database
     * @return ArrayList of Authors
     * @throws SQLException
     */
    public static ArrayList<Author> all() throws SQLException {

        return getAuthorResultSet("src/MyModels/BookStore/tables.db","SELECT * FROM Author");
    }

    /**
     * Method get. Used to get a Author object from an id
     * @param authorId to get the author
     * @return a Author
     * @throws SQLException
     */
    public static Author get(int authorId) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM Author WHERE authorId = " + authorId);

        if (rs == null || rs.isClosed()) {
            System.out.println("No Author found, creating new Author");
            sqLiteConn.close();
            return new Author();

        }else{
            Author author = new Author();
            author.authorId = rs.getInt(rs.getMetaData().getColumnName(1));
            author.first_name = rs.getString(rs.getMetaData().getColumnName(2));
            author.last_name = rs.getString(rs.getMetaData().getColumnName(3));
            author.email = rs.getString(rs.getMetaData().getColumnName(4));

            sqLiteConn.close();
            return author ;
        }
    }

    /**
    * Method to get something specific from the database
    * @param condition sql expression to get a ResultSet
    * @return an ArrayList of Authors that match the condition searched
    * @throws SQLException
    */
    public static ArrayList<Author> where(String condition) throws SQLException {

        return getAuthorResultSet("src/MyModels/BookStore/tables.db", "SELECT * FROM Author WHERE " + condition);
    }

    /**
     * Method to delete a author from the database
     * @throws SQLException
     */
    public void delete() throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT COUNT(*) FROM Author WHERE authorId = " + this.authorId);

        if (rs.getMetaData().getColumnCount() == 1) {
            sqLiteConn.executeUpdate("DELETE FROM Author WHERE authorId = " + this.authorId);
            System.out.println("Success");

        } else {
            System.out.println("Doesnt Exist Author with that id");
            return;
        }

        sqLiteConn.close();
    }

    /**
     *Method to get the elements in case exists an 1 to N or N to N relation
     */
    public ArrayList<Book> getAllBook(int fk_authorId) throws SQLException{

        SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM Book WHERE FK_authorId = " + fk_authorId);
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
                book.getAuthor().setAuthorId(fk_authorId);

        bookList.add(book);

        }

        sqLiteConn.close();
        return bookList;
    }
     /**
     * Method to save information regarding the fk Book, this will save in the class Book the fk of the Author
     */
     public void saveBook() {

         SQLiteConn sqLiteConn = new SQLiteConn("src/MyModels/BookStore/tables.db");
          if(this.book.getBookId() == -1){
                     return;
                 }
                 sqLiteConn.executeUpdate("UPDATE Author SET " +
                                  " First_name = '" + this.first_name + "' , " +
                                  " Last_name = '" + this.last_name + "' , " +
                                  " Email = '" + this.email + "' , " +
                                  "Fk_bookId = " + this.book.getBookId() +
                                  " WHERE authorId =" + authorId);

                 sqLiteConn.executeUpdate("UPDATE Book SET " +
                                " Title = '" + this.book.getTitle() + "' , " +
                                " PubDate = " + this.book.getPubDate() + " , " +
                                " Price = " + this.book.getPrice() + " , " +
                                " Quantity = " + this.book.getQuantity() + " " +
                                ", FK_authorId = " + getAuthorId() +
                                " WHERE bookId =" + book.getBookId());

                 sqLiteConn.close();
     }


    private static ArrayList<Author> getAuthorResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
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
					  authorList.add(author);
                    }

        sqLiteConn.close();
        return authorList;
    }
}