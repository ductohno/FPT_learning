/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0021;

/**
 *
 * @author ADMIN
 */
// implements: 1 class ke thua 1 giao dien
//Comparable: Chỉ ra lớp này có thể được so sánh với lớp ở trong <>, định nghĩa các quy tắc so sánh
// Định nghĩa được là do, ví dụ như Collection.sort, kế thừa giao diện Comparable, và dùng method compareTo để so sánh
public class Student implements Comparable<Student>{
    private String id;
    private String studentName;
    private String semester;
    private String courseName;

    public Student(String id, String studentName, String semester, String courseName) {
        this.id = id;
        this.studentName = studentName;
        this.semester = semester;
        this.courseName = courseName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    @Override
    // a.compareTo(b) nếu a > b, đảo vị trí
    public int compareTo(Student t) {
        return this.studentName.compareTo(t.studentName);
    }
    
}
