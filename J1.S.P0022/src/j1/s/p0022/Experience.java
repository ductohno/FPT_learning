/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0022;

/**
 *
 * @author ADMIN
 */
public class Experience extends Candidate{
    private int expInYear;
    private String proSkill;

    public Experience() {
    }

    public Experience(int expInYear, String proSkill, Candidate candidate) {
        super(candidate.getId(), candidate.getFirstName(), candidate.getLastName(), candidate.getBirthDate(), candidate.getAddress(), candidate.getPhone(), candidate.getEmail(), candidate.getType());
        this.expInYear = expInYear;
        this.proSkill = proSkill;
    }

    public int getExpInYear() {
        return expInYear;
    }

    public void setExpInYear(int expInYear) {
        this.expInYear = expInYear;
    }

    public String getProSkill() {
        return proSkill;
    }

    public void setProSkill(String proSkill) {
        this.proSkill = proSkill;
    }
}
