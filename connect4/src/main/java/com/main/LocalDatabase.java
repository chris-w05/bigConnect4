package com.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalDatabase {
    //users\\chris\\Connect5\\Connect4\\src\\
    private static final String url = "jdbc:sqlite:connect4/src/main/java/com/main/local_database.db";



    public static void test(){
        try{
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url)) {
                // Create a table (if not exists)
                displayData(connection);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        catch( ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    
    public static Connection connect(){
        try{
            Connection connection = DriverManager.getConnection(url);
            return connection;
        }
        catch( Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void testAdd(){
        try (Connection connection = DriverManager.getConnection(url)) {
            
            insertData(connection, (short)1, "test", 0.0f);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertData(Connection connection, short color, String hash, double winchance) throws SQLException {
        String table= "states_negative";
        if( color > 0){
            table = "states_positive";
        }
        
        String insertDataSQL = "INSERT INTO " + table + " (hash, winchance) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertDataSQL)) {
            statement.setString(1, hash);
            statement.setDouble(2, winchance);
            statement.executeUpdate();
        }
    }

    public static double checkForValue( Connection connection, short color, String hash) throws SQLException{
        String table;
        if( color == 1){
            table = "states_positive";
        }
        else{
            table = "states_negative";
        }
        String selectDataSQL = "SELECT hash FROM " + table + " WHERE hash LIKE '" + hash + "'";
        try (PreparedStatement statement = connection.prepareStatement(selectDataSQL)){
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getDouble( "winchance");
        }
    }

    public static void deleteData(Connection connection, String hash) throws SQLException{
        String deleteDataSQL = " DELETE FROM states where hash LIKE '" + hash + "'";
        try (PreparedStatement statement = connection.prepareStatement(deleteDataSQL)) {
            statement.executeUpdate();
        }
    }

    public static void displayData(Connection connection) throws SQLException {
        String selectDataSQL = "SELECT * FROM states";
        try (PreparedStatement statement = connection.prepareStatement(selectDataSQL);
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("hash\twinchance");
            while (resultSet.next()) {
                String hash = resultSet.getString("hash");
                double winchance = resultSet.getDouble("winchance");
                System.out.println(hash + "\t" + winchance );
            }
        }
    }

    public static void clearTable(String table) throws SQLException{
        try (Connection connection = DriverManager.getConnection(url)) {
            String deleteDataSQL = " DELETE FROM " + table;
            try (PreparedStatement statement = connection.prepareStatement(deleteDataSQL)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

