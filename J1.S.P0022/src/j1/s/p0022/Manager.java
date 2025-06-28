/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0022;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author ADMIN
 */
public class Manager {
    
    //Attribute
    private ArrayList<Candidate> candidateList = new ArrayList<>();
    private ArrayList<Experience> experienceList = new ArrayList<>();
    private ArrayList<Fresher> fresherList = new ArrayList<>();
    private ArrayList<Intern> internList = new ArrayList<>();
    private final Map<Integer, String> candidateTypeMap= new HashMap<>();
            
    //Constant
    private final int CURRENT_YEAR = LocalDate.now().getYear();
    private final int MAX_YEAR_OF_EXP = 100;
    private final String NOT_NULL_STRING = "^.+$";
    private final String EMAIL = "^[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String RANK_OF_GRADUATION = "^(Excellence|Good|Fair|Poor)$";
    private final String YES_NO_QUESTION = "^[YyNn]$";
    private final String PHONE_NUMBER = "^[0-9]{10,}$";
    
    //Contructor
    public Manager() {
        this.candidateTypeMap.put(0, "Experience");
        this.candidateTypeMap.put(1, "Fresher");
        this.candidateTypeMap.put(2, "Intern");
    }
    
    //Method
    public void createCandidate(int indexOfTypeCandidate){
        boolean isExited = false;
        while(!isExited){
            if(!candidateTypeMap.containsKey(indexOfTypeCandidate)){
                System.out.println("This candidate type id doesn't exists");
                return;
            }
            System.out.println("==============Create " + this.candidateTypeMap.get(indexOfTypeCandidate) + "==============");
            String id = Utility.getID("Enter id: ", "Invalid id or id in the list", NOT_NULL_STRING, candidateList);
            String firstName = Utility.getString("Enter first name: ", "Invalid first name", NOT_NULL_STRING);
            String lastName = Utility.getString("Enter last name: ", "Invalid last name", NOT_NULL_STRING);
            int birthDate = Utility.getChoose("Enter birth date: ", "Birth date must be from 1900 to now", 1900, this.CURRENT_YEAR);
            String address = Utility.getString("Enter address: ", "Invalid address", NOT_NULL_STRING);
            String phone = Utility.getString("Enter phone number: ", "Phone number must be >10 numbers", this.PHONE_NUMBER);
            String email = Utility.getString("Enter email: ", "Invalid email", EMAIL);
            int type = indexOfTypeCandidate;
            Candidate candidate = new Candidate(id, firstName, lastName, birthDate, address, phone, email, type);
            switch(type){
                case 0:
                    this.createExperience(candidate);
                    break;
                case 1: 
                    this.createFresher(candidate);
                    break;
                case 2:
                    this.createIntern(candidate);
                    break;
                default:
                    System.out.println("Create candidate successfully");
                    break;
            }
            this.candidateList.add(candidate);
            String choosen = Utility.getString("Do you want to continue (Y/N): ", "Choosen must be Y/y or N/n", this.YES_NO_QUESTION);
            if(choosen.equalsIgnoreCase("N")){
                isExited = true;
            }
        }
    }
    
    public void createExperience(Candidate candidate){
        int expInYear = Utility.getChoose("Enter year of experience: ", "Year of experience must be from 0 to " + this.MAX_YEAR_OF_EXP, 0, this.MAX_YEAR_OF_EXP);
        String proSkill = Utility.getString("Enter professional skill: ", "Invalid professional skill", NOT_NULL_STRING);
        Experience experiencer = new Experience(expInYear, proSkill, candidate);
        this.experienceList.add(experiencer);
        System.out.println("Create experience successfully");
    }
    
    public void createFresher(Candidate candidate){
        int granduatedTime = Utility.getChoose("Enter granduated time: ", "Granduated time must be from " + candidate.getBirthDate() + " to " + this.CURRENT_YEAR, candidate.getBirthDate(), this.CURRENT_YEAR);
        String granduationRank = Utility.getString("Enter granduation rank: ", "Granduation rank must be "+ this.RANK_OF_GRADUATION, this.RANK_OF_GRADUATION);
        String graduatedUniversity = Utility.getString("Enter granduated university: ", "Invalid granduation university", this.NOT_NULL_STRING);
        Fresher fresher = new Fresher(granduatedTime, granduationRank, graduatedUniversity, candidate);
        this.fresherList.add(fresher);
        System.out.println("Create fresher successfully");
    }
    
    public void createIntern(Candidate candidate){
        String major = Utility.getString("Enter major: ", "Invalid major", this.NOT_NULL_STRING); 
        String semester = Utility.getString("Enter semester: ", "Invalid semester", this.NOT_NULL_STRING);
        String universityName = Utility.getString("Enter university name: ", "Invalid university name", this.NOT_NULL_STRING);
        Intern intern = new Intern(major, semester, universityName, candidate);
        this.internList.add(intern);
        System.out.println("Create intern successfully");
    }
    
    public void search(){
        displayCandidateName();
        String searchedString = Utility.getString("Enter candidate name (first or last name): ", "Must be not null, try again", this.NOT_NULL_STRING).toLowerCase();
        int typeOfCandidate = Utility.getTypeCandidate("Enter type of candidate: ", "That type doesn't exist", candidateTypeMap);
        ArrayList<Candidate> result = new ArrayList<>();
        for(Candidate candidate: candidateList){
            if((candidate.getFirstName().toLowerCase().contains(searchedString) || candidate.getLastName().toLowerCase().contains(searchedString))
               && (candidate.getType()==typeOfCandidate)){
                result.add(candidate);
            }
        }
        System.out.println("");
        this.displaySearchResult("The candidate found: ", result);
    }
    
    
    public void displayCandidateName(){
        if(this.candidateList.isEmpty()){
            System.out.println("No result");
            return;
        }
        if(!this.experienceList.isEmpty()){
            System.out.println("============"+"Experience Candidate"+"==============");
            for(Experience experience: this.experienceList){
                System.out.println(experience.getFirstName()+" "+experience.getLastName());
            }
        }
        if(!this.fresherList.isEmpty()){
            System.out.println("============"+"Fresher Candidate"+"==============");
            for(Fresher fresher: this.fresherList){
                System.out.println(fresher.getFirstName()+" "+fresher.getLastName());
            }
        }
        if(!this.internList.isEmpty()){
            System.out.println("============"+"Intern Candidate"+"==============");
            for(Intern intern: this.internList){
                System.out.println(intern.getFirstName()+" "+intern.getLastName());
            }
        }
    }
    
    public void displaySearchResult(String msg, ArrayList<Candidate> result){
        if(result.isEmpty()){
            System.out.println("Not found");
            return;
        }
        System.out.println(msg);
        for(Candidate candidate : result){
            System.out.println(candidate.toString());
        }
    }
}
