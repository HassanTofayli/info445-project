package com.example.info445project;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


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

    public static void main(String[] args) {
        launch();
    }


    public void initStageTitle(Stage stage){
        stage.titleProperty().bind(stageTitle);
        stageTitle.addListener((o, oldv, newv) ->{ System.out.println("newv: " + newv); });
    }
}