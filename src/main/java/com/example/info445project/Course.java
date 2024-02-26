package com.example.info445project;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.example.info445project.Main.conn;

public class Course {
    public int id;
    public String Name;
    public String Description;
    public String ImageURL;
    public int Code;

    public Course() {
    }

    public Course(String name, String description, String imageURL, int code) {
        Name = name;
        Description = description;
        ImageURL = imageURL;
        Code = code;
    }

    public Course(ResultSet rs) throws SQLException {
        this.id = rs.getInt("CourseID");
        this.Name = rs.getString("Name");
        this.Description = rs.getString("Description");
        this.ImageURL = rs.getString("ImageURL");
        this.Code = rs.getInt("Code");
    }

    public Course(int courseId, String name, String description, String imageURL, int code) {
        id = courseId;
        Name = name;
        Description = description;
        ImageURL = imageURL;
        Code = code;
    }

    // Static method to create a new course in the database
    public static Course createCourse(Connection conn, String name, String description, String imageURL, int code) throws SQLException {
        // Check if the course already exists
        boolean courseExists = checkCourseExistsInDatabase(conn, code);

        if (courseExists) {
            // If the course exists, return null or handle accordingly
            System.out.println("Course with code " + code + " already exists.");
            return null;
        } else {
            // If the course does not exist, proceed with insertion
            String sql = "INSERT INTO Courses (Name, Description, ImageURL, Code) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, imageURL);
                pstmt.setInt(4, code);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int courseId = rs.getInt(1);
                            return new Course(courseId, name, description, imageURL, code);
                        }
                    }
                }
            }
            return null; // Course creation failed or course already exists
        }
    }

    public static Course editCourse(Connection conn, int courseId, String name, String description, String imageUrl, int code) throws SQLException {
        // Check if another course with the same code exists (excluding this course)
        String checkSql = "SELECT 1 FROM courses WHERE Code = ? AND CourseID != ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, code);
            checkStmt.setInt(2, courseId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // A course with the same code already exists, return null
                    return null;
                }
            }
        }

        // If no other course uses the new code, proceed with update
        String updateSql = "UPDATE courses SET Name = ?, Description = ?, ImageURL = ?, Code = ? WHERE CourseID = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, name);
            updateStmt.setString(2, description);
            updateStmt.setString(3, imageUrl);
            updateStmt.setInt(4, code);
            updateStmt.setInt(5, courseId);

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                // Update was successful, return the updated course
                return new Course(courseId, name, description, imageUrl, code);
            } else {
                // No rows updated, return null
                return null;
            }
        }
    }

    public static List<Course> fetchAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }


    public static Course fetchCourseFromDatabase(Connection conn, int code) throws SQLException {
        if (checkCourseExistsInDatabase(Main.conn, code)) {
            String sql = "SELECT * FROM Courses WHERE Code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int courseId = rs.getInt("CourseID");
                        String name = rs.getString("Name");
                        String description = rs.getString("Description");
                        String imageURL = rs.getString("ImageURL");
                        int courseCode = rs.getInt("Code");

                        // Assuming the Course class has a constructor that matches this signature
                        return new Course(courseId, name, description, imageURL, courseCode);
                    }
                }
            }
        }
        return null; // No course found
    }

    // Method to delete a course from the database
    public static boolean deleteCourse(Connection conn, int courseId) throws SQLException {
        String sql = "DELETE FROM Courses WHERE CourseID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static boolean checkCourseExistsInDatabase(Connection conn, int courseCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Courses WHERE Code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // True if count is more than 0
                }
            }
        }
        return false; // Course does not exist
    }

    public static Course getCourseByCode(Connection conn, int courseCode) throws SQLException {
        String sql = "SELECT * FROM Courses WHERE Code = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Assuming you have a constructor in your Course class that takes a ResultSet
                    return new Course(rs); // Return the found course
                }
            }
        }
        return null; // Return null if the course does not exist
    }




    class CourseAnnouncement implements CourseContent  {
        int announcementId;
        int courseId;
        String content;
        Timestamp createdAt;
        private Label contentLabel;
        public CourseAnnouncement(int announcementId, int courseId, String content, Timestamp createdAt) {
            this.announcementId = announcementId;
            this.courseId = courseId;
            this.content = content;
            this.createdAt = createdAt;
        }

        @Override
        public Timestamp getCreatedAt() {
            return this.createdAt;
        }
        public void updateAnnouncementUi(String newContent) {
            Platform.runLater(() -> {
                this.content = newContent; // Update the content attribute if exists
                this.contentLabel.setText(newContent); // Update the UI label text
            });
        }}

    class CourseImage implements CourseContent  {
        int imageId;
        int courseId;
        String url;
        String description;
        Timestamp createdAt;
        private ImageView imageView;
        private Label descriptionLabel;

        public CourseImage(int imageId, int courseId, String url, String description, Timestamp createdAt) {
            this.imageId = imageId;
            this.courseId = courseId;
            this.url = url;
            this.description = description;
            this.createdAt = createdAt;
        }
        public void updateImageUi(String newDescription, String newUrl) {
            Platform.runLater(() -> {
                this.description = newDescription; // Update the description attribute if exists
                this.url = newUrl; // Update the URL attribute if exists
                this.imageView.setImage(new Image(newUrl)); // Update the ImageView source
                this.descriptionLabel.setText(newDescription); // Update the description label text
            });
        }
        @Override
        public Timestamp getCreatedAt() {
            return this.createdAt;
        }    }

    class CourseVideo implements CourseContent  {
        int videoId;
        int courseId;
        String url;
        String description;
        Timestamp createdAt;
        private Label descriptionLabel;
        public CourseVideo(int videoId, int courseId, String url, String description, Timestamp createdAt) {
            this.videoId = videoId;
            this.courseId = courseId;
            this.url = url;
            this.description = description;
            this.createdAt = createdAt;
        }
        public void updateVideoUi(String newDescription) {
            Platform.runLater(() -> {
                this.description = newDescription; // Update the description attribute if exists
                this.descriptionLabel.setText(newDescription); // Update the description label text
                // If you have a thumbnail or video player, you might update it here as well
            });
        }
        @Override
        public Timestamp getCreatedAt() {
            return this.createdAt;
        }    }

    public List<CourseAnnouncement> getCourseAnnouncements(Connection conn, int courseId) throws SQLException {
        List<CourseAnnouncement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM courseannouncements WHERE CourseID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CourseAnnouncement announcement = new CourseAnnouncement(
                            rs.getInt("AnnouncementID"),
                            courseId,
                            rs.getString("Content"),
                            rs.getTimestamp("CreatedAt")
                    );
                    announcements.add(announcement);
                    System.out.println(announcement.content + " Date: " + announcement.createdAt);
                }
            }
        }
        return announcements;
    }
    public List<CourseImage> getCourseImages(Connection conn, int courseId) throws SQLException {
        List<CourseImage> images = new ArrayList<>();
        String sql = "SELECT * FROM courseimages WHERE CourseID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CourseImage image = new CourseImage(
                            rs.getInt("ImageID"),
                            courseId,
                            rs.getString("URL"),
                            rs.getString("Description"),
                            rs.getTimestamp("CreatedAt")
                    );
                    images.add(image);
                }
            }
        }
        return images;
    }
    public List<CourseVideo> getCourseVideos(Connection conn, int courseId) throws SQLException {
        List<CourseVideo> videos = new ArrayList<>();
        String sql = "SELECT * FROM coursevideos WHERE CourseID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CourseVideo video = new CourseVideo(
                            rs.getInt("VideoID"),
                            courseId,
                            rs.getString("URL"),
                            rs.getString("Description"),
                            rs.getTimestamp("CreatedAt")
                    );
                    videos.add(video);
                }
            }
        }
        return videos;
    }
    public List<CourseContent> getAllCourseContentSorted(Connection conn, int courseId) throws SQLException {
        List<CourseContent> allContent = new ArrayList<>();

        // Retrieve each type of content
        List<CourseAnnouncement> announcements = getCourseAnnouncements(conn, courseId);
        List<CourseImage> images = getCourseImages(conn, courseId);
        List<CourseVideo> videos = getCourseVideos(conn, courseId);

        // Add all content to the combined list
        allContent.addAll(announcements);
        allContent.addAll(images);
        allContent.addAll(videos);

        // Sort the combined list by createdAt timestamp
        allContent.sort(Comparator.comparing(CourseContent::getCreatedAt));

        return allContent;
    }
    public void fillScrollPaneWithContent(ScrollPane scrollPane, List<CourseContent> contentList) {
        VBox contentBox = new VBox(10); // VBox to hold all content, with spacing of 10 pixels
        contentBox.setStyle("-fx-padding: 10;"); // Add padding around the VBox

        for (CourseContent content : contentList) {
            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            // Common edit button action setup
            editButton.setOnAction(e -> {
                if (content instanceof CourseAnnouncement) {
                    editAnnouncementInScrollPane((CourseAnnouncement) content);
                } else if (content instanceof CourseImage) {
                    editImageInScrollPane((CourseImage) content);
                } else if (content instanceof CourseVideo) {
                    editVideoInScrollPane((CourseVideo) content);
                }
                // Add more else if blocks for other content types as necessary
            });

            if (content instanceof CourseAnnouncement) {
                CourseAnnouncement announcement = (CourseAnnouncement) content;
                Label titleLabel = new Label("Announcement: " + content.getCreatedAt());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2a2a2a; -fx-underline: true;");

                Label contentLabel = new Label(announcement.content);
                announcement.contentLabel = contentLabel;
                contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                contentLabel.setWrapText(true);

                VBox announcementBox = new VBox(10, titleLabel, contentLabel, editButton);
                announcementBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

                contentBox.getChildren().add(announcementBox);
            } else if (content instanceof CourseImage) {
                CourseImage image = (CourseImage) content;
                ImageView imageView = new ImageView(new Image(image.url));
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                Label descriptionLabel = new Label(image.description);
                descriptionLabel.setWrapText(true);

                Label titleLabel = new Label("Image: " + content.getCreatedAt());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2a2a2a; -fx-underline: true;");

                VBox imageBox = new VBox(10, titleLabel, imageView, descriptionLabel, editButton);
                imageBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");

                image.imageView = imageView;
                image.descriptionLabel = descriptionLabel;
                contentBox.getChildren().add(imageBox);
            } else if (content instanceof CourseVideo) {
                CourseVideo video = (CourseVideo) content;
                Label titleLabel = new Label("Video: " + content.getCreatedAt());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2a2a2a; -fx-underline: true;");

                Label videoLabel = new Label(video.description);
                videoLabel.setWrapText(true);

                VBox videoBox = new VBox(10, titleLabel, videoLabel, editButton);
                videoBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 20; -fx-background-radius: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
                video.descriptionLabel = videoLabel;

                contentBox.getChildren().add(videoBox);
            }
            // Other types of content can be added here in a similar manner
        }

        // Set the VBox as the content for the scroll pane
        scrollPane.setContent(contentBox);
    }


    public void editAnnouncementInScrollPane(Course.CourseAnnouncement announcement) {
        TextInputDialog dialog = new TextInputDialog(announcement.content);
        dialog.setTitle("Edit Announcement");
        dialog.setHeaderText("Edit the announcement content:");
        dialog.setContentText("Content:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newContent -> {
            try {
                editAnnouncement(announcement.announcementId, newContent);
                announcement.updateAnnouncementUi(newContent);
                // Update the UI accordingly, perhaps by finding the announcement's node and updating the label
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void editImageInScrollPane(CourseImage image) {
        TextInputDialog descriptionDialog = new TextInputDialog(image.description);
        descriptionDialog.setTitle("Edit Image Description");
        descriptionDialog.setHeaderText("Edit the description for the image:");
        descriptionDialog.setContentText("Description:");

        Optional<String> descriptionResult = descriptionDialog.showAndWait();
        descriptionResult.ifPresent(newDescription -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select New Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File newImageFile = fileChooser.showOpenDialog(null);
            if (newImageFile != null && !newImageFile.toURI().toString().equals(image.url)) {
                try {
                    boolean isOldImageUsedElsewhere = checkIfImageUsedByOtherAnnouncements(conn, image.url);

                    String newImageUrl = copyFileToDirectory(newImageFile, "images");

                    editCourseImage(image.imageId, newImageUrl, newDescription);

                    if (!isOldImageUsedElsewhere) {
                        deleteImageFromFileSystem(image.url);
                    }

                    image.updateImageUi(newDescription, newImageUrl);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    editCourseImage(image.imageId, image.url, newDescription);
                    image.updateImageUi(image.url, newDescription);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkIfImageUsedByOtherAnnouncements(Connection conn, String imageUrl) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Announcements WHERE ImageURL = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imageUrl);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    public String copyFileToDirectory(File file, String subDirectory) {
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


    private void deleteImageFromFileSystem(String imageUrl) {
        // Assuming imageUrl is a relative path
        File imageFile = new File("src/main/resources/com/example/info445project/" + imageUrl);
        if (imageFile.exists() && !imageFile.isDirectory()) {
            imageFile.delete();
        }
    }


    public void editVideoInScrollPane(Course.CourseVideo video) {
        // Open a dialog to get the new video description
        TextInputDialog dialog = new TextInputDialog(video.description);
        dialog.setTitle("Edit Video Description");
        dialog.setHeaderText("Edit the description for the video:");
        dialog.setContentText("Description:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newDescription -> {
            try {
                editCourseVideo(video.videoId, video.url, newDescription);
                video.updateVideoUi(newDescription);
                // Update the UI accordingly, perhaps by finding the video's node and updating the label
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }





    public void editAnnouncement(int announcementId, String newContent) throws SQLException {
        String sql = "UPDATE courseannouncements SET Content = ? WHERE AnnouncementID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, announcementId);
            pstmt.executeUpdate();
        }
    }
    public void editCourseImage(int imageId, String newImageUrl, String newDescription) throws SQLException {
        String sql = "UPDATE courseimages SET URL = ?, Description = ? WHERE ImageID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newImageUrl);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, imageId);
            pstmt.executeUpdate();
        }
    }
    public void editCourseVideo(int videoId, String newVideoUrl, String newDescription) throws SQLException {
        String sql = "UPDATE coursevideos SET URL = ?, Description = ? WHERE VideoID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newVideoUrl);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, videoId);
            pstmt.executeUpdate();
        }
    }

}
