package database;

import network.FileRecord;

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
     * creates table of the specific user with files data
     * @param login login of the specific user
     */
    public void createUserTable(String login) {
        Statement statement = null;

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "create table if not exists " + login + "('ID' integer primary key autoincrement,'FileName' text,'FilePath' text,'Size' text,'Checksum' text);";

        try {
            assert statement != null;
            statement.executeUpdate(sql);
            System.out.println("Created user database successfully");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        if(exist==true){
                createUserTable(username);
        }
        return exist;
    }

    /**
     * inserts into database information of the specific file that user has sent to the server
     * @returns file's ID from DB
     */
    public int insertFileData(String username, FileRecord fileRecord){

        try {
            String sql = "INSERT INTO "+username+" (FileName, FilePath, Size, Checksum) VALUES (?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, fileRecord.getName());
            preparedStatement.setString(2, fileRecord.getPath());
            preparedStatement.setLong(3, fileRecord.getSize());
            preparedStatement.setString(4, fileRecord.getChecksum());

            preparedStatement.execute();
            connection.commit();
            System.out.println("User data inserted successfully");


            sql = "SELECT id FROM "+username+" WHERE FileName=? AND FilePath=? AND Size=? AND Checksum=?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, fileRecord.getName());
            preparedStatement.setString(2, fileRecord.getPath());
            preparedStatement.setLong(3,fileRecord.getSize());
            preparedStatement.setString(4, fileRecord.getChecksum());
            ResultSet rs = preparedStatement.executeQuery();
            connection.commit();

            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * method that reads rows from the specific table
     * @param tableName table name to read from
     * @return vector of string data
     */
    public Vector<Vector<String>> getTableRows(String tableName){
        Vector<Vector<String>> rowsVector = new Vector<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + tableName +" ;");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnNumber = rsmd.getColumnCount();

            while (resultSet.next()){
                Vector<String> tmp = new Vector<>();
                for(int i =1;i<=columnNumber;i++){
                    tmp.add(resultSet.getString(i));
                }
                rowsVector.add(tmp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsVector;
    }

    /**
     * deletes specified rows from the specified log
     * @param tableName name of the table
     * @param ID id of the file to delete
     */
    public void deleteFile( String tableName, int ID){
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from " + tableName + " where ID='" + ID +"';");
            connection.commit();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * gets data of the file using table name and unique ID of this file
     * @param tableName table name of the specific user
     * @param ID unique ID of the file to get
     * @return table with file data
     */
    public String[] getFile(String tableName, int ID){
        String[] fileData = new String[5];
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + tableName +" where ID='"+ID+"';");


                for(int i =1;i<=5;i++) {
                    fileData[i-1] = (resultSet.getString(i));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fileData;
    }

}