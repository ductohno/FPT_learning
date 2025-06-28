/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0022;

/**
 *
 * @author ADMIN
 */
public class Fresher extends Candidate{
    // dont know it year of date, nah i choose year
    private int granduatedTime;
    private String graduationRank;
    private String graduatedUniversity;

    public Fresher() {
    }

    public Fresher(int granduatedTime, String graduationRank, String graduatedUniversity, Candidate candidate) {
        super(candidate.getId(), candidate.getFirstName(), candidate.getLastName(), candidate.getBirthDate(), candidate.getAddress(), candidate.getPhone(),candidate.getEmail(), candidate.getType());
        this.granduatedTime = granduatedTime;
        this.graduationRank = graduationRank;
        this.graduatedUniversity = graduatedUniversity;
    }

    public int getGranduatedTime() {
        return granduatedTime;
    }

    public void setGranduatedTime(int granduatedTime) {
        this.granduatedTime = granduatedTime;
    }

    public String getGraduationRank() {
        return graduationRank;
    }

    public void setGraduationRank(String graduationRank) {
        this.graduationRank = graduationRank;
    }

    public String getGraduatedUniversity() {
        return graduatedUniversity;
    }

    public void setGraduatedUniversity(String graduatedUniversity) {
        this.graduatedUniversity = graduatedUniversity;
    }
}
