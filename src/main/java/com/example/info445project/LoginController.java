package com.example.info445project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class LoginController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField textField_username;
    @FXML
    private TextField textField_password;
    private String admin_username = "admin", admin_password = "admin";
    private String student_username = "Tom", student_password = "Tom123";
    int i = 0;
    Stage stage;
    Scene scene;
    Parent root;




    @FXML
    protected void onHelloClick(){
        welcomeText.setText("Clicked: " + ++i + " Times!");
    }
    @FXML
    public void onLoginClick(ActionEvent e) throws IOException {
        String username = textField_username.getText();
        String password = textField_password.getText();

        if (username.equals(admin_username) && password.equals(admin_password)) loginAdmin();
        else if (Arrays.stream(App_Database.students).anyMatch(student -> Objects.equals(student.Name, username) && Objects.equals(student.Password, password))) {

            loginStudent(e);
        }
        else {welcomeText.setText("Wrong Credentials"); welcomeText.setStyle("-fx-background-color: red; -fx-background-radius: 5; -fx-padding: 10;");}

    }

    private void loginStudent(ActionEvent e) throws IOException {

        String username = textField_username.getText();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("student-view.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();
        homeController.loadStudentHome(username);
        Main.stageTitle.set("Welcome " + username);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    private void loginAdmin() throws IOException {
    }

}