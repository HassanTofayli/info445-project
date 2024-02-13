package com.example.info445project;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    public String Name;
    public String Password;
    private List<Integer> coursesCodes;

    public Teacher(String name, String password, List<Integer> courses) {
        Name = name;
        Password = password;
        this.coursesCodes = new ArrayList<>(courses);
    }
    public void addCourseCode(Integer courseCode) {
        if (!coursesCodes.contains(courseCode)) { // Avoid adding duplicates
            System.out.println("COURSE ADDED to Student Array: " + courseCode);
            coursesCodes.add(courseCode);
        }
    }
    public void removeCourseCode(Integer courseCode) {
        coursesCodes.remove(courseCode);
    }
    public List<Integer> getCoursesCodes() {
        return new ArrayList<>(coursesCodes);
    }
}
