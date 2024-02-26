package com.example.info445project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

import static com.example.info445project.Main.*;

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

    @FXML
    protected void onAsGuestClick(ActionEvent e) throws IOException {
        Main.stageTitle.set("Guest View");
        Main.guest = true;
        String username = "Guest";

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("guest-view.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }
    @FXML
    public void onLoginClick(ActionEvent e) throws IOException {
        textField_username.addEventHandler(KeyEvent.KEY_TYPED, event -> welcomeText.setText(""));
        textField_password.addEventHandler(KeyEvent.KEY_TYPED, event -> welcomeText.setText(""));
        String username = textField_username.getText();
        String password = textField_password.getText();

        if (username.equals(admin_username) && password.equals(admin_password)) {
            loginAdmin();
        } else if (checkTeacherCredentials(username, password)) {
            loginTeacher(e);
        } else if (checkStudentCredentials(username, password)) {
            loginStudent(e);
        } else {
            welcomeText.setText("Wrong Credentials");
            welcomeText.setStyle("-fx-background-color: red; -fx-background-radius: 5; -fx-padding: 10;");
        }
    }


    // Check student credentials
    private boolean checkStudentCredentials(String username, String password) {
        String sql = "SELECT * FROM Students WHERE Name = ? AND Password = ?";
        return checkCredentials(sql, username, password);
    }

    // Check teacher credentials
    private boolean checkTeacherCredentials(String username, String password) {
        String sql = "SELECT * FROM Teachers WHERE Name = ? AND Password = ?";
        return checkCredentials(sql, username, password);
    }

    // General method to check credentials against a given SQL query
    private boolean checkCredentials(String sql, String username, String password) {
        try (PreparedStatement pstmt = Main.conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Ideally, this should be a hashed password
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If there's a result, the credentials are correct
        } catch (SQLException ex) {
            System.out.println("Error checking credentials: " + ex.getMessage());
            return false;
        }
    }

    private void loginStudent(ActionEvent e) throws IOException {
        Main.stageTitle.set("Student View");

        String username = textField_username.getText();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("student-view.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();
        homeController.loadStudentHome(username);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }
    private void loginTeacher(ActionEvent e) throws IOException {
        Main.stageTitle.set("Teacher View");

        String username = textField_username.getText();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("teacher-view.fxml"));
        root = loader.load();

        TeacherController teacherController = loader.getController();
        teacherController.loadTeacher(username);

        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }
    private void loginAdmin() throws IOException {
        Main.stageTitle.set("Admin View");

    }

}