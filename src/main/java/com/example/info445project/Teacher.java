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
    }

    public void addCourseCode(Integer courseCode) {
        if (!coursesCodes.contains(courseCode)) {
            System.out.println("COURSE ADDED to Student Array: " + courseCode);
            coursesCodes.add(courseCode);
        }
    }
    public void removeCourseCode(Integer courseCode) {
        coursesCodes.remove(courseCode);
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


    public List<Integer> getCoursesCodes() {
        return coursesCodes;
    }}
