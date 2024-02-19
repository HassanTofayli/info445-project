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













//    private static final String URL = "jdbc:sqlserver://localhost:1433;database=info445_project;integratedSecurity=false;user=testuser;password=test;encrypt=false;";

//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
//            Connection connection = DriverManager.getConnection(URL);
//            Statement stat = connection.createStatement();
//            String query = "Select * from Students";
//            ResultSet rs = stat.executeQuery(query);
//            while (rs.next()){
//                System.out.println(rs.getString(1)+rs.getString(2));
//            }
//
//
//        } catch (ClassNotFoundException | SQLException e) {
//            System.out.println(e.getMessage());
//        }
// Create datasource.
//    SQLServerDataSource ds = new SQLServerDataSource();
//        ds.setUser("testuser");
//                ds.setPassword("test");
//                ds.setServerName("localhost");
//                ds.setPortNumber(Integer.parseInt("1433"));
//                ds.setDatabaseName("info445_project");
//                ds.setEncrypt("false");
//
//                try (Connection con = ds.getConnection();
//                CallableStatement cstmt = con.prepareCall("{call dbo.uspGetEmployeeManagers(?)}");) {
//                // Execute a stored procedure that returns some data.
//                cstmt.setInt(1, 50);
//                ResultSet rs = cstmt.executeQuery();
//
//                // Iterate through the data in the result set and display it.
//                while (rs.next()) {
//                  System.out.println("EMPLOYEE: " + rs.getString("LastName") + ", " + rs.getString("FirstName"));
//                  System.out.println("MANAGER: " + rs.getString("ManagerLastName") + ", " + rs.getString("ManagerFirstName"));
//                  System.out.println();
//                }
//                }
//                // Handle any errors that may have occurred.
//                catch (SQLException e) {
//                System.out.println(e.getMessage());
//                }