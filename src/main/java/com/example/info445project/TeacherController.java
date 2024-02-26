package com.example.info445project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.List;
import java.util.Optional;

import static com.example.info445project.Main.*;

import static com.example.info445project.Main.stageTitle;

public class TeacherController {


    @FXML
    private Label welcome_text;
    @FXML
    private TilePane teacher_courses_tilepane;
    Label err = new Label("");

    TextField nameField, descriptionField, codeField;
    Label chosenImageLabel;
    private ScrollPane courseContentScrollPane;
    private Stage all_courses_stage;
    private Scene all_courses_scene;
    private Teacher teacher;
    private Course newCourse;
    TeacherController teacherController;
    HomeController homeController;
    private Button createAnnouncementButton;

    public static void returnToTeacherView() throws IOException {

    }


    public void loadTeacher(String username) {
        welcome_text.setText("Welcome " + username);
        try {
            teacher = Teacher.getTeacherByName(Main.conn, username);
            if (teacher != null) {
                stageTitle.set("Teacher View");
                Main.currentUser = teacher;
                showTeacherCourses();
            } else {
                System.out.println("No teacher found with the name: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onShowAllCoursesButton(ActionEvent e) throws IOException {
        homeController = new HomeController();
        homeController.onShowAllCoursesButton(e);
    }

    @FXML
    public void onBackToLoginClick(ActionEvent e) {
        homeController = new HomeController();
        homeController.onBackToLoginClick(e);
    }


    public void loadTeacherHome(String name) throws FileNotFoundException {
        welcome_text.setText("Welcome " + name);

        if (!Main.guest) {
            try {
                teacher = Teacher.getTeacherByName(Main.conn, name);
                if (teacher != null) {
                    Main.stageTitle.set("Teacher View");
                    Main.currentUser = teacher; // Update current user to the logged-in teacher
                    showTeacherCourses(); // Load and display the teacher's courses
                } else {
                    System.out.println("No teacher found with the name: " + name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Main.stageTitle.set("Guest View");
        }
    }

    private void showTeacherCourses() throws FileNotFoundException {
        try {
            List<Course> courses = teacher.fetchTeacherCourses(Main.conn);
            teacher_courses_tilepane.getChildren().clear(); // Clear existing courses before loading new ones

            for (Course course : courses) {
                teacher_courses_tilepane.getChildren().add(createCourseCard(course));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        } else if (currentViewTitle.equals("Teacher View")) {
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> {
                try {
                    newCourse = course;
                    showEditCourseView();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            });
        }
        return card;
    }
    @FXML
    public void showEditCourseView() throws FileNotFoundException, SQLException { // courseId is null when creating a new course
        System.out.println(newCourse.Name);
        initialize();
        Stage stage = new Stage();

        Button editCourseBtn = new Button("Edit");
        Button chooseImageButton = new Button("Choose Image");

        // Main layout container
        BorderPane mainLayout = new BorderPane();

        // Top components
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(15));
        topBox.setAlignment(Pos.CENTER);

        Button createAnnouncementButton = new Button("Create Announcement");
        Button createVideoButton = new Button("Create Video");
        Button createImageButton = new Button("Create Image");

        // Text fields for course properties
        nameField = new TextField();
        nameField.setPromptText("Name");
        descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        codeField = new TextField();
        codeField.setPromptText("Code");
        chosenImageLabel = new Label(newCourse.ImageURL);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Course Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File destDir = new File("src/main/resources/com/example/info445project/images/courses");
        chooseImageButton.setOnAction(ev -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    File destFile = new File(destDir, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    chosenImageLabel.setText(selectedFile.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    chosenImageLabel.setText("Failed to copy image");
                }
            }
        });

        nameField.setText(newCourse.Name);
        descriptionField.setText(newCourse.Description);
        codeField.setText(String.valueOf(newCourse.Code));
        chosenImageLabel.setText(newCourse.ImageURL);

        editCourseBtn.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String imageName = chosenImageLabel.getText();
            String code = codeField.getText();

            if (name.isEmpty() || description.isEmpty() || imageName.isEmpty() || code.isEmpty()) {
                System.out.println("All fields are required.");
                return;
            }

            try {
                newCourse = Course.editCourse(Main.conn, newCourse.id, name, description, imageName, Integer.parseInt(code));

                // Enable or disable buttons as needed
                createAnnouncementButton.setDisable(false);
                createVideoButton.setDisable(false);
                createImageButton.setDisable(false);
            } catch (SQLException ex) {
                err.setText("Couldn't update the course");
                ex.printStackTrace();
            }
        });

        topBox.getChildren().addAll(nameField, descriptionField, codeField, chooseImageButton, chosenImageLabel, editCourseBtn);
        mainLayout.setTop(topBox); // Set the topBox to the top of the main layout

        // Place the buttons and their containers on the layout
        HBox buttonsBox = new HBox(10, createAnnouncementButton, createVideoButton, createImageButton);
        buttonsBox.setPadding(new Insets(15));
        buttonsBox.setAlignment(Pos.CENTER);

        createAnnouncementButton.setOnAction(ev -> addAnnouncementToScrollPane());
        createVideoButton.setOnAction(ev -> addVideoToScrollPane());
        createImageButton.setOnAction(ev -> addImageToScrollPane());

        Button createContentButton = new Button("Close and Save");
        createContentButton.setOnAction(ev -> {
            stage.close();
            try {
                showTeacherCourses();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Bottom box that contains the Close and Save button
        HBox createBox = new HBox(10, createContentButton);
        createBox.setPadding(new Insets(15));
        createBox.setAlignment(Pos.CENTER);

        // Combine buttons into a container
        VBox bottomContainer = new VBox(10, buttonsBox, createBox);
        mainLayout.setBottom(bottomContainer);

        List<CourseContent> contents = newCourse.getAllCourseContentSorted(conn, newCourse.id);
        for (CourseContent c : contents) System.out.println(c.getCreatedAt());
        // Scrollable content area setup...
        courseContentScrollPane = new ScrollPane();
        newCourse.fillScrollPaneWithContent(courseContentScrollPane, contents);
        courseContentScrollPane.setFitToWidth(true);
        courseContentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        courseContentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainLayout.setCenter(courseContentScrollPane);

        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setTitle("Edit Course View");
        stage.setScene(scene);
        stage.showAndWait();
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

        alert.showAndWait();
    }

    @FXML
    public void showCreateCourseView(ActionEvent e) {
        initialize();
        Stage stage = new Stage();
        Button CreateCourse = new Button("Create");
        Button chooseImageButton = new Button("Choose Image");

        Button createAnnouncementButton = new Button("Create Announcement");
        Button createVideoButton = new Button("Create Video");
        Button createImageButton = new Button("Create Image");


        // Main layout container
        BorderPane mainLayout = new BorderPane();

        // Top components
        HBox topBox = new HBox(10);
        topBox.setPadding(new Insets(15));
        topBox.setAlignment(Pos.CENTER);

        // Text fields for course properties
        nameField = new TextField();
        nameField.setPromptText("Name");
        descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        codeField = new TextField();
        codeField.setPromptText("Code");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Course Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        chosenImageLabel = new Label("No image selected");
        chooseImageButton.setOnAction(ev -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                chosenImageLabel.setText(selectedFile.getName());
                // Additional logic to handle the file will go here
            }
        });
        CreateCourse.setStyle("-fx-background-color: green;");
        CreateCourse.setOnAction(event -> {
            // Retrieve content from text fields
            String name = nameField.getText();
            String description = descriptionField.getText();
            String imageName = chosenImageLabel.getText();
            String code = codeField.getText();

            // Validate input (basic validation example)
            if (name.isEmpty() || description.isEmpty() || imageName.isEmpty() || code.isEmpty() || imageName.equals("Failed to copy image")) {
                // Show error message or alert
                System.out.println("All fields are required.");
                return;
            }
            try {
                newCourse = Course.createCourse(Main.conn, name, description, "images/courses/" + imageName, Integer.parseInt(code));
                if (newCourse != null) {
                    addCourseToTeacher(((Teacher)currentUser).id,newCourse.id);
                    createAnnouncementButton.setDisable(false);
                    createVideoButton.setDisable(false);
                    createImageButton.setDisable(false);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        topBox.getChildren().addAll(nameField, descriptionField, codeField, chooseImageButton, chosenImageLabel, CreateCourse);
        mainLayout.setTop(topBox); // Set the topBox to the top of the main layout

        File destDir = new File("src/main/resources/com/example/info445project/images/courses");
        if (!destDir.exists())
            destDir.mkdirs();

        chooseImageButton.setOnAction(ev -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    File destFile = new File(destDir, selectedFile.getName());
                    Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    chosenImageLabel.setText(selectedFile.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    chosenImageLabel.setText("Failed to copy image");
                }
            }
        });


        // Buttons list
        HBox buttonsBox = new HBox(10);
        buttonsBox.setPadding(new Insets(15));
        buttonsBox.setAlignment(Pos.CENTER);

        createAnnouncementButton.setOnAction(ev -> addAnnouncementToScrollPane());
        createVideoButton.setOnAction(ev -> addVideoToScrollPane());
        createImageButton.setOnAction(ev -> addImageToScrollPane());

        createAnnouncementButton.setDisable(true);
        createVideoButton.setDisable(true);
        createImageButton.setDisable(true);
        // Add btns
        buttonsBox.getChildren().addAll(createAnnouncementButton, createImageButton, createVideoButton);

        HBox createBox = new HBox(10);
        createBox.setPadding(new Insets(15));
        createBox.setAlignment(Pos.CENTER);

        Button createContentButton = new Button("Close and Save");
        createContentButton.setOnAction(ev -> {
            stage.close();
            try {
                showTeacherCourses();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        createBox.getChildren().add(createContentButton);

        // Place createBox just below the buttonsBox in the layout
        VBox bottomContainer = new VBox(10, buttonsBox, createBox);
        mainLayout.setBottom(bottomContainer);
        // Scrollable content area
        courseContentScrollPane = new ScrollPane();
        VBox scrollContent = new VBox();
        scrollContent.setStyle("-fx-background-color: WHITE;");
        courseContentScrollPane.setContent(scrollContent);
        courseContentScrollPane.setFitToWidth(true);
        courseContentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        courseContentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        mainLayout.setCenter(courseContentScrollPane); // Set the scrollable area to the center of the main layout
        Scene scene = new Scene(mainLayout, 1200, 800);
        stage.setTitle("Create Course View");
        stage.setScene(scene);
        stage.showAndWait();
    }
    private void addCourseToTeacher(int teacherID, int courseID) throws SQLException {
        // Check if the course is already assigned to the teacher
        String checkSql = "SELECT COUNT(*) FROM teachercourses WHERE TeacherID = ? AND CourseID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, teacherID);
            checkStmt.setInt(2, courseID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        // The course is already assigned to this teacher, so we don't need to insert it again.
                        System.out.println("The course is already assigned to this teacher.");
                        return; // Exit the method early
                    }
                }
            }
        }

        // If we reach here, the course is not assigned to the teacher yet, so proceed with the insert.
        String insertSql = "INSERT INTO teachercourses (TeacherID, CourseID) VALUES (?, ?);";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, teacherID);
            insertStmt.setInt(2, courseID);
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding course to teacher failed, no rows affected.");
            }
        }
    }


    private FileChooser fileChooser;
    public void initialize() {
        // Initialize the file chooser once for reuse
        fileChooser = new FileChooser();
        String[] paths = {
                "src/main/resources/com/example/info445project/images/courses/content/images",
                "src/main/resources/com/example/info445project/images/courses/content/videos"
        };
        for (String path : paths) {
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
    }

    public void addAnnouncementToScrollPane() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Announcement");
        dialog.setHeaderText("Create a new announcement");
        dialog.setContentText("Enter the announcement content:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(content -> {
            try {
                int annId = addAnnouncement(content);
                addAnnouncementToScrollPaneBtn(annId, content);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void addAnnouncementToScrollPaneBtn(int announcementId, String content) {
        VBox contentBox = (VBox) courseContentScrollPane.getContent();

        Label titleLabel = new Label("Announcement");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2a2a2a; -fx-underline: true;");

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

        VBox announcementBox = new VBox(10, titleLabel, contentLabel, removeButton);
        announcementBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

        contentBox.getChildren().add(announcementBox);
        contentBox.setSpacing(20); // Add spacing between announcements

        removeButton.setOnAction(e -> {
            // Remove from UI
            contentBox.getChildren().remove(announcementBox);

            // Remove from database
            try {
                removeAnnouncementFromDatabase(announcementId);
            } catch (SQLException ex) {
                ex.printStackTrace(); // Handle exception
            }
        });
    }
    public void addVideoToScrollPane() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mov", "*.avi")
        );

        File videoFile = fileChooser.showOpenDialog(null); // Replace 'null' with a Stage reference if needed
        if (videoFile != null) {
            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Video Description");
            descriptionDialog.setHeaderText("Enter a description for the video:");
            descriptionDialog.setContentText("Description:");

            Optional<String> result = descriptionDialog.showAndWait();
            result.ifPresent(description -> {
                try {
                    // Assume addVideo returns the ID of the new video added to the database
                    int videoId = addVideo(videoFile.toURI().toString(), description);
                    copyFileToDirectory(videoFile, "videos");
                    addVideoToScrollPaneBtn(videoId, videoFile.toURI().toString(), description);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            });
        }
    }

    public void addVideoToScrollPaneBtn(int videoId, String videoUrl, String description) {
        VBox contentBox = (VBox) courseContentScrollPane.getContent();

        Label titleLabel = new Label("Video");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2a2a2a;");

        // Create a WebView for video playback
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        String videoHTML = String.format(
                "<video width='320' controls><source src='%s' type='video/mp4'></video>",
                videoUrl
        );
        webEngine.loadContent(videoHTML);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        descLabel.setWrapText(true);

        Button removeButton = new Button("Remove");
        HBox videoBox = new HBox(10, titleLabel, webView, descLabel, removeButton);
        videoBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 15; -fx-background-radius: 5; -fx-border-radius: 5;");

        contentBox.getChildren().add(videoBox);
        contentBox.setSpacing(10); // Add spacing between items

        removeButton.setOnAction(e -> {
            contentBox.getChildren().remove(videoBox);
            // No need to dispose of anything here, WebView handles it

            try {
                removeVideoFromDatabase(videoId);
            } catch (SQLException ex) {
                ex.printStackTrace(); // Handle exception
            }
        });
    }
    public void addImageToScrollPane() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File imageFile = fileChooser.showOpenDialog(null); // Assuming this is called within a context where 'null' can be replaced with a Stage reference if needed
        if (imageFile != null) {
            TextInputDialog descriptionDialog = new TextInputDialog();
            descriptionDialog.setTitle("Image Description");
            descriptionDialog.setHeaderText("Enter a description for the image:");
            descriptionDialog.setContentText("Description:");

            Optional<String> result = descriptionDialog.showAndWait();
            result.ifPresent(description -> {
                try {
                    // Assume addImage returns the ID of the new image added to the database
                    int imageId = addImage(imageFile.toURI().toString(), description);
                    copyFileToDirectory(imageFile, "images");
                    addImageToScrollPaneBtn(imageId, imageFile.toURI().toString(), description);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    public void addImageToScrollPaneBtn(int imageId, String imageUrl, String description) {
        VBox contentBox = (VBox) courseContentScrollPane.getContent();

        Label titleLabel = new Label("Image");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2a2a2a; -fx-underline: true;");

        ImageView imageView = new ImageView(new Image(imageUrl));
        imageView.setFitWidth(200); // Adjust as necessary
        imageView.setFitHeight(150); // Adjust as necessary
        imageView.setPreserveRatio(true);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        descLabel.setWrapText(true);

        VBox imageAndDesc = new VBox(imageView, descLabel);
        imageAndDesc.setSpacing(5);

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

        VBox imageBox = new VBox(10, titleLabel, imageAndDesc, removeButton);
        imageBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-border-radius: 5; -fx-alignment: top-center;");
        VBox.setMargin(imageBox, new Insets(10, 0, 10, 0)); // Adds margin around the entire VBox

        contentBox.getChildren().add(imageBox);
        contentBox.setSpacing(10); // Add spacing between items

        removeButton.setOnAction(e -> {
            contentBox.getChildren().remove(imageBox);

            try {
                removeImageFromDatabase(imageId);
            } catch (SQLException ex) {
                ex.printStackTrace(); // Handle exception
            }
        });
    }






    public int addVideo(String url, String description) throws SQLException {
        String sql = "INSERT INTO CourseVideos (CourseID, URL, Description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, newCourse.Code); // Assuming newCourse.Code contains the correct CourseID
            pstmt.setString(2, url);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the ID of the newly inserted video
                } else {
                    throw new SQLException("Creating video failed, no ID obtained.");
                }
            }
        }
    }
    public int addAnnouncement(String content) throws SQLException {
        String sql = "INSERT INTO CourseAnnouncements (CourseID, Content) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, newCourse.id); // Assuming newCourse.Code contains the correct CourseID
            pstmt.setString(2, content);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the ID of the newly inserted announcement
                } else {
                    throw new SQLException("Creating announcement failed, no ID obtained.");
                }
            }
        }
    }
    public int addImage(String url, String description) throws SQLException {
        String sql = "INSERT INTO CourseImages (CourseID, URL, Description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, newCourse.id); // Make sure newCourse.id contains the correct CourseID
            pstmt.setString(2, url);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the ID of the newly inserted image
                } else {
                    throw new SQLException("Creating image failed, no ID obtained.");
                }
            }
        }
    }



