/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j1.s.p0021;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ADMIN
 */
public class Utility {
    public static int getPositiveInt(String msg, String errorMsg){
        int i = 0;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while(loop)
        {
            System.out.print(msg);
            try
            {
                i = Integer.parseInt(sc.nextLine());
                if(i>0)
                   loop = false;
            }
            catch(NumberFormatException e)
            {
                System.out.println(errorMsg);
            }
        }
        return i;
    }
    
    public static String getString(String msg, String errorMsg, String regex){
        String input=null;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while(loop)
        {
            System.out.print(msg);
            input=sc.nextLine().trim();
            if(input.matches(regex)){
                loop=false;
            }
            else{
                System.out.println(errorMsg);
            }
        }
        return input;
    }
    
    public static boolean checkIdDupdicate(ArrayList<Student> studentList, String id){
        for(Student student: studentList){
            if(student.getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    
    public static int getChoose(String msg, String errorMsg, int min, int max){
        int i = 0;
        Scanner sc = new Scanner(System.in);
        boolean loop = true;
        while(loop)
        {
            System.out.print(msg);
            try
            {
                i = Integer.parseInt(sc.nextLine());
                if(i>=min&&i<=max){
                    loop=false;
                }
                else{
                    System.out.println(errorMsg);
                }
            }
            catch(NumberFormatException e)
            {
                System.out.println(errorMsg);
            }
        }
        return i;
    }
}
