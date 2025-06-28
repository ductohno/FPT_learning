/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0022;

/**
 *
 * @author ADMIN
 */
public class Intern extends Candidate{
    private String majors;
    private String semester;
    private String universityName;

    public Intern() {
    }

    public Intern(String majors, String semester, String universityName, Candidate candidate) {
        super(candidate.getId(), candidate.getFirstName(), candidate.getLastName(), candidate.getBirthDate(), candidate.getAddress(), candidate.getPhone(), candidate.getEmail(), candidate.getType());
        this.majors = majors;
        this.semester = semester;
        this.universityName = universityName;
    }

    public String getMajors() {
        return majors;
    }

    public void setMajors(String majors) {
        this.majors = majors;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }
}
