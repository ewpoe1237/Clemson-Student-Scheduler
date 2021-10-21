package ui;

import java.util.Scanner;

public class Console {
    /*
        The console class contains static methods used for convenience in the UI.Scheduler/main class. This helps maintain project structure organization and cleanliness in code.
        The methods in this class were created in reference to Mike Murach & associates' coding assignments within his textbook, "Murach's Beginning Java with Eclipse"
     */

    private static Scanner sc = new Scanner(System.in);

    public static String getUserInput(String prompt) {
        //obtains a string, according to user input. will re-prompt if the user inputs an empty value.
        String s = "";
        while (true) {
            System.out.println(prompt);
            s = sc.nextLine();
            if (s.equals("")) {
                System.out.println("Error! This entry is required. Please enter a valid input.");
            } else {
                break;
            }
        }
        return s;
    }

    public static int getInt(String prompt) {
        //obtains an integer, according to user input. will re-prompt if the integer is invalid.
        int i = 0;
        while (true) {
            System.out.println(prompt);
            try {
                i = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error! Invalid integer. Please enter a whole number.");
            }
        }
        return i;
    }

    public static int getInt(String prompt, int lowLimit, int highLimit) {
        //obtains an integer input from the user. Will re-prompt if the user inputs an integer outside the (lowLimit, highLimit) range.
        int i = 0;

        while (true) {
            i = getInt(prompt);
            if (i <= lowLimit) {
                System.out.println("Error! Number must be greater than " + lowLimit);
            } else if (i >= highLimit) {
                System.out.println("Error! Number must be less than " + highLimit);
            } else {
                break;
            }
        }
        return i;
    }

    public static double getDouble(String prompt) {
        //obtains a double, according to user input. will re-prompt if the double is invalid.
        double d = 0;
        while (true) {
            System.out.print(prompt);
            try {
                d = Double.parseDouble(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error! Invalid decimal. Please enter a valid number.");
            }
        }
        return d;
    }

    public static double getDouble(String prompt, double lowLimit, double highLimit) {
        //obtains a double input from the user. Will re-prompt if the user inputs a double outside the (lowLimit, highLimit) range.
        double d = 0;
        while (true) {
            d = getDouble(prompt);
            if (d <= lowLimit) {
                System.out.println(
                        "Error! Number must be greater than " + lowLimit);
            } else if (d >= highLimit) {
                System.out.println(
                        "Error! Number must be less than " + highLimit);
            } else {
                break;
            }
        }
        return d;
    }
}
