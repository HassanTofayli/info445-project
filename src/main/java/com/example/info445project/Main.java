package com.example.info445project;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;


public class Main extends Application {

    public static StringProperty stageTitle = new SimpleStringProperty();
    public static Object currentUser;

    @Override
    public void start(Stage stage) {
        try {

            initStageTitle(stage);

            Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private static final String URL = "jdbc:sqlserver://localhost:1433;database=info445_project;integratedSecurity=false;user=testuser;password=test;encrypt=false;";

    public static void main(String[] args) {
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
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser("testuser");
        ds.setPassword("test");
        ds.setServerName("localhost");
        ds.setPortNumber(Integer.parseInt("1433"));
        ds.setDatabaseName("info445_project");
        ds.setEncrypt("false");

        try (Connection con = ds.getConnection();
             CallableStatement cstmt = con.prepareCall("{call dbo.uspGetEmployeeManagers(?)}");) {
            // Execute a stored procedure that returns some data.
            cstmt.setInt(1, 50);
            ResultSet rs = cstmt.executeQuery();

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                System.out.println("EMPLOYEE: " + rs.getString("LastName") + ", " + rs.getString("FirstName"));
                System.out.println("MANAGER: " + rs.getString("ManagerLastName") + ", " + rs.getString("ManagerFirstName"));
                System.out.println();
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        launch();
    }


    public void initStageTitle(Stage stage){
        stage.titleProperty().bind(stageTitle);
        stageTitle.addListener((o, oldv, newv) ->{ System.out.println("newv: " + newv); });
    }
}