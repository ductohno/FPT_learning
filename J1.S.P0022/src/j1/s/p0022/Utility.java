/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0022;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author ADMIN
 */
public class Utility {

    public static int getPositiveInt(String msg, String errorMsg) {
        int i = 0;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.print(msg);
            try {
                i = Integer.parseInt(sc.nextLine());
                if (i >= 0) {
                    loop = false;
                }
                else{
                    System.out.println(errorMsg);
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMsg);
            }
        }
        return i;
    }

    public static String getString(String msg, String errorMsg, String regex) {
        String input = null;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.print(msg);
            input = sc.nextLine().trim();
            if (input.matches(regex)) {
                loop = false;
            } else {
                System.out.println(errorMsg);
            }
        }
        return input;
    }

    public static int getChoose(String msg, String errorMsg, int min, int max) {
        int i = 0;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.print(msg);
            try {
                i = Integer.parseInt(sc.nextLine());
                if (i >= min && i <= max) {
                    loop = false;
                } else {
                    System.out.println(errorMsg);
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMsg);
            }
        }
        return i;
    }

    public static String getID(String message, String errorMessage, String regex, ArrayList<Candidate> candidateList) {
        while (true) {
            String input = getString(message, errorMessage, regex);

            boolean isDuplicate = false;
            for (Candidate candidate : candidateList) {
                if (candidate.getId().equalsIgnoreCase(input)) { 
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                return input;
            } else {
                System.out.println("ID is duplicated. Please enter a different ID.");
            }
        }
    }

    public static int getTypeCandidate(String msg, String errorMsg, Map<Integer, String> maps) {
        while (true) {
            int input = getPositiveInt(msg, errorMsg);
            if (maps.containsKey(input)) {
                return input;
            } else {
                System.out.println(errorMsg);
                System.out.println("Exited value: ");
                for (Map.Entry<Integer, String> entry : maps.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        }
    }
}
