package com.example.info445project;

import java.util.ArrayList;
import java.util.List;

public class Student {
    public String Name;
    public String Password;
    private List<Integer> coursesCodes;

    public List<Teacher> privateTeachers;

    public Student(String name, String password, List<Integer> courses) {
        Name = name;
        Password = password;
        this.coursesCodes = new ArrayList<>(courses);
        this.privateTeachers = new ArrayList<>();
    }
    public void addCourseCode(Integer courseCode) {
        if (!coursesCodes.contains(courseCode)) { // Avoid adding duplicates
            System.out.println("COURSE ADDED to Student Array: " + courseCode);
            coursesCodes.add(courseCode);
        }
    }
    public void addPrivateTeacher(Teacher teacher) {
        if (!privateTeachers.contains(teacher)) { // Avoid adding duplicates
            System.out.println("COURSE ADDED to Student Array: " + teacher.Name);
            privateTeachers.add(teacher);
        }else System.out.println("This teacher is already a private teacher for this student");
    }
    public void removeCourseCode(Integer courseCode) {
        coursesCodes.remove(courseCode);
    }
    public List<Integer> getCoursesCodes() {
        return new ArrayList<>(coursesCodes);
    }
    public List<Teacher> getPrivateTeachers() {
        return new ArrayList<>(privateTeachers);
    }
}
