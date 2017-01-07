import java.sql.*;
import java.util.Vector;

/**
 * Created by Marcin Jamroz on 15.11.2016.
 * handles communication with the database
 */
public class Database {

    /**
     * variable which is used to establish connection with database
     */
    private Connection connection = null;


    /**
     * opens connection to database
     */
    public void openDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:backuper.db");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Opened database successfully");
    }


    /**
     * creates table with users logins and passwords
     */
    public void createUsersTable() {
        Statement statement = null;

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "create table if not exists users('ID' integer primary key autoincrement,'Login' text,'Password' text,unique('Login'));";

        try {
            assert statement != null;
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Created login database successfully");

    }


    /**
     * handles process of registration of the user, adds user row to the logins and passwords table
     * @return return true if user has been successfully added or false if specific username is already in use
     */
    public boolean registerUser(String username, String password){
        Statement statement = null;
        ResultSet rs = null;
        boolean exist = false;
        try {
            statement = connection.createStatement();
            String checkQuery = "select Count(*) from users where Login='" + username + "'";
            rs = statement.executeQuery(checkQuery);
            if(rs.getInt(1)!=0) exist = false;
            else exist = true;
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(exist==true){
            try {
                statement = connection.createStatement();
                String sql = "insert into users(Login, Password) values('" + username + "','" + password + "');";
                statement.executeUpdate(sql);
                connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        return exist;
    }

    /**
     * handles logging of the user, creates user table if not exists
     * @return return true if user has been successfully logged or false if data is wrong
     */
    public boolean loginUser(String username, String password){
        Statement statement = null;
        ResultSet rs = null;
        boolean exist = false;
        try {
            statement = connection.createStatement();
            String checkQuery = "select Count(*) from users where Login='" + username + "' and Password='" + password + "';";
            rs = statement.executeQuery(checkQuery);
            if(rs.getInt(1)==0) exist = false;
            else exist = true;
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exist;
    }
}