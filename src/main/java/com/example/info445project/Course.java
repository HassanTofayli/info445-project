package com.example.info445project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Course {
    public int id;
    public String Name;
    public String Description;
    public String ImageURL;
    public int Code;

    public Course(String name, String description, String imageURL, int code) {
        Name = name;
        Description = description;
        ImageURL = imageURL;
        Code = code;
    }
    public Course(ResultSet rs) throws SQLException {
        this.id=rs.getInt("CourseID");
        this.Name = rs.getString("Name");
        this.Description = rs.getString("Description");
        this.ImageURL = rs.getString("ImageURL");
        this.Code = rs.getInt("Code");
    }

    public Course(int courseId, String name, String description, String imageURL, int code) {
        id=courseId;
        Name = name;
        Description = description;
        ImageURL = imageURL;
        Code = code;
    }

    // Static method to create a new course in the database
    public static Course createCourse(Connection conn, String name, String description, String imageURL, int code) throws SQLException {
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
        return null; // Course creation failed
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
}
