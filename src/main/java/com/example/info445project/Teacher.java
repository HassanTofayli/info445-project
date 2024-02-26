package com.example.info445project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Teacher {
    public int id;
    public String Name;
    public String Password;
    private List<Integer> coursesCodes;

    public Teacher(String name, String password, List<Integer> courses) {
        Name = name;
        Password = password;
        this.coursesCodes = new ArrayList<>(courses);
    }

    public Teacher(int id, String name, String password) {
        this.id = id;
        Name = name;
        Password = password;
    }

    public Teacher(ResultSet rs) throws SQLException {
        this.id = rs.getInt("TeacherID");
        this.Name = rs.getString("Name");
        this.Password = rs.getString("Password");
        this.coursesCodes = new ArrayList<>();
        fetchCourseCodes(Main.conn);
    }

    public void addCourseCode(Connection conn, Integer courseCode) throws SQLException {
        if (!coursesCodes.contains(courseCode)) {
            String sql = "INSERT INTO TeacherCourses (TeacherID, CourseID) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, this.id);
                pstmt.setInt(2, courseCode);
                pstmt.executeUpdate();
                coursesCodes.add(courseCode); // Update local list if successful
                System.out.println("COURSE ADDED to Teacher Array: " + courseCode);
            }
        }
    }

    public void removeCourseCode(Connection conn, Integer courseCode) throws SQLException {
        String sql = "DELETE FROM TeacherCourses WHERE TeacherID = ? AND CourseID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setInt(2, courseCode);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                coursesCodes.remove(courseCode); // Update local list if successful
                System.out.println("COURSE REMOVED from Teacher Array: " + courseCode);
            }
        }
    }

    public void fetchAndSetCourseCodes(Connection conn) throws SQLException {
        String sql = "SELECT CourseID FROM TeacherCourses WHERE TeacherID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                this.coursesCodes.add(rs.getInt("CourseID"));
            }
        }
    }

    public static List<Teacher> fetchAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM Teachers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                teachers.add(new Teacher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }
    public List<Course> fetchTeacherCourses(Connection conn) throws SQLException {
        List<Course> courses = new ArrayList<>();
        // The SQL JOIN operation to get courses taught by the teacher
        String sql = "SELECT c.* FROM Courses c " +
                "JOIN teachercourses tc ON c.CourseID = tc.CourseID " +
                "WHERE tc.TeacherID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Assuming you have a constructor in your Course class that takes a ResultSet
                courses.add(new Course(rs));
            }
        }
        return courses;
    }



    public static Teacher getTeacherByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT * FROM Teachers WHERE Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Assuming you have a constructor in your Teacher class that takes a ResultSet
                return new Teacher(rs);
            }
        }
        return null; // No teacher found with the given name
    }
    public void fetchCourseCodes(Connection conn) throws SQLException {
        String sql = "SELECT CourseID FROM TeacherCourses WHERE TeacherID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int courseCode = rs.getInt("CourseID");
                coursesCodes.add(courseCode);
            }
        }
    }
}
