package com.example.info445project;

import java.sql.*;

public class DBConnection {
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        Connection connection = DriverManager.getConnection(URL);
//        Statement stat = connection.createStatement();
//        String query = "Select * from Students";
//        ResultSet rs = stat.executeQuery(query);
//        while (rs.next()){
//            System.out.println(rs.getString(1)+rs.getString(2));


    public Connection conn;
    private String URL = "jdbc:sqlserver://(localdb)\\.;databaseName=info445_project;integratedSecurity=true";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