    public void removeAnnouncementFromDatabase(int announcementId) throws SQLException {
        String sql = "DELETE FROM CourseAnnouncements WHERE AnnouncementID = ?";
        try (PreparedStatement pstmt = Main.conn.prepareStatement(sql)) {
            pstmt.setInt(1, announcementId);
            pstmt.executeUpdate();
        }
    }
    public void removeVideoFromDatabase(int videoId) throws SQLException {
        String sql = "DELETE FROM CourseVideos WHERE VideoID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, videoId);
            pstmt.executeUpdate();
        }
    }
    public void removeImageFromDatabase(int imageId) throws SQLException {
        String sql = "DELETE FROM CourseImages WHERE ImageID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, imageId);
            pstmt.executeUpdate();
        }
    }

    private String copyFileToDirectory(File file, String subDirectory) {
        File destDir = new File("src/main/resources/com/example/info445project/images/courses/content/" + subDirectory);

        // Ensure the directory exists
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File destFile = new File(destDir, file.getName());
        if (!destFile.exists()) { // Check if the file already exists
            try {
                Files.copy(file.toPath(), destFile.toPath()); // Copy without REPLACE_EXISTING option
                return "The file uploaded successfully"; // You might want to return a path or a status
            } catch (IOException ex) {
                ex.printStackTrace(); // Handle the exception as needed
                return "Upload failed";
            }
        } else {
            // File already exists, handle according to your needs
            return "File " + destFile.getName() + " already exists.";
        }
    }


    private Optional<Pair<String, String>> showFileDescriptionDialog(File file, String fileType) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(fileType + " Description");
        dialog.setHeaderText("Enter description for the " + fileType.toLowerCase());

        // Set the button types.
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the description label and field.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField description = new TextField();
        description.setPromptText("Description");

        grid.add(new Label("Description:"), 0, 0);
        grid.add(description, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the description field by default.
        Platform.runLater(description::requestFocus);

        // Convert the result to a pair when the OK button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(file.getAbsolutePath(), description.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
