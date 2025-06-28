/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package j1.s.p0022;

/**
 *
 * @author ADMIN
 */
public class Main {

    public static void displayMenu(String msg, String[] menu) {
        System.out.println("==========" + msg + "==========");
        int index = 0;
        for (String part : menu) {
            index++;
            System.out.println(index + ". " + part);
        }
    }

    public static void main(String[] args) {
        boolean exited = false;
        Manager manager = new Manager();
        while (!exited) {
            String[] menu = {"Experience", "Fresher", "Internship", "Searching", "Exit"};
            displayMenu("Student manage", menu);
            int choosen = Utility.getChoose("Select one: ", "Input must be between 1 and " + menu.length, 1, menu.length);
            switch (choosen) {
                case (1):
                    manager.createCandidate(0);
                    break;
                case (2):
                    manager.createCandidate(1);
                    break;
                case (3):
                    manager.createCandidate(2);
                    break;
                case (4):
                    manager.search();
                    break;
                default:
                    exited = true;
                    break;
            }
        }
    }

}
