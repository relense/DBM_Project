package ${pkg};
<#assign uses_date = false>
<#assign uses_list = false>
<#list attributes as attribute>
    <#if attribute.type == "Date" && uses_date == false>
        <#assign uses_date = true>
        <#lt>import java.util.Date;
    </#if>
    <#if attribute.type?starts_with("List") && uses_list == false>
            <#assign uses_list = true>
            <#lt>import java.util.List;
        </#if>
</#list>
import utils.sqlite.SQLiteConn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ${name} {
    private int ${name?lower_case}Id;
    <#list attributes as attribute>
    private ${attribute.type} ${attribute.name};
    </#list>
    <#list foreignKeys as fk>
    private ${fk?capitalize} ${fk?lower_case};
    </#list>

    // Empty constructor
    public ${name}() {

    }
<#assign rqbool><#list required as rq>${rq.type} ${rq.name}<#sep>, </#list></#assign>
    <#if rqbool?has_content>

    /**
     *Constructor for required attributes only
     */
    public ${name}(${rqbool}){
         <#list attributes as attribute>
         <#if attribute.required == true>
         this.${attribute.name} = ${attribute.name};
         </#if>
         </#list>
    }
    </#if>

    /**
     *Constructor with all atributes and id
     */
    private ${name}(int ${name?lower_case}Id, <#list attributes as attribute>${attribute.type} ${attribute.name}<#sep>, </#list><#list foreignKeys as fk>, ${fk?capitalize} ${fk}</#list>) {
        this.${name?lower_case}Id = ${name?lower_case}Id;
        <#list attributes as attribute>
        this.${attribute.name} = ${attribute.name};
        </#list>
        <#list foreignKeys as fk>
        this.${fk} = ${fk};
        </#list>
    }

    public int get${name}Id(){
        return ${name?lower_case}Id;
    }

    public void set${name}Id(int ${name?lower_case}Id){
        this.${name?lower_case}Id = ${name?lower_case}Id;
    }

    <#list attributes as attribute>
    <#-- Getter -->
    public ${attribute.type} get${attribute.name?cap_first}() {
        return ${attribute.name};
    }

    <#-- Setter -->
    public void set${attribute.name?cap_first}(${attribute.type} ${attribute.name}) {
        this.${attribute.name} = ${attribute.name};
    }

    </#list>
    <#list foreignKeys as fk>
    <#-- Getter -->
    public ${fk?capitalize} get${fk?cap_first}() {
         return ${fk};
    }

    <#-- Setter -->
    public void set${fk?cap_first}(${fk?capitalize} ${fk}) {
         this.${fk} = ${fk};
    }

    </#list>

    /**
     * Method to create an ${name} in the database or update/save
     */
    public void save() {

         SQLiteConn sqLiteConn = new SQLiteConn("src/Person/tables.db");

          if (this.${name?lower_case}Id == 0){
           int id = sqLiteConn.executeUpdate("INSERT INTO ${name}(<#list attributes as attribute>${attribute.name}<#sep>, </#list>) " +
                " VALUES (<@compress single_line=true><#list attributes as attribute>
                <#if attribute.type == "String">'" + this.${attribute.name} + "'
                <#else>" + this.${attribute.name} + "
                </#if><#sep>,</#list>);");</@compress>

           this.${name?lower_case}Id = id;

          }else
            sqLiteConn.executeUpdate("UPDATE ${name} SET " +
            <#list attributes as attribute>
            <@compress single_line=true>" ${attribute.name?cap_first} =
                <#if attribute.type == "String">'" + this.${attribute.name} + "'
                <#else>" + this.${attribute.name} + "
                </#if><#sep> , </@compress> " +
            </#list>
            " WHERE ${name?lower_case}Id =" + ${name?lower_case}Id);

          sqLiteConn.close();
    }


    /**
     * Method that return a list of all the ${name}s that exist in the database
     * @return ArrayList of ${name}s
     * @throws SQLException
     */
    public static ArrayList<${name}> all() throws SQLException {

        return get${name}ResultSet("src/${parent}/tables.db","SELECT * FROM ${name}");
    }

    /**
     * Method get. Used to get a ${name} object from an id
     * @param ${name?lower_case}Id to get the ${name?lower_case}
     * @return a ${name}
     * @throws SQLException
     */
    public static ${name} get(int ${name?lower_case}Id) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/${name}/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT * FROM ${name} WHERE ${name?lower_case}Id = " + ${name?lower_case}Id);

        if (rs == null || rs.isClosed()) {
            System.out.println("No ${name} found, creating new ${name}");
            sqLiteConn.close();
            return new ${name}();

        }else{
            ${name} ${name?lower_case} = new ${name}();
            ${name?lower_case}.${name?lower_case}Id = rs.getInt(rs.getMetaData().getColumnName(1));
            <#list attributes as attribute>
            ${name?lower_case}.${attribute.name} = <#compress>
            <#if attribute.type == "String">rs.getString(rs.getMetaData().getColumnName(${attribute?index + 2}));
            <#elseif attribute.type == "int">rs.getInt(rs.getMetaData().getColumnName(${attribute?index + 2}));
            <#elseif attribute.type == "Date">rs.getDate(rs.getMetaData().getColumnName(${attribute?index + 2}));
            <#elseif attribute.type == "double">rs.getDouble(rs.getMetaData().getColumnName(${attribute?index + 2}));</#if></#compress>
            </#list>

            System.out.println("<#list attributes as attribute>${attribute.name?cap_first}: " + ${name?lower_case}.${attribute.name} <#sep> +
                                " |  </#list>);

            sqLiteConn.close();
            return ${name?lower_case} ;
        }
    }

    /**
    * Method to get something specific from the database
    * @param condition sql expression to get a ResultSet
    * @return an ArrayList of ${name}s that match the condition searched
    * @throws SQLException
    */
    public static ArrayList<${name}> where(String condition) throws SQLException {

        return get${name}ResultSet("src/${parent}/tables.db", "SELECT * FROM ${name} WHERE " + condition);
    }

    /**
     * Method to delete a ${name?lower_case} from the database
     * @throws SQLException
     */
    public void delete() throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn("src/${name}/tables.db");
        ResultSet rs = sqLiteConn.executeQuery("SELECT COUNT(*) FROM ${name} WHERE ${name?lower_case}Id = " + this.${name?lower_case}Id);

        if (rs.getMetaData().getColumnCount() == 1) {
            sqLiteConn.executeUpdate("DELETE FROM ${name} WHERE ${name?lower_case}Id = " + this.${name?lower_case}Id);
            System.out.println("Success");

        } else {
            System.out.println("Doesnt Exist ${name} with that id");
            return;
        }

        sqLiteConn.close();
    }

    private static ArrayList<${name}> get${name}ResultSet(String filename, String query) throws SQLException {

        SQLiteConn sqLiteConn = new SQLiteConn(filename);
        ResultSet rs = sqLiteConn.executeQuery(query);
        ArrayList<${name}> ${name?lower_case}List = new ArrayList<>();

               if (rs.getMetaData().getColumnCount() == 0) {
                    System.out.println("No Results");
                    return null;
               }

               while (rs.next()) {
                     ${name?lower_case}List.add(new ${name}(rs.getInt(rs.getMetaData().getColumnName(1)),
                 <#assign i>
                      <#list attributes as attribute>
                      <#compress><#if attribute.type == "String">rs.getString(rs.getMetaData().getColumnName(${attribute?index + 2}))
                      <#elseif attribute.type == "int">rs.getInt(rs.getMetaData().getColumnName(${attribute?index + 2}))
                      <#elseif attribute.type == "Date">rs.getDate(rs.getMetaData().getColumnName(${attribute?index + 2}))
                      <#elseif attribute.type == "double">rs.getDouble(rs.getMetaData().getColumnName(${attribute?index + 2}))
                      </#if></#compress><#sep>,
                      </#list>
                      </#assign>
                      ${i}<#list foreignKeys as fk>,
                      <#compress>(${fk?capitalize}) rs.getObject(${fk?index + i?word_list?size + 2})</#compress><#sep>,
                      </#list>));
                    }

        sqLiteConn.close();
        return ${name?lower_case}List;
    }
}