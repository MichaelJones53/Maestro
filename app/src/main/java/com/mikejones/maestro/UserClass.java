package com.mikejones.maestro;

import java.util.ArrayList;

public class UserClass {
    private String professor;
    private String professorId;
    private String classId;
    private String className;
    private ArrayList<Student> students;
    private ArrayList<Topic> topics;

    public UserClass(String p, String pId, String id, String n, ArrayList<Student> s, ArrayList<Topic> t){
        professor = p;
        professorId = pId;
        classId = id;
        className = n;
        students = s;
        topics = t;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }


}
