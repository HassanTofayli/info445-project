package com.example.info445project;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;


public class Main extends Application {

    public static StringProperty stageTitle = new SimpleStringProperty();
    public static Object currentUser;
    public static Connection conn;

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



    public static void main(String[] args) throws SQLException {
        conn = DatabaseConnection.getConnection();
        launch();
    }


    public void initStageTitle(Stage stage){
        stage.titleProperty().bind(stageTitle);
        stageTitle.addListener((o, oldv, newv) ->{ System.out.println("newv: " + newv); });
    }

    public static void executeSampleQuery() {
        String query = "SELECT * FROM Institutions"; // Your SQL query here

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Assuming 'Institutions' has an 'InstitutionID' and 'Name' column
                int id = resultSet.getInt("InstitutionID");
                String name = resultSet.getString("Name");
                System.out.println("Institution ID: " + id + ", Name: " + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}