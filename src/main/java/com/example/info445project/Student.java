package com.example.info445project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student {
    public int id;
    public String Name;
    public String Password;
    public int InstitutionId;
    public List<Integer> coursesCodes;
    public List<Teacher> privateTeachers;


    public Student(String name, String password, List<Integer> courses) {
        Name = name;
        Password = password;
        this.coursesCodes = new ArrayList<>(courses);
        this.privateTeachers = new ArrayList<>();
    }
    public Student(ResultSet rs) throws SQLException {
        this.id = rs.getInt("StudentID");
        this.Name = rs.getString("Name");
        this.Password = rs.getString("Password");
        this.InstitutionId = rs.getInt("InstitutionID");
        this.coursesCodes = new ArrayList<>();
        this.privateTeachers = new ArrayList<>();
    }


    public void addCourseCode(Connection conn, Integer courseCode) throws SQLException {
        // Get the CourseID from the courses table using the CourseCode
        String courseIdSql = "SELECT CourseID FROM courses WHERE Code = ?";
        int courseId = -1;
        try (PreparedStatement courseIdStmt = conn.prepareStatement(courseIdSql)) {
            courseIdStmt.setInt(1, courseCode);
            ResultSet courseIdRs = courseIdStmt.executeQuery();
            if (courseIdRs.next()) {
                courseId = courseIdRs.getInt("CourseID");
            } else {
                throw new SQLException("Course code does not exist in the courses table.");
            }
        }

        // Now, check if the course code already exists for this student in the database
        String checkSql = "SELECT COUNT(*) FROM StudentCourses WHERE StudentID = ? AND CourseID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, this.id);
            checkStmt.setInt(2, courseId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("This course is already registered for this student.");
                return; // Course already exists, so exit the method
            }
        }

        // If the course is not registered for this student, proceed to insert it
        String insertSql = "INSERT INTO StudentCourses (StudentID, CourseID) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, this.id);
            insertStmt.setInt(2, courseId); // Use the obtained CourseID here
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("COURSE ADDED for Student: " + this.id + ", Course ID: " + courseId);
                coursesCodes.add(courseId); // Add the CourseID to the local list if successful
            }
        }
    }




    public void addPrivateTeacher(Connection conn, Teacher teacher) throws SQLException {
        int studentId = ((Student)Main.currentUser).id; // Assuming Main.currentUser is always a Student

        // First, check against the database to ensure there isn't already a link between this student and teacher
        String checkSql = "SELECT COUNT(*) FROM StudentTeachers WHERE StudentID = ? AND TeacherID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, teacher.id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Already exists in the database
                System.out.println("This teacher is already a private teacher for this student");
                return; // Stop the method here
            }
        }

        // If not found in the database, proceed to insert
        String insertSql = "INSERT INTO StudentTeachers (StudentID, TeacherID) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, studentId);
            insertStmt.setInt(2, teacher.id);
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("TEACHER ADDED to Student Array: " + teacher.Name);
                privateTeachers.add(teacher); // Add to the local list if successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle potential SQL exceptions, such as a failed insert
        }
    }


    public void removeCourseCode(Connection conn, Integer courseCode) throws SQLException {
        String sql = "DELETE FROM StudentCourses WHERE StudentID = ? AND CourseID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setInt(2, courseCode);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                coursesCodes.remove(courseCode); // Remove from the local list if successful
            }
        }
    }

    public List<Integer> getCoursesCodes(Connection conn) throws SQLException {
        String sql = "SELECT CourseID FROM StudentCourses WHERE StudentID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int courseCode = rs.getInt("CourseID");
                coursesCodes.add(courseCode);
            }
        }
        return coursesCodes;
    }
    public List<Teacher> getPrivateTeachers(Connection conn) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.* FROM Teachers t JOIN StudentTeachers st ON t.TeacherID = st.TeacherID WHERE st.StudentID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id); // Assuming 'this.id' is the student's ID
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int teacherId = rs.getInt("TeacherID");
                String name = rs.getString("Name");
                String password = rs.getString("Password");
                Teacher teacher = new Teacher(teacherId, name, password);
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    public void addInstitution(Connection conn, int newInstitutionId) throws SQLException {
        String sql = "UPDATE Students SET InstitutionID = ? WHERE StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newInstitutionId);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
            // Update local field if the database update is successful
            this.InstitutionId = newInstitutionId;
        }
    }

    public void removeInstitution(Connection conn) throws SQLException {
        String sql = "UPDATE Students SET InstitutionID = NULL WHERE StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.executeUpdate();
            // Update local field if the database update is successful
            this.InstitutionId = 0; // Or any other value representing no institution
        }
    }

    public void removePrivateTeacher(Connection conn, int teacherId) throws SQLException {
        String sql = "DELETE FROM StudentTeachers WHERE StudentID = ? AND TeacherID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.id);
            pstmt.setInt(2, teacherId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Remove the teacher from the local list if the database deletion is successful
                privateTeachers.removeIf(teacher -> teacher.id == teacherId);
            }
        }
    }


    public static Student getStudentByName(String name) throws SQLException {
        String sql = "SELECT * FROM Students WHERE Name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(rs);
            } else {
                return null;
            }
        }
    }

    public boolean checkIfStudentRegisteredForCourse(Connection conn, int studentId, int courseCode) throws SQLException {
        String sql = "SELECT COUNT(*) FROM StudentCourses WHERE StudentID = ? AND CourseID = (SELECT CourseID FROM Courses WHERE Code = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // True if count is more than 0, meaning student is registered
                }
            }
        }
        return false; // Student is not registered for the course
    }

}
