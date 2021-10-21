package ui;

import java.util.ArrayList;

import static ui.Console.*;

public class Scheduler {
    /*
     UI.Scheduler.java is our main, driver class. The scheduler class handles communication with the user's input and
     the rest of the program.
    */

    public static void main(String[] args) {
        String continuing = "y", userInput = "";
        int courseNumber, semestersLeft, maxCredits;
        ArrayList<String[]> courseCodes = new ArrayList<>();

        System.out.println("Welcome to the Clemson University undergraduate student scheduler!");
        System.out.println("This program will walk you through building a semester-by-semester schedule according to your current academic progress and preferences.");
        System.out.println("◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦");

        System.out.print("To begin, ");
        semestersLeft = getInt("please input the amount of semesters you have left in your curriculum.", 0, 10);
        maxCredits = getInt("What is the maximum amount of credits you would like to take in one semester?", 0, 25);

        System.out.println("\nNow, please enter the classes you have already taken.");
        System.out.println("Input should be in the format of a course code with the subject and then the number, separated by a space.");
        System.out.println("Course numbers must be below 7000, and greater than or equal to 1000.");

        //loop that handles input courses and adds each valid input course to the courseCodes array.
        while(continuing.equalsIgnoreCase("Y") || continuing.equalsIgnoreCase("C")) {
            userInput = getUserInput("\nPlease enter a course code for a course you've taken. E.G. \"CPSC 2810\"");
            String[] parsedString = userInput.split(" ");

            //handles invalid input in the case that the user inputs an unacceptable course code
            //meaning there are several spaces separating sections of input, the course title length is outside the range [3,4],
            //or the course number length is not equal to 4.
            if(parsedString.length != 2 || parsedString[0].length() > 4 || parsedString[0].length() < 3
                || parsedString[1].length() != 4) {
                System.out.println("Error! Course code must be separated by a space, with the subject and then the number.");
                continue;
            }

            //makes sure that the course number input is a valid integer
            try {
                courseNumber = Integer.parseInt(parsedString[1]);
                if(courseNumber < 1000 || courseNumber >= 7000) {
                    System.out.println("Error! Course number must be greater than or equal to 1000 and less than 7000.");
                    continue;
                }
            } catch(NumberFormatException e) {
                System.out.println("Error! Course number must be a valid integer.");
                continue;
            }

            System.out.println("You entered the course as: " + userInput);
            continuing = getUserInput("Would you like to add another course? Please input \"y\" for yes, \"n\" for no." +
                    "\nIf you entered the incorrect course code and would like to change it, please input \"c\".");

            //if the user doesn't want to change their input, add it to the courseCodes arrayList.
            if(!continuing.equalsIgnoreCase("C")) {
                //if the user would not like to change their input course, add the course to the courseCodes arraylist.
                courseCodes.add(parsedString);
                System.out.println(userInput + " has been added to your completed course list.");
            }
        }

        for(int i = 0; i < semestersLeft; i++) {
            //for each semester, walk the user through a pseudo-schedule, prompting for the gen eds they have left.
            System.out.println("◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦");
            System.out.println("SEMESTER " + (i+1));
            System.out.println("---");


            System.out.println("◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦•◦");
        }
    }
}
