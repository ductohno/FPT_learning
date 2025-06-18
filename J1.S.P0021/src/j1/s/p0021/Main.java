/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package j1.s.p0021;

/**
 *
 * @author ADMIN
 */
public class Main {

    public static void displayMenu(String msg, String[] menu){
        System.out.println("=========="+msg+"==========");
        int index = 0;
        for (String part: menu){
            index++;
            System.out.println(index+". "+part);
        }
    }
    
    public static void main(String[] args) {
        boolean exited=false;
        StudentManage manage = new StudentManage();
        while(!exited){
            String[] menu = {"Create", "Find/Sort", "Update/Delete", "Report", "Exit"};
            displayMenu("Student manage", menu);
            int choosen = Utility.getChoose("Select one: ", "Input must be between 1 and "+ menu.length, 1, menu.length);
            switch(choosen){
                case(1):
                    manage.createStudent();
                    break;
                case(2):
                    manage.findSortStudent();
                    break;
                case(3):
                    manage.updateDeleteStudent();
                    break;
                case(4):
                    manage.reportStudent();
                    break;
                default:
                    exited=true;
                    break;
            }
        }
    }
}
