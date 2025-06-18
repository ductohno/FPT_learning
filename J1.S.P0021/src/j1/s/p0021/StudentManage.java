/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0021;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class StudentManage {

    private ArrayList<Student> studentList = new ArrayList<>();

    public StudentManage() {
        Student student1 = new Student("HE194107", "Nguyen Van A", "Fall 24", "C/C++");
        studentList.add(student1);
        Student student2 = new Student("HE185207", "Tran Thi C", "Fall 24", "Java");
        studentList.add(student2);
        Student student3 = new Student("HE185207", "Tran Thi C", "Spring 25", "Java");
        studentList.add(student3);
        Student student4 = new Student("HE185207", "Tran Thi C", "Sum 23", "C/C++");
        studentList.add(student4);
        Student student5 = new Student("HE185207", "Tran Thi C", "Fall 24", "C/C++");
        studentList.add(student5);
        Student student6 = new Student("SE185207", "Pham Van B", "Fall 24", ".Net");
        studentList.add(student6);
        Student student7 = new Student("HE152773", "Nguyen Thi C", "Sum 25", ".Net");
        studentList.add(student7);
        Student student8 = new Student("HE185207", "Tran Thi C", "Sum 25", ".Net");
        studentList.add(student8);
        Student student9 = new Student("HE195236", "Truong Xuan X", "Fall 24", "Java");
        studentList.add(student9);
        Student student10 = new Student("HE190202", "Nguyen Do Ngoc", "Fall 24", ".Net");
        studentList.add(student10);
    }

    public void createStudent() {
        System.out.println("=======Create======");
        boolean stopped = false;
        while (!stopped) {
            String id = Utility.getString("Enter id: ", "Invalid id", "^[A-Z]{2}[0-9]{6}");
            String name = Utility.getString("Enter name: ", "Invalid name", "^.+$");
            String semester = Utility.getString("Enter semester: ", "Invalid semester", "^.+$");
            String course = Utility.getString("Enter course: ", "Course must be Java, .Net or C/C++", "^Java|\\.Net|C/C\\+\\+$");
            Student addedStudent = new Student(id, name, semester, course);
            studentList.add(addedStudent);
            System.out.println("Created student " + name + " success");
            if (studentList.size() > 10) {
                String choosen = Utility.getString("Do you want to continue(Y/N): ", "You must chooose Y or N", "^[Y/N]$");
                if (choosen.equals("N")) {
                    stopped = true;
                }
            }
        }
    }

    public void findSortStudent() {
        ArrayList<Student> result = new ArrayList<>();
        String name = Utility.getString("Enter name: ", "Invalid name", "^.*$");
        for (Student student : studentList) {
            if (student.getStudentName().contains(name)) {
                result.add(student);
            }
        }
        if (!result.isEmpty()) {
            Collections.sort(result);
            this.displayStudentList(result, "Student list", false);
        } else {
            System.out.println("No result");
        }
    }

    public void updateDeleteStudent() {
        if (!studentList.isEmpty()) {
            boolean isFound = false;
            displayStudentList(studentList, "Student list", true);
            String id = Utility.getString("Enter id: ", "Invalid id", "^[A-Z]{2}[0-9]{6}");
            for (int i = 0; i < studentList.size(); i++) {
                Student student = studentList.get(i);
                if (student.getId().equals(id)) {
                    isFound = true;
                    System.out.println("Found student: " + student.getStudentName());
                    String choosen = Utility.getString("Do you want to update(U) or delete(D) or Nothing(N): ","Must be U, D or N", "^[UDN]$");
                    switch (choosen) {
                        case "U":
                            String name = Utility.getString("Enter updated name: ", "Invalid name", "^.+$");
                            String semester = Utility.getString("Enter updated semester: ", "Invalid semester", "^.+$");
                            String course = Utility.getString("Enter updated course: ","Course must be Java, .Net or C/C++","^Java|\\.Net|C/C\\+\\+$");
                            Student updatedStudent = new Student(id, name, semester, course);
                            studentList.set(i, updatedStudent); 
                            System.out.println("Update complete");
                            break;
                        case "D":
                            studentList.remove(studentList.indexOf(student)); 
                            System.out.println("Delete complete");
                            i--;
                            break;
                        default:
                            break;
                    }
                }
            }
            if (isFound == false) {
                System.out.println("Not found student with " + id);
            }
        } else {
            System.out.println("No student exist");
        }
    }

    public void reportStudent() {
        Map<String, Map<String, Integer>> map = new HashMap<>();

        for (Student student : studentList) {
            String studentName = student.getStudentName();

            if (map.containsKey(studentName)) {
                continue;
            }
            Map<String, Integer> courseMap = new HashMap<>();
            for (Student otherStudent : studentList) {
                if (studentName.equals(otherStudent.getStudentName())) {
                    String courseName = otherStudent.getCourseName();
                    int count = courseMap.getOrDefault(courseName, 0);
                    courseMap.put(courseName, count + 1);
                }
            }
            map.put(studentName, courseMap);
        }
        System.out.println("========Student report========");
        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            String name = entry.getKey();
            Map<String, Integer> courses = entry.getValue();
            for (Map.Entry<String, Integer> courseEntry : courses.entrySet()) {
                System.out.printf("%-18s|%-8s|%-8d\n", name, courseEntry.getKey(), courseEntry.getValue());
            }
        }
        System.out.println("Report student success");
    }

    public void displayStudentList(ArrayList<Student> list, String msg, boolean displayId) {
        System.out.println("========" + msg + "=========");
        if (displayId) {
                    System.out.printf("%-10s%-20s%-10s%-10s\n", "ID" , "Name", "Semester", "Course");
            for (Student student : list) {
                System.out.printf("%-10s%-20s%-10s%-10s\n", student.getId(), student.getStudentName(), student.getSemester(), student.getCourseName());
            }
        } else {
                    System.out.printf("%-20s%-10s%-10s\n", "Name", "Semester", "Course");
            for (Student student : list) {
                System.out.printf("%-20s%-10s%-10s\n", student.getStudentName(), student.getSemester(), student.getCourseName());
            }
        }
    }

}
