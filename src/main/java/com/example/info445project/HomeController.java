package com.example.info445project;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.example.info445project.Main.stageTitle;
import static com.example.info445project.Main.*;


public class HomeController {

    @FXML
    private Label welcome_text;
    @FXML
    private TilePane courses_pane;
    @FXML
    private TilePane student_courses_tilepane;
    @FXML
    private TilePane private_teachers_tilepane;


    private Stage all_courses_stage;
    private Scene all_courses_scene;
    private Student student;



    HomeController homeController;

    public void loadStudentHome(String name) throws FileNotFoundException {
        welcome_text.setText("Welcome " + name);

        if(!Main.guest){
            try {
                student = Student.getStudentByName(name);
                if (student != null) {
                    stageTitle.set("Student View");
                    Main.currentUser = student;
                    student = (Student) Main.currentUser;
                    showStudentCourses();
                    showPrivateTeachers();
                } else {
                    System.out.println("No student found with the name: " + name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else stageTitle.set("Guest View");


    }
    public void onBackToLoginClick(ActionEvent e){
        try {
            stageTitle.set("Login");
            Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            Main.guest = false;

        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }


    @FXML
    public void returnToStudentView(ActionEvent e) throws IOException {
        if(Main.guest) {
            stageTitle.set("Guest View");
            String username = "Guest";

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("guest-view.fxml"));
            root = loader.load();

            HomeController homeController = loader.getController();
            homeController.loadStudentHome("guest");

        } else if (Main.currentUser instanceof Teacher) {
            stageTitle.set("Teacher View");
            FXMLLoader loader = new FXMLLoader(TeacherController.class.getResource("teacher-view.fxml"));
            root = loader.load();
            Teacher teacher = (Teacher) currentUser;
            TeacherController teacherController = loader.getController();
            teacherController.loadTeacher(teacher.Name);
        } else{
            stageTitle.set("Student View");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student-view.fxml"));
            root = loader.load();
            homeController = loader.getController();

            student = (Student) Main.currentUser;

            homeController.loadStudentHome(student.Name);
        }
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

    public void showPrivateTeachers() {
        // Assuming 'StudentTeachers' is the junction table between students and teachers
        String sql = "SELECT t.* FROM Teachers t " +
                "JOIN StudentTeachers st ON t.TeacherID = st.TeacherID " +
                "WHERE st.StudentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, student.id);
            ResultSet rs = pstmt.executeQuery();

            List<Teacher> privateTeachers = new ArrayList<>();
            while (rs.next()) {
                privateTeachers.add(new Teacher(rs));
            }

            if (!privateTeachers.isEmpty()) {
                for (Teacher teacher : privateTeachers) {
                    System.out.println(teacher.Name);
                }
                viewTeachers(privateTeachers, private_teachers_tilepane);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void showStudentCourses() {
        // Assuming 'StudentCourses' is the junction table between students and courses
        String sql = "SELECT c.* FROM Courses c " +
                "JOIN StudentCourses sc ON c.CourseID = sc.CourseID " +
                "WHERE sc.StudentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, student.id);
            ResultSet rs = pstmt.executeQuery();

            List<Course> studentCourses = new ArrayList<>();
            while (rs.next()) {
                studentCourses.add(new Course(rs));
            }

            for (Course course : studentCourses) {
                System.out.println(course.Name + " - " + course.ImageURL);
            }
            viewCourses(studentCourses, student_courses_tilepane);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void onAddClassClick() {
        System.out.println("clicked");

        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER); // Correct alignment

        Text text = new Text("Enter the Code of your class");
        Font font = new Font("Mono", 10);
        Text text_grayed = new Text("Note for Developer: use 1, 2, 3... for testing");
        text_grayed.setFont(font);
        TextField code_input = new TextField();
        code_input.setPromptText("Enter Code"); // Use prompt text instead of setting text
        code_input.setMaxWidth(200); // Adjusted width for better UX
        Button addClassBtn = new Button("Add Class");
        Text err = new Text();

        addClassBtn.setOnAction(e -> {
            err.setText(""); // Clear previous error message

            try {
                int code = Integer.parseInt(code_input.getText());
                // Replace the following with a database check
                boolean courseExists = Course.checkCourseExistsInDatabase(Main.conn, code); // Implement this method
                boolean isAlreadyRegistered = student.checkIfStudentRegisteredForCourse(Main.conn, ((Student) Main.currentUser).id, code); // Implement this method

                if (courseExists) {
                    if (isAlreadyRegistered) {
                        err.setText("This course is already registered");
                    } else {
                        Course myCourse = Course.fetchCourseFromDatabase(Main.conn, code); // Implement fetching the course from the database
                        System.out.println("FOUND: " + myCourse.Name + " - code: " + myCourse.Code);
                        student.addCourseCode(Main.conn, code); // Implement adding course to student in the database
                        // Update UI accordingly
                        student_courses_tilepane.getChildren().add(createCourseCard(myCourse));
                        stage.close();
                    }
                } else {
                    err.setText("Not Found, Try another code!");
                }
            } catch (NumberFormatException ex) {
                err.setText("Please enter a valid number, strings are not allowed.");
            } catch (Exception ex) {
                err.setText("An error occurred.");
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(text, text_grayed, code_input, addClassBtn, err);

        stage.setTitle("Add Class");
        stage.setScene(new Scene(root, 400, 300));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    }


    @FXML
    public void onShowAllTeachersButton(ActionEvent e) throws IOException {
        stageTitle.set("All Teachers");
        List<Teacher> teachers = Teacher.fetchAllTeachers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("all-teachers.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();

        viewTeachers(teachers, homeController.courses_pane);

        ScrollPane scrollPane = new ScrollPane(courses_pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        all_courses_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        all_courses_scene = new Scene(root, 1000, 600);
        all_courses_stage.setScene(all_courses_scene);
        all_courses_stage.show();
        System.out.println(Main.currentUser);
    }

    public void viewTeachers(List<Teacher> teahcers, TilePane tilePane) throws FileNotFoundException {
        for (Teacher teacher : teahcers)
            tilePane.getChildren().add(createTeacherCard(teacher));
        System.out.println(Main.currentUser);
    }

    public StackPane createTeacherCard(Teacher teacher) throws FileNotFoundException {
        System.out.println(Main.currentUser);

        System.out.println(teacher.Name);
        FileInputStream in = new FileInputStream("src/main/resources/com/example/info445project/images/teacher.jpg");
        ImageView imageView = new ImageView(new Image(in));
        imageView.setFitWidth(150);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        Label title = new Label(teacher.Name);
        title.setWrapText(true);
        title.setMaxWidth(imageView.getFitWidth());

        VBox cardContent = new VBox(5, imageView, title);

        StackPane card = new StackPane(cardContent);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #FFFFFF;");
        card.setPadding(new Insets(5));

        String currentViewTitle = stageTitle.get(); // Assuming Main.stageTitle is bound to the stage title
        if (currentViewTitle.equals("All Teachers")) {
            card.setOnMouseClicked(e -> {
                try {
                    showTeacherDetails(teacher);
                } catch (FileNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }
            });
        } else if (currentViewTitle.equals("Student View")) {
            // Add a remove button for the student view
            Button removeButton = new Button("Remove");
            removeButton.setStyle("-fx-background-color: red;");
            removeButton.setTooltip(new Tooltip("Remove this private teacher"));
            removeButton.setOnAction(e -> {
                try {
                    System.out.println("Remove: " + teacher.Name);
                    student.removePrivateTeacher(Main.conn, teacher.id);
                    private_teachers_tilepane.getChildren().clear(); // Clear the current UI
                    showPrivateTeachers(); // Reload and display the updated list from the database
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // Position the button on the card (example: top right)
            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
            card.getChildren().add(removeButton);
        }
        return card;
    }

    public void showTeacherDetails(Teacher teacher) throws FileNotFoundException, SQLException {
        System.out.println(teacher.Name);
        System.out.println("Main User" + Main.currentUser);
        System.out.println("Main User" + ((Student) Main.currentUser).Name);


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Private Lessons with " + teacher.Name);
        alert.setContentText("Do you want to get private lessons with this teacher?");

        ButtonType addButton = new ButtonType("Add Teacher");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(addButton, cancelButton);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        FileInputStream in = new FileInputStream("src/main/resources/com/example/info445project/images/teacher.jpg");
        ImageView imageView = new ImageView(new Image(in));
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        Label desc = new Label("Do you want to get private lesson with this teacher?");
        desc.setMaxWidth(350);
        desc.setWrapText(true);

        content.getChildren().addAll(imageView, desc);
        alert.getDialogPane().setContent(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == addButton) {
            student.addPrivateTeacher(Main.conn, teacher);
            alert.close();
        } else {

            alert.close();
        }
    }


    @FXML
    public void onShowAllCoursesButton(ActionEvent e) throws IOException {
        stageTitle.set("All Courses");
        List<Course> courses = Course.fetchAllCourses();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("all-courses-view.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();

        homeController.viewCourses(courses, homeController.courses_pane);

        ScrollPane scrollPane = new ScrollPane(courses_pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        all_courses_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        all_courses_scene = new Scene(root, 1000, 600);
        all_courses_stage.setScene(all_courses_scene);
        all_courses_stage.show();
    }

    public void viewCourses(List<Course> courses, TilePane tilePane) throws FileNotFoundException {
        for (Course course : courses)
            tilePane.getChildren().add(createCourseCard(course));
    }

    public StackPane createCourseCard(Course course) throws FileNotFoundException {
        System.out.println(course.ImageURL);
        FileInputStream in = new FileInputStream("src/main/resources/com/example/info445project/" + course.ImageURL);
        ImageView imageView = new ImageView(new Image(in));
        imageView.setFitWidth(150);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);

        Label title = new Label(course.Name);
        title.setWrapText(true);
        title.setMaxWidth(imageView.getFitWidth());

        VBox cardContent = new VBox(5, imageView, title);

        StackPane card = new StackPane(cardContent);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #FFFFFF;");
        card.setPadding(new Insets(5));

        String currentViewTitle = stageTitle.get(); // Assuming Main.stageTitle is bound to the stage title
        if (currentViewTitle.equals("All Courses")) {
            card.setOnMouseClicked(e -> {
                try {
                    showCourseDetails(course);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
        } else if (currentViewTitle.equals("Student View")) {
            // Add a remove button for the student view
            Button removeButton = new Button("X");
            removeButton.setStyle("-fx-background-color: red; -fx-background-radius: 15");
            removeButton.setTooltip(new Tooltip("Remove this private teacher"));
            removeButton.setOnAction(e -> {
                try {
                    student.removeCourseCode(Main.conn, course.id);
                    student_courses_tilepane.getChildren().clear(); // Clear the current UI
                    showStudentCourses(); // Reload and display the updated list from the database
                    System.out.println("Course Removed: " + course.Name);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // Position the button on the card (example: top right)
            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
            card.getChildren().add(removeButton);
        }
        return card;
    }

    public void showCourseDetails(Course course) throws FileNotFoundException {
        System.out.println(course.ImageURL);
        System.out.println(course.Name);
        System.out.println(course.Code);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(course.Name + " - Code: " + course.Code);
        alert.setHeaderText(course.Name + " - Code: " + course.Code);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        FileInputStream in = new FileInputStream("src/main/resources/com/example/info445project/" + course.ImageURL);
        ImageView imageView = new ImageView(new Image(in));
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        Label desc = new Label(course.Description);
        desc.setMaxWidth(350);
        desc.setWrapText(true);

        content.getChildren().addAll(imageView, desc);
        alert.getDialogPane().setContent(content);

        // Create a custom button to add the course
        ButtonType addButton = new ButtonType("Add Course");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(addButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == addButton) {
            try {
                ((Student)Main.currentUser).addCourseCode(Main.conn, course.Code);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}