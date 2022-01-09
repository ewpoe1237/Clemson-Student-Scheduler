package ui;

import db.DBUtil;
import scheduling.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class ScheduleGUI {
    private JPanel wrapper;
    private JPanel rootPanel;
    private JPanel rootCard;
    private JPanel SemesterOne;
    private JButton continueButtonOne;
    private JPanel inputPrompts;
    private JSlider semesterSlider;
    private JSlider creditSlider;
    private JButton continueButtonTwo;
    private JPanel courseInputCard;
    private JPanel courseInputs;
    private JPanel inputCard;
    private JRadioButton honorsButton;
    private JButton adminContinue;
    private JPanel adminCard;
    private JPanel adminLogin;
    private JTextArea usernameInput;
    private JPasswordField passwordInput;
    private JButton loginButton;
    private JPanel additionCard;
    private JPanel additionPanel;
    private JTextField courseField;
    private JTextField coreqField;
    private JTextField reqPreField;
    private JTextField groupPreField;
    private JButton confirmExitButton;
    private JButton confirmAddButton;
    private JTextField attributeField;
    private JList<String> inputList;
    private JButton confirmContinueButton;
    private JTextField titleField;
    private JTextField creditHourField;
    private JRadioButton exportButton;
    private JPanel adminPortalCard;
    private JPanel adminPortal;
    private JButton addCoursesButton;
    private JPanel editorCard;
    private JPanel editorPanel;
    private JComboBox editCourseSelector;
    private JPanel DBViewerCard;
    private JPanel viewerPanel;
    private JButton editCoursesButton;
    private JButton viewCoursesButton;
    private JButton cancelAndExitButton;
    private JButton exitToMainMenuButton;
    private JButton confirmEditButton;
    private JButton cancelToPortalButton;
    private JButton confirmEditToPortalButton;
    private JComboBox fieldEditComboBox;
    private JTextField newEditValue;
    private JComboBox requirementsBox;
    private JScrollPane courseListScrollbar;
    private JTextArea textToView;
    private JComboBox typeToView;
    private JButton exitToPortalButton;
    private JScrollPane inputScroller;
    private JPanel semOneCard;
    private JPanel semOnePanel;
    private JLabel semesterTitle;
    private JLabel mainCSLabel;
    private JLabel introComputingLabel;
    private JLabel natSciLabel;
    private JLabel ethicsLabel;
    private JLabel statisticsLabel;
    private JLabel theoryLabel;
    private JLabel CS3000Label;
    private JLabel writingLabel;
    private JLabel genEdLabel;
    private JComboBox introCompBox;
    private JComboBox natSciBox;
    private JComboBox ethicsBox;
    private JComboBox statisticsBox;
    private JComboBox theoryBox;
    private JComboBox CS3000Box;
    private JComboBox writingBox;
    private JButton confirmScheduleButton;
    private JLabel creditLabel;
    private JTable semOneTable;

    private String[] mainCategories = {"MainCS", "IntroComputing", "NaturalScience", "Ethics", "Statistics", "Theory", "CS3000Above", "Writing"};
    private String[] genEdCategories = {"Comm", "Lit", "Nonlit", "Social Sciences (3 Credits)", "Social Sciences (6 Credits)", "CCA", "STS"};

    private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100};
    private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int semestersLeft = 10, maxCredits = 15;
    private int creditsSpent = 0;
    private boolean honorsStudent = false, wantToExport = false;

    private HashMap<String, Integer> inputCourses = new HashMap<>();
    private HashMap<String, Boolean> usedRequirements = new HashMap<>();
    private ArrayList<Course> coursesLoaded = new ArrayList<>();
    private static ArrayList<ArrayList<Course>> schedules = new ArrayList<>();
    private ArrayList<String> genEdsToFulfill = new ArrayList<>();
    private ArrayList<Course> currentSchedule = new ArrayList<>();

    static ScheduleGUI myGUI = new ScheduleGUI();
    static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("ScheduleGUI");
        frame.setContentPane(myGUI.wrapper);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        myGUI.wrapper.add(myGUI.rootCard, "rootCard");
        myGUI.wrapper.add(myGUI.inputCard, "inputCard");
        myGUI.wrapper.add(myGUI.courseInputCard, "courseInputCard");
        myGUI.wrapper.add(myGUI.adminCard, "adminCard");
        myGUI.wrapper.add(myGUI.additionCard, "additionCard");
        myGUI.wrapper.add(myGUI.adminPortalCard, "adminPortalCard");
        myGUI.wrapper.add(myGUI.editorCard, "editorCard");
        myGUI.wrapper.add(myGUI.DBViewerCard, "DBViewerCard");
        myGUI.wrapper.add(myGUI.semOneCard, "semOneCard");
    }

    private void resetVariables() {
        inputCourses.clear();
        usedRequirements.clear();
        genEdsToFulfill.clear();
        currentSchedule.clear();
        semestersLeft = 10;
        maxCredits = 15;
        creditsSpent = 0;
        honorsStudent = false;
        wantToExport = false;
    }

    private ArrayList<Course> loadAllCourses(ArrayList<Course> coursesLoaded) {
        //Loads all courses from the database into the coursesLoaded arraylist.
        String courseCode, description, attr, coreqList, requiredList, optionalList, type;
        int creditHours;
        Connection connection;

        String sql = "SELECT * FROM Courses " +
                "ORDER BY ID";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                courseCode = rs.getString("Code");
                description = rs.getString("Description");
                creditHours = rs.getInt("CreditHours");
                requiredList = rs.getString("ReqPrereqs");
                optionalList = rs.getString("GroupPrereqs");
                coreqList = rs.getString("Coreqs");
                attr = rs.getString("Attributes");
                type = rs.getString("Category");

                Course c = new Course(courseCode, description, type, attr, coreqList, requiredList, optionalList, creditHours);

                coursesLoaded.add(c);
            }

            return coursesLoaded;
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to obtain all courses the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void updateCreditsSpent(ArrayList<Course> courses) {
        //Updates the amount of credits spent according to courses in the current schedule's arraylist
        for(int i = 0; i < courses.size(); i++) {
            creditsSpent += courses.get(i).getCreditHours();
        }
    }

    private void updateUsedCourses(Course myCourse, int[] categoriesUsed) {
        //Updates how many courses have been used in each category for this schedule
        //per semester load
        //0) MainCS 1) IntroComputing 2) CS3000Above 3) NaturalScience 4) Statistics 5) Theory 6) Writing 7) Ethics 8) Uncategorized
        switch(myCourse.getType()) {
            case "MainCS":
                categoriesUsed[0]++;
                break;
            case "IntroComputing":
                categoriesUsed[1]++;
                break;
            case "CS3000Above":
                categoriesUsed[2]++;
                break;
            case "NaturalScience":
                categoriesUsed[3]++;
                break;
            case "Statistics":
                categoriesUsed[4]++;
                break;
            case "Theory":
                categoriesUsed[5]++;
                break;
            case "Writing":
                categoriesUsed[6]++;
                break;
            case "Ethics":
                categoriesUsed[7]++;
                break;
            case "Uncategorized":
                categoriesUsed[8]++;
                break;
        }
    }

    private void updateCategoryCreditCount(Course myCourse) {
        //Updates the amount of credits per category that exist in our current schedule, according to the parameter course's type.

        //private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100}
        //1) MainCS 2) IntroComputing 3) CS3000Above 4) NaturalScience 5) Statistics 6) Theory 7) Writing 8) Ethics 9) Uncategorized
        //private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        //private HashMap<String, Boolean> usedRequirements = new HashMap<>();
        switch(myCourse.getType()) {
            case "MainCS":
                categoryCreditCount[0] += myCourse.getCreditHours();
                if(categoryCreditCount[0] >= maxCategoryCredits[0]) usedRequirements.put("MainCS", true);
                break;
            case "IntroComputing":
                categoryCreditCount[1] += myCourse.getCreditHours();
                if(categoryCreditCount[1] >= maxCategoryCredits[1]) usedRequirements.put("IntroComputing", true);
                break;
            case "CS3000Above":
                categoryCreditCount[2] += myCourse.getCreditHours();
                if(categoryCreditCount[2] >= maxCategoryCredits[2]) usedRequirements.put("CS3000Above", true);
                break;
            case "NaturalScience":
                categoryCreditCount[3] += myCourse.getCreditHours();
                if(categoryCreditCount[3] >= maxCategoryCredits[3]) usedRequirements.put("NaturalScience", true);
                break;
            case "Statistics":
                categoryCreditCount[4] += myCourse.getCreditHours();
                if(categoryCreditCount[4] >= maxCategoryCredits[4]) usedRequirements.put("Statistics", true);
                break;
            case "Theory":
                categoryCreditCount[5] += myCourse.getCreditHours();
                if(categoryCreditCount[5] >= maxCategoryCredits[5]) usedRequirements.put("Theory", true);
                break;
            case "Writing":
                categoryCreditCount[6] += myCourse.getCreditHours();
                if(categoryCreditCount[6] >= maxCategoryCredits[6]) usedRequirements.put("Writing", true);
                break;
            case "Ethics":
                categoryCreditCount[7] += myCourse.getCreditHours();
                if(categoryCreditCount[7] >= maxCategoryCredits[7]) usedRequirements.put("Ethics", true);
                break;
            case "Uncategorized":
                categoryCreditCount[8] += myCourse.getCreditHours();
                if(categoryCreditCount[8] >= maxCategoryCredits[8]) usedRequirements.put("Uncategorized", true);
                break;
        }
    }

    private void updateCompletedRequirements() {
        //Updates the amount of credits with the corresponding requirements

        // private void updateCategoryCreditCount(Course myCourse) {
        // private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100}
        // 1) MainCS 2) IntroComputing 3) CS3000Above 4) NaturalScience 5) Statistics 6) Theory 7) Writing 8) Ethics 9) Uncategorized
        // private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        //Read from courses loaded and use the max amt of possible credits in the maincs category to get the max amt of credits for mainCS
        int cumulativeCSCredits = 0;
        for(int i = 0; i < coursesLoaded.size(); i++) {
            if(coursesLoaded.get(i).getType().equalsIgnoreCase("MainCS")) cumulativeCSCredits += coursesLoaded.get(i).getCreditHours();
        }

        maxCategoryCredits[0] = cumulativeCSCredits;

        //loop through courses loaded and if we have input a course that is in that category add to the cumulative credit count of that category
        for(int i = 0; i < coursesLoaded.size(); i++) {
            if(inputCourses.containsKey(coursesLoaded.get(i).getCourseCode())) {
                updateCategoryCreditCount(coursesLoaded.get(i));
            }
        }
    }

    private String extractTextFromType(String inputType) {
        //gets all courses of a specific type and returns the info as a string
        String output = "";
        String courseCode, description, attr, coreqList, requiredList, optionalList, type;
        int creditHours;

        Connection connection;

        String sql = "SELECT * FROM Courses " +
                "ORDER BY ID";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return "ERROR";
        }

        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                courseCode = rs.getString("Code");
                description = rs.getString("Description");
                creditHours = rs.getInt("CreditHours");
                requiredList = rs.getString("ReqPrereqs");
                optionalList = rs.getString("GroupPrereqs");
                coreqList = rs.getString("Coreqs");
                attr = rs.getString("Attributes");
                type = rs.getString("Category");

                if(!coreqList.equalsIgnoreCase("")) {
                    if(coreqList.charAt(0) == '!') coreqList = coreqList.substring(1);
                    if(coreqList.length() != 0) {
                        if (coreqList.charAt(coreqList.length() - 1) == '!')
                            coreqList = coreqList.substring(0, coreqList.length() - 1);
                    }
                }

                if(!optionalList.equalsIgnoreCase("")) {
                    if(optionalList.charAt(0) == '!') optionalList = optionalList.substring(1);
                    if(optionalList.length() != 0) {
                        if(optionalList.charAt(optionalList.length() - 1) == '!') optionalList = optionalList.substring(0, optionalList.length() - 1);
                    }
                }

                if(!requiredList.equalsIgnoreCase("")) {
                    if(requiredList.charAt(0) == '!') requiredList = requiredList.substring(1);
                    if(requiredList.length() != 0) {
                        if(requiredList.charAt(requiredList.length() - 1) == '!') requiredList = requiredList.substring(0, requiredList.length() - 1);
                    }
                }

                String[] extractedCoreqs = coreqList.split("-");
                String[] extractedRequired = requiredList.split("-");
                String[] extractedOptionalFirst = optionalList.split("!");

                if(type.equalsIgnoreCase(inputType)) {
                    output += (courseCode + " - " + description + ": " + creditHours + " credits; Corequisites: { "); //6 spaces for tab
                    int i;

                    for (i = 0; i < extractedCoreqs.length; i++) {
                        if(extractedCoreqs[i].equalsIgnoreCase("")) continue;

                        if (i != extractedCoreqs.length - 1) output += (extractedCoreqs[i] + ", ");
                        else output += extractedCoreqs[i];
                    }

                    output += (" }; Mutually Required Prerequisites: { ");

                    for (i = 0; i < extractedRequired.length; i++) {
                        if(extractedRequired[i].equalsIgnoreCase("")) continue;

                        if (i != extractedRequired.length - 1) output += (extractedRequired[i] + ", ");
                        else output += extractedRequired[i];
                    }

                    output += (" }; Group Prerequisites: { ");

                    for (i = 0; i < extractedOptionalFirst.length; i++) {
                        if(extractedOptionalFirst[i].equalsIgnoreCase("")) continue;

                        String[] pulledFromHyphen = extractedOptionalFirst[i].split("-");
                        output += "{";
                        for (int j = 0; j < pulledFromHyphen.length; j++) {
                            if(pulledFromHyphen[j].equalsIgnoreCase("")) continue;

                            if (j != pulledFromHyphen.length - 1) {
                                output += (pulledFromHyphen[j] + ", ");
                            }
                            else output += pulledFromHyphen[j];
                        }

                        if(i != extractedOptionalFirst.length - 1) output += "}, ";
                        else output += "}";
                    }

                    output += " }\n\n";
                }

            }
            return output;
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to obtain CS courses in the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return "ERROR";
        }
    }

    private boolean errorCheckCodes(String courseCode) {
        if(courseCode == "") {
            JOptionPane.showMessageDialog(frame,
                    "Please input a non-empty value for course code.",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } else if(courseCode.split(" ").length != 2) {
            JOptionPane.showMessageDialog(frame,
                    "Please input course code with the title and the number separated by one space: E.G. 'CPSC 2810'",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            try {
                Integer.parseInt(courseCode.split(" ")[1]);
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Please input a valid number in the second portion of your course code.",
                        "ERROR",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private int errorCheckCredits(String creditHours) {
        //error checks input to see whether new input credit hours is allowed
        int hours = -1;
        creditHours = creditHours.trim();

        if(creditHours == "") {
            JOptionPane.showMessageDialog(frame,
                    "Please input a non-empty value for credit hours.",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            creditHourField.setText("");
            return -1;
        } else {
            try {
                hours = Integer.parseInt(creditHours);
            } catch (NumberFormatException m) {
                JOptionPane.showMessageDialog(frame,
                        "Please input only a number value for the number of credit hours.",
                        "ERROR",
                        JOptionPane.WARNING_MESSAGE);
                creditHourField.setText("");
                return -1;
            }
        }

        if(hours < 0 || hours > 12) {
            JOptionPane.showMessageDialog(frame,
                    "Please input only positive numbers between 0 and 12 for the number of credit hours",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            creditHourField.setText("");
            return -1;
        }

        return hours;
    }

    public boolean checkCategory(String cat, int[] categoriesUsed) {
        //returns false if the category has already been fulfilled while scheduling

        //private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100}
        //0) MainCS 1) IntroComputing 2) CS3000Above 3) NaturalScience 4) Statistics 5) Theory 6) Writing 7) Ethics 8) Uncategorized
        //private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        switch(cat) {
            case "MainCS":
            case "Uncategorized":
                return true;
            case "IntroComputing":
                if(categoriesUsed[1] >= 1) return false;
                break;
            case "CS3000Above":
                if(categoriesUsed[2] >= 4) return false;
                break;
            case "NaturalScience":
                if(categoriesUsed[3] >= 1) return false;
                break;
            case "Statistics":
                if(categoriesUsed[4] >= 1) {
                    usedRequirements.put("Statistics", true);
                    return false;
                }
                break;
            case "Theory":
                if(categoriesUsed[5] >= 1) {
                    usedRequirements.put("Theory", true);
                    return false;
                }
                break;
            case "Writing":
                if(categoriesUsed[6] >= 1) {
                    usedRequirements.put("Writing", true);
                    return false;
                }
                break;
            case "Ethics":
                if(categoriesUsed[7] >= 1) return false;
                break;
        }

        return true;
    }

    private void addRequirementComboCats(JComboBox box) {
        box.addItem(new ComboItem("Main CS Requirement", "MainCS"));
        box.addItem(new ComboItem("Intro to Computing", "IntroComputing"));
        box.addItem(new ComboItem("CS 3000+ Elective", "CS3000Above"));
        box.addItem(new ComboItem("Natural Science", "NaturalScience"));
        box.addItem(new ComboItem("Statistics", "Statistics"));
        box.addItem(new ComboItem("Theory", "Theory"));
        box.addItem(new ComboItem("Writing", "Writing"));
        box.addItem(new ComboItem("Ethics", "Ethics"));
        box.addItem(new ComboItem("Uncategorized Prereq/Coreq", "Uncategorized"));
    }

    private boolean removeFromDBViaCode(String code) {
        Connection connection;

        String sql = "DELETE FROM Courses " +
                "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, code);
            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to remove the course " + code.toUpperCase() + " from the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean compareTypeToMaxCredits(Course myCourse) {
        //private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100}
        //1) MainCS 2) IntroComputing 3) CS3000Above 4) NaturalScience 5) Statistics 6) Theory 7) Writing 8) Ethics 9) Uncategorized
        //private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        switch(myCourse.getType()) {
            case "MainCS":
                if(myCourse.getCreditHours() + categoryCreditCount[0] <= maxCategoryCredits[0]) return true;
                break;
            case "IntroComputing":
                if(myCourse.getCreditHours() + categoryCreditCount[1] <= maxCategoryCredits[1]) return true;
                break;
            case "CS3000Above":
                if(myCourse.getCreditHours() + categoryCreditCount[2] <= maxCategoryCredits[2]) return true;
                break;
            case "NaturalScience":
                if(myCourse.getCreditHours() + categoryCreditCount[3] <= maxCategoryCredits[3]) return true;
                break;
            case "Statistics":
                if(myCourse.getCreditHours() + categoryCreditCount[4] <= maxCategoryCredits[4]) return true;
                break;
            case "Theory":
                if(myCourse.getCreditHours() + categoryCreditCount[5] <= maxCategoryCredits[5]) return true;
                break;
            case "Writing":
                if(myCourse.getCreditHours() + categoryCreditCount[6] <= maxCategoryCredits[6]) return true;
                break;
            case "Ethics":
                if(myCourse.getCreditHours() + categoryCreditCount[7] <= maxCategoryCredits[7]) return true;
                break;
            case "Uncategorized":
                if(myCourse.getCreditHours() + categoryCreditCount[8] <= maxCategoryCredits[8]) return true;
                break;
        }

        return false;
    }

    private void removeCreditCount(Course myCourse) {
        //private int[] maxCategoryCredits = {100, 8, 16, 16, 3, 3, 3, 6, 100}
        //1) MainCS 2) IntroComputing 3) CS3000Above 4) NaturalScience 5) Statistics 6) Theory 7) Writing 8) Ethics 9) Uncategorized
        //private int[] categoryCreditCount = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        //private HashMap<String, Boolean> usedRequirements = new HashMap<>();
        switch(myCourse.getType()) {
            case "MainCS":
                categoryCreditCount[0] -= myCourse.getCreditHours();
                if(categoryCreditCount[0] < maxCategoryCredits[0]) usedRequirements.remove("MainCS");
                break;
            case "IntroComputing":
                categoryCreditCount[1] -= myCourse.getCreditHours();
                if(categoryCreditCount[1] < maxCategoryCredits[1]) usedRequirements.remove("IntroComputing");
                break;
            case "CS3000Above":
                categoryCreditCount[2] -= myCourse.getCreditHours();
                if(categoryCreditCount[2] < maxCategoryCredits[2]) usedRequirements.remove("CS3000Above");
                break;
            case "NaturalScience":
                categoryCreditCount[3] -= myCourse.getCreditHours();
                if(categoryCreditCount[3] < maxCategoryCredits[3]) usedRequirements.remove("NaturalScience");
                break;
            case "Statistics":
                categoryCreditCount[4] -= myCourse.getCreditHours();
                if(categoryCreditCount[4] < maxCategoryCredits[4]) usedRequirements.remove("Writing");
                break;
            case "Theory":
                categoryCreditCount[5] -= myCourse.getCreditHours();
                if(categoryCreditCount[5] < maxCategoryCredits[5]) usedRequirements.remove("Writing");
                break;
            case "Writing":
                categoryCreditCount[6] -= myCourse.getCreditHours();
                if(categoryCreditCount[6] < maxCategoryCredits[6]) usedRequirements.remove("Writing");
                break;
            case "Ethics":
                categoryCreditCount[7] -= myCourse.getCreditHours();
                if(categoryCreditCount[7] < maxCategoryCredits[7]) usedRequirements.remove("Ethics");
                break;
            case "Uncategorized":
                categoryCreditCount[8] += myCourse.getCreditHours();
                if(categoryCreditCount[8] < maxCategoryCredits[8]) usedRequirements.remove("Uncategorized");
                break;
        }
    }

    private ArrayList<Course> getCoursesToSchedule(int maxCredits, ArrayList<String> categoriesToFulfill) {
        //String[] columnNames = {"Course Name", "Course Title", "Credits", "Curriculum Category"};
        int creditCounter = 0, coreqCreditCounter = 0;
        int iterations = 0, usedCounter = 0;
        ArrayList<Course> toSchedule = new ArrayList<>();

        //0) MainCS 1) IntroComputing 2) CS3000Above 3) NaturalScience 4) Statistics 5) Theory 6) Writing 7) Ethics 8) Uncategorized
        int[] categoriesUsed = {0, 0, 0, 0, 0, 0, 0, 0, 0};

        while(creditCounter <= maxCredits && iterations < 15) {
            for (int i = 0; i < categoriesToFulfill.size() && iterations < 15; i++) {
                if(usedCounter >= categoriesToFulfill.size()) return toSchedule;

                iterations++;

                if(usedRequirements.containsKey(categoriesToFulfill.get(i))) {
                    usedCounter++;
                    continue;
                }

                if(!checkCategory(categoriesToFulfill.get(i), categoriesUsed)) {
                    usedCounter++;
                    continue;
                }

                Course inputCourse;
                inputCourse = searchForCourse(categoriesToFulfill.get(i), maxCredits - creditCounter);
                coreqCreditCounter = 0;

                if (inputCourse == null) {
                    continue;
                } else {
                    ArrayList<String> preProcessedCoreqs = inputCourse.getProcessedCoreqs(inputCourses);
                    if (preProcessedCoreqs != null && preProcessedCoreqs.size() != 0) {
                        ArrayList<Course> coreqs = findCoreqsByString(preProcessedCoreqs);
                        for (int j = 0; j < coreqs.size(); j++) {
                            coreqCreditCounter += coreqs.get(j).getCreditHours();
                        }

                        if (coreqCreditCounter + inputCourse.getCreditHours() + creditCounter <= maxCredits) {
                            if(compareTypeToMaxCredits(inputCourse)) {
                                creditCounter += inputCourse.getCreditHours();

                                toSchedule.add(inputCourse);
                                inputCourses.put(inputCourse.getCourseCode(), 1);

                                for (int k = 0; k < coreqs.size(); k++) {
                                    creditCounter += coreqs.get(k).getCreditHours();

                                    toSchedule.add(coreqs.get(k));
                                    inputCourses.put(coreqs.get(k).getCourseCode(), 1);
                                    updateCategoryCreditCount(coreqs.get(k));
                                }

                                updateUsedCourses(inputCourse, categoriesUsed);
                                updateCategoryCreditCount(inputCourse);
                            }
                        }
                    } else {
                        if(creditCounter + inputCourse.getCreditHours() <= maxCredits && compareTypeToMaxCredits(inputCourse)) {
                            creditCounter += inputCourse.getCreditHours();
                            toSchedule.add(inputCourse);
                            inputCourses.put(inputCourse.getCourseCode(), 1);

                            updateUsedCourses(inputCourse, categoriesUsed);
                            updateCategoryCreditCount(inputCourse);
                        }
                    }
                }
            }
        }

        return toSchedule;
    }

    private ArrayList<Course> findCoreqsByString(ArrayList<String> coreqs) {
        ArrayList<Course> coreqOutput = new ArrayList<>();
        if(coreqs == null) return null;

        for(int i = 0; i < coreqs.size(); i++) {
            for(int j = 0; j < coursesLoaded.size(); j++) {
                if(coursesLoaded.get(j).getCourseCode().equalsIgnoreCase(coreqs.get(i))) {
                    coreqOutput.add(coursesLoaded.get(j));
                    break;
                }
            }
        }

        return coreqOutput;
    }

    private Course searchForCourse(String category, int creditsToSpend) {
        //0) MainCS 1) IntroComputing 2) CS3000Above 3) NaturalScience 4) Statistics 5) Theory 6) Writing 7) Ethics 8) Uncategorized
        for(int i = 0; i < coursesLoaded.size(); i++) {
            if(!inputCourses.containsKey(coursesLoaded.get(i).getCourseCode())) {
                if (coursesLoaded.get(i).getType().equalsIgnoreCase(category)) {
                    if(coursesLoaded.get(i).getCreditHours() <= creditsToSpend) {
                        if (coursesLoaded.get(i).hasAllRequirements(inputCourses)) {
                            return coursesLoaded.get(i);
                        }
                    }
                }
            }
        }

        usedRequirements.put(category, true);

        return null;
    }

    private void populateUsableCourses(JComboBox box, String category, int creditsToSpend) {
        //0) MainCS 1) IntroComputing 2) CS3000Above 3) NaturalScience 4) Statistics 5) Theory 6) Writing 7) Ethics 8) Uncategorized
        box.removeAllItems();
        for(int i = 0; i < coursesLoaded.size(); i++) {
            if(!inputCourses.containsKey(coursesLoaded.get(i).getCourseCode())) {
                if (coursesLoaded.get(i).getType().equalsIgnoreCase(category)) {
                    if(coursesLoaded.get(i).getCreditHours() <= creditsToSpend) {
                        if (coursesLoaded.get(i).hasAllRequirements(inputCourses)) {
                            box.addItem(new ComboItem(coursesLoaded.get(i).getCourseCode(), coursesLoaded.get(i).getCourseCode()));
                        }
                    }
                }
            }
        }

    }

    private void removeCoreqs(Course c) {
        ArrayList<String> preProcessedCoreqs = c.getProcessedCoreqs(inputCourses);
        ArrayList<Course> coreqs = findCoreqsByString(preProcessedCoreqs);

        if(coreqs != null && coreqs.size() > 0) {
            for (int k = 0; k < coreqs.size(); k++) {
                inputCourses.remove(coreqs.get(k).getCourseCode());
                creditsSpent -= coreqs.get(k).getCreditHours();
                removeCreditCount(coreqs.get(k));
                if(currentSchedule.contains(coreqs.get(k))) currentSchedule.remove(coreqs.get(k));
            }
        }
    }

    private int getGroupCredits(ArrayList<Course> courses, String category) {
        //gets the total amount of credits in the specified category
        int credits = 0;
        for(int i = 0; i < courses.size(); i++) {
            if(courses.get(i).getType().equalsIgnoreCase(category)) credits += courses.get(i).getCreditHours();
        }

        return credits;
    }

    private void switchCourse(Course to, Course from, int creditsToSpend, String category) {
        ArrayList<Course> toSchedule = new ArrayList<>();
        if(from == null || to == null) return;

        int coreqCreditCounter = 0;
        ArrayList<String> preProcessedCoreqs = to.getProcessedCoreqs(inputCourses);
        ArrayList<Course> coreqs = new ArrayList<>();

        if(preProcessedCoreqs != null) coreqs = findCoreqsByString(preProcessedCoreqs);

        if(coreqs.size() > 0 && coreqs != null && to.hasAllRequirements(inputCourses)) {
            inputCourses.remove(from.getCourseCode());
            creditsSpent -= from.getCreditHours();
            removeCreditCount(from);
            removeCoreqs(from);
            if(currentSchedule.contains(from)) currentSchedule.remove(from);

            for (int j = 0; j < coreqs.size(); j++) {
                coreqCreditCounter += coreqs.get(j).getCreditHours();
            }

            if (coreqCreditCounter + to.getCreditHours() <= creditsToSpend) {
                if (compareTypeToMaxCredits(to)) {
                    toSchedule.add(to);
                    inputCourses.put(to.getCourseCode(), 1);
                    updateCategoryCreditCount(to);
                    currentSchedule.add(to);

                    for (int k = 0; k < coreqs.size(); k++) {
                        toSchedule.add(coreqs.get(k));
                        inputCourses.put(coreqs.get(k).getCourseCode(), 1);
                        updateCategoryCreditCount(coreqs.get(k));
                        currentSchedule.add(coreqs.get(k));
                    }
                }
            }
        } else if(to.hasAllRequirements(inputCourses)) {
            inputCourses.remove(from.getCourseCode());
            removeCreditCount(from);
            if(currentSchedule.contains(from)) currentSchedule.remove(from);

            toSchedule.add(to);
            inputCourses.put(to.getCourseCode(), 1);
            updateCategoryCreditCount(to);
            currentSchedule.add(to);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change the course. Please try again.",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch(category) {
            case "IntroComputing":
                formatScheduleLabel(introComputingLabel, "IntroComputing", toSchedule);
                break;
            case "NaturalScience":
                formatScheduleLabel(natSciLabel, "NaturalScience", toSchedule);
                break;
            case "Ethics":
                formatScheduleLabel(ethicsLabel, "Ethics", toSchedule);
                break;
            case "Statistics":
                formatScheduleLabel(statisticsLabel, "Statistics", toSchedule);
                break;
            case "Theory":
                formatScheduleLabel(theoryLabel, "Theory", toSchedule);
                break;
            case "CS3000Above":
                formatScheduleLabel(CS3000Label, "CS3000Above", toSchedule);
                break;
            case "Writing":
                formatScheduleLabel(writingLabel, "Writing", toSchedule);
                break;
        }
    }

    private void addCourseEditCats(ArrayList<Course> DBCourses) {
        //adds db courses into list of those available to edit
        for(int i = 0; i < DBCourses.size(); i++) {
            editCourseSelector.addItem(new ComboItem(DBCourses.get(i).getCourseCode() + " - " + DBCourses.get(i).getDescription(), DBCourses.get(i).getCourseCode()));
        }
    }

    private void addFieldEditCats() {
        fieldEditComboBox.addItem(new ComboItem("Delete Item", "Delete Item"));
        fieldEditComboBox.addItem(new ComboItem("Change Course Code", "Change Course Code"));
        fieldEditComboBox.addItem(new ComboItem("Change Title", "Change Description"));
        fieldEditComboBox.addItem(new ComboItem("Change Credit Hours", "Change Credit Hours"));
        fieldEditComboBox.addItem(new ComboItem("Change Attributes", "Change Attributes"));
        fieldEditComboBox.addItem(new ComboItem("Change Corequisites", "Change Corequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Required Prerequisites", "Change Required Prerequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Group Prerequisites", "Change Group Prerequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to MainCS", "Change Category to MainCS"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to IntroComputing", "Change Category to IntroComputing"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to CS 3000+ Elective", "Change Category to CS 3000+ Elective"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to NaturalScience", "Change Category to NaturalScience"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to Statistics", "Change Category to Statistics"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to Theory", "Change Category to Theory"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to Writing", "Change Category to Writing"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to Ethics", "Change Category to Ethics"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to Uncategorized", "Change Category to Uncategorized"));
    }

    private boolean replaceSQLCourseCode(ArrayList<Course> DBCourses, String newCode, String oldCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET Code = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newCode);
            ps.setString(2, oldCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + oldCode.toUpperCase() + " to " + newCode.toUpperCase() + ". Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, oldCode) != null) findCourseViaCode(DBCourses, oldCode).setCourseCode(newCode);

        return true;
    }

    private boolean replaceSQLCreditHours(ArrayList<Course> DBCourses, int newHours, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET CreditHours = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, newHours);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s credit hours. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setCreditHours(newHours);

        return true;
    }

    private boolean replaceSQLDescription(ArrayList<Course> DBCourses, String newDescription, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET Description = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newDescription);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s description. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setDescription(newDescription);

        return true;
    }

    private boolean replaceSQLCoreqs(ArrayList<Course> DBCourses, String newCoreqs, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET Coreqs = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newCoreqs);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s corequisites. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setCoreqList(newCoreqs);

        return true;
    }

    private boolean replaceSQLReqPrereqs(ArrayList<Course> DBCourses, String newReqPrereqs, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET ReqPrereqs = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newReqPrereqs);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s required prerequisites. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setRequired(newReqPrereqs);

        return true;
    }

    private boolean replaceSQLGroupPrereqs(ArrayList<Course> DBCourses, String newGroupPrereqs, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET GroupPrereqs = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newGroupPrereqs);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s group prerequisites. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setOptional(newGroupPrereqs);

        return true;
    }

    private boolean replaceSQLCategory(String newCategory, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET Category = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newCategory);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s category. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean replaceSQLAttributes(ArrayList<Course> DBCourses, String newAttributes, String courseCode) {
        Connection connection;
        String sql = "UPDATE Courses "
                + "SET Attributes = ? "
                + "WHERE Code = ?";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newAttributes);
            ps.setString(2, courseCode);

            ps.executeUpdate();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to change course " + courseCode.toUpperCase() + "'s attributes. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if(findCourseViaCode(DBCourses, courseCode) != null) findCourseViaCode(DBCourses, courseCode).setAttr(newAttributes);

        return true;
    }

    private Course findCourseViaCode(ArrayList<Course> courseList, String code) {
        for(int i = 0; i < courseList.size(); i++) {
            if(courseList.get(i).getCourseCode().equalsIgnoreCase(code)) return courseList.get(i);
        }

        return null;
    }

    private Course findNonLabViaCategory(ArrayList<Course> courseList, String category) {
        for(int i = 0; i < courseList.size(); i++) {
            if(courseList.get(i).getType().equalsIgnoreCase(category) && !courseList.get(i).getDescription().toLowerCase().contains("laboratory") && !courseList.get(i).getDescription().toLowerCase().contains("recitation")) return courseList.get(i);
        }

        return null;
    }

    private void setComboSelected(JComboBox comboBox, String value) {
        ComboItem item;

        for (int i = 0; i < comboBox.getItemCount(); i++)
        {
            item = (ComboItem)comboBox.getItemAt(i);
            if (item.getValue().equalsIgnoreCase(value))
            {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private DefaultListModel<String> getListModelCodes() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        String courseCode;

        Connection connection;

        String sql = "SELECT * FROM Courses " +
                "ORDER BY Code";
        try {
            connection = DBUtil.getConnection();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to connect to the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try(PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                courseCode = rs.getString("Code");
                listModel.addElement(courseCode);
            }

        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "There was an error trying to obtain all courses the DB. Please try again.",
                    "ERROR: " + e.getErrorCode(),
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        for(int i = 0; i < genEdCategories.length; i++) {
            listModel.addElement(genEdCategories[i]);
        }

        return listModel;
    }

    public void formatScheduleLabel(JLabel myLabel, String category, ArrayList<Course> classesToAdd) {
        String toEdit = "";

        for(int i = 0; i < classesToAdd.size(); i++) {
            if(classesToAdd.get(i).getType().equalsIgnoreCase(category)) {
                if (i != classesToAdd.size() - 1)
                    toEdit += " " + classesToAdd.get(i).getCourseCode() + " - " + classesToAdd.get(i).getDescription() + " - " + classesToAdd.get(i).getCreditHours() + " credits;";
                else
                    toEdit += " " + classesToAdd.get(i).getCourseCode() + " - " + classesToAdd.get(i).getDescription() + " - " + classesToAdd.get(i).getCreditHours() + " credits";
            }
        }

        myLabel.setText(toEdit);
    }

    public void formatGenEdLabel(JLabel myLabel, String category) {
        String toEdit = "";
        toEdit += category;
        myLabel.setText(toEdit.toUpperCase());
    }

    public void updateSchedule() {
        //The function that actually writes to the schedule from the algorithm reading DB info
        ArrayList<String> categoriesToFulfill = new ArrayList<>();
        currentSchedule.clear();

        creditsSpent = 0;

        switch(semestersLeft) {
            case 8:
                //private ArrayList<Course> getCoursesToSchedule(int maxCredits, String[] categoriesToFulfill)
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER ONE");
                for(int i = 0; i < mainCategories.length && i < 3; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 7:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER TWO");
                for(int i = 0; i < mainCategories.length && i < 3; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                break;
            case 6:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER THREE");
                for(int i = 0; i < mainCategories.length && i < 3; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 5:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER FOUR");
                for(int i = 0; i < mainCategories.length && i < 5; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 4:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER FIVE");
                for(int i = 0; i < mainCategories.length && i < 5; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 3:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER SIX");
                for(int i = 0; i < mainCategories.length && i < 6; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                }

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 2:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER SEVEN");
                for(int i = 0; i < mainCategories.length && i < 7; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));
                break;
            case 1:
                semesterTitle.setText("Clemson Undergraduate CS Scheduler - SEMESTER EIGHT");
                for(int i = 0; i < mainCategories.length && i < 7; i++) {
                    if(!usedRequirements.containsKey(mainCategories[i])) categoriesToFulfill.add(mainCategories[i]);
                }

                if(genEdsToFulfill.size() > 0) {
                    formatGenEdLabel(genEdLabel, genEdsToFulfill.get(0));
                    inputCourses.put(genEdsToFulfill.get(0), 1);
                    genEdsToFulfill.remove(0);

                    creditsSpent += 3;
                    currentSchedule = getCoursesToSchedule(maxCredits - 3, categoriesToFulfill);
                } else currentSchedule = getCoursesToSchedule(maxCredits, categoriesToFulfill);

                updateCreditsSpent(currentSchedule);
                formatScheduleLabel(mainCSLabel, "MainCS", currentSchedule);
                formatScheduleLabel(introComputingLabel, "IntroComputing", currentSchedule);
                formatScheduleLabel(natSciLabel, "NaturalScience", currentSchedule);
                formatScheduleLabel(ethicsLabel, "Ethics", currentSchedule);
                formatScheduleLabel(statisticsLabel, "Statistics", currentSchedule);
                formatScheduleLabel(theoryLabel, "Theory", currentSchedule);
                formatScheduleLabel(CS3000Label, "CS3000Above", currentSchedule);
                formatScheduleLabel(writingLabel, "Writing", currentSchedule);

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                confirmScheduleButton.setText("Exit to Menu!");
                break;
            default:
                return;
        }

        creditLabel.setText(Integer.toString(creditsSpent));

        semestersLeft--;
    }

    static void printToFile() {
        String directoryString = "ScheduleOutFiles/";
        Path filePath;
        String fileName = "";
        String inputName = "";
        int multipleCounter = 0;

        LocalDate currentTime = LocalDate.now();

        fileName += Integer.toString(currentTime.getYear()) + Integer.toString(currentTime.getDayOfMonth()) + Integer.toString(currentTime.getMonthValue());
        fileName.trim();
        inputName = fileName + ".txt";

        while(true) {
            try {
                filePath = Paths.get(directoryString, inputName);
            } catch (InvalidPathException e) {
                multipleCounter++;
                fileName += multipleCounter;
                fileName.trim();
                inputName = fileName + ".txt";
                continue;
            }

            break;
        }

        //check to make sure file does not already exist
        while(Files.exists(filePath)) {
            try {
                multipleCounter++;
                fileName += multipleCounter;
                fileName.trim();
                inputName = fileName + ".txt";
            } catch (InvalidPathException e) {
                multipleCounter++;
                fileName += multipleCounter;
                fileName.trim();
                inputName = fileName + ".txt";
            }

            filePath = Paths.get(directoryString, inputName);
        }

        boolean exception = false;
        //attempt creating file within directory
        while(true) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                System.out.println("IOException encountered.");
                exception = true;
                filePath = Paths.get(directoryString, inputName);
                continue;
            }

            break;
        }

        //if(exception) return;

        File myFile = filePath.toFile();

        try(PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(myFile)))) {
            for(int i = 0; i < schedules.size(); i++) {
                printer.println("SEMESTER " + (i + 1) + ": ");
                for(int j = 0; j < schedules.get(i).size(); j++) {
                    printer.println(schedules.get(i).get(j).getCourseCode() + "-" + schedules.get(i).get(j).getDescription() + ": " + schedules.get(i).get(j).getCreditHours() + " credit hours; Category in Curriculum: " + schedules.get(i).get(j).getType());
                }

                printer.println();
            }

        } catch(IOException e) {
            System.out.println("\nIOException encountered. Please check your directory path.");
        }
    }

    public ScheduleGUI() {
        continueButtonOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllCourses(coursesLoaded);
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "inputCard");
            }
        });
        continueButtonTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "courseInputCard");

                inputList.setModel(getListModelCodes());
                semestersLeft = semesterSlider.getValue();
                maxCredits = creditSlider.getValue();
            }
        });
        adminContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllCourses(coursesLoaded);
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminCard");
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userText = usernameInput.getText().trim();
                String passText = String.valueOf(passwordInput.getPassword()).trim();

                //add sql stuff here!

                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        confirmExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseCode = courseField.getText();
                String courseTitle = titleField.getText();
                String attributes = attributeField.getText().trim();
                String coreqs = coreqField.getText().trim();
                String requiredPrereqs = reqPreField.getText().trim();
                String groupPrereqs = groupPreField.getText().trim();
                String creditHours = creditHourField.getText().trim();
                String category;

                if(!errorCheckCodes(courseCode)) {
                    courseField.setText("");
                    return;
                }

                int hours = errorCheckCredits(creditHours);
                if(hours == -1) {
                    creditHourField.setText("");
                    return;
                }

                Object temp = requirementsBox.getSelectedItem();
                category = ((ComboItem)temp).getValue();

                //if our error checking was fine we can now insert into course table.
                String sql =
                        "INSERT INTO Courses " +
                                "(Code, Description, CreditHours, GroupPrereqs, ReqPrereqs, Coreqs, Attributes, Category) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?) ";
                Connection connection = null;

                try {
                    connection = DBUtil.getConnection();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to connect to the DB. Please try again.",
                            "ERROR" + ex.getErrorCode(),
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, courseCode);
                    ps.setString(2, courseTitle);
                    ps.setInt(3, hours);
                    ps.setString(4, groupPrereqs);
                    ps.setString(5, requiredPrereqs);
                    ps.setString(6, coreqs);
                    ps.setString(7, attributes);
                    ps.setString(8, category);

                    ps.executeUpdate();
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to insert the values into the DB. Please try again.",
                            "ERROR: " + ex.getErrorCode(),
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                courseField.setText("");
                attributeField.setText("");
                coreqField.setText("");
                reqPreField.setText("");
                groupPreField.setText("");
                titleField.setText("");
                creditHourField.setText("");

                JOptionPane.showMessageDialog(frame,
                        "You have successfully inserted " + courseTitle + " into the database.",
                        "Success!",
                        JOptionPane.INFORMATION_MESSAGE);

                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        confirmAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseCode = courseField.getText();
                String courseTitle = titleField.getText();
                String attributes = attributeField.getText().trim();
                String coreqs = coreqField.getText().trim();
                String requiredPrereqs = reqPreField.getText().trim();
                String groupPrereqs = groupPreField.getText().trim();
                String creditHours = creditHourField.getText().trim();
                String category;

                if(!errorCheckCodes(courseCode)) {
                    courseField.setText("");
                    return;
                }

                int hours = errorCheckCredits(creditHours);

                if(hours == -1) {
                    creditHourField.setText("");
                    return;
                }

                Object temp = requirementsBox.getSelectedItem();
                category = ((ComboItem)temp).getValue();

                //if our error checking was fine we can now insert into course table.
                String sql =
                        "INSERT INTO Courses " +
                                "(Code, Description, CreditHours, GroupPrereqs, ReqPrereqs, Coreqs, Attributes, Category) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?, ?) ";
                Connection connection = null;

                try {
                    connection = DBUtil.getConnection();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to connect to the DB. Please try again.",
                            "ERROR: " + ex.getErrorCode(),
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, courseCode);
                    ps.setString(2, courseTitle);
                    ps.setInt(3, hours);
                    ps.setString(4, groupPrereqs);
                    ps.setString(5, requiredPrereqs);
                    ps.setString(6, coreqs);
                    ps.setString(7, attributes);
                    ps.setString(8, category);

                    ps.executeUpdate();
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to insert the values into the DB. Please try again.",
                            "ERROR: " + ex.getErrorCode(),
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(frame,
                        "You have successfully inserted " + courseTitle + " into the database.",
                        "Success!",
                        JOptionPane.INFORMATION_MESSAGE);

                courseField.setText("");
                attributeField.setText("");
                coreqField.setText("");
                reqPreField.setText("");
                groupPreField.setText("");
                titleField.setText("");
                creditHourField.setText("");
            }
        });
        addCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "additionCard");
                addRequirementComboCats(requirementsBox);
                setComboSelected(requirementsBox, "MainCS");
            }
        });
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wantToExport = !wantToExport;
            }
        });
        editCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "editorCard");

                editCourseSelector.removeAllItems();
                addCourseEditCats(coursesLoaded);

                fieldEditComboBox.removeAllItems();
                addFieldEditCats();
            }
        });

        viewCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "DBViewerCard");
                typeToView.removeAllItems();
                addRequirementComboCats(typeToView);
            }
        });
        cancelAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        exitToMainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "rootCard");
            }
        });
        cancelToPortalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        confirmEditToPortalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = fieldEditComboBox.getSelectedItem();
                String actionType = "";
                if (temp != null) {
                    actionType = ((ComboItem)temp).getValue();
                }
                boolean noErrors = true;

                temp = editCourseSelector.getSelectedItem();
                String courseCode = "";
                if (temp != null) {
                    courseCode = ((ComboItem)temp).getValue();
                }

                String newInput = newEditValue.getText();

                if(coursesLoaded == null) noErrors = false;

                if(noErrors) {
                    switch (actionType) {
                        case "Delete Item":
                            if(newInput.trim().equalsIgnoreCase("DELETE")) noErrors = removeFromDBViaCode(courseCode);
                            else {
                                JOptionPane.showMessageDialog(frame,
                                        "Please enter \"DELETE\" into the text box to confirm deletion of the course.",
                                        "ERROR",
                                        JOptionPane.WARNING_MESSAGE);
                                noErrors = false;
                            }
                            break;
                        case "Change Course Code":
                            if (errorCheckCodes(newInput)) {
                                noErrors = replaceSQLCourseCode(coursesLoaded, newInput, courseCode);
                            } else noErrors = false;
                            break;
                        case "Change Description":
                            noErrors = replaceSQLDescription(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Credit Hours":
                            int hours = errorCheckCredits(newInput);
                            if(hours == -1) noErrors = false;
                            else {
                                noErrors = replaceSQLCreditHours(coursesLoaded, hours, courseCode);
                            }
                            break;
                        case "Change Attributes":
                            noErrors = replaceSQLAttributes(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Corequisites":
                            noErrors = replaceSQLCoreqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Required Prerequisites":
                            noErrors = replaceSQLReqPrereqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Group Prerequisites":
                            noErrors = replaceSQLGroupPrereqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Category to MainCS":
                            noErrors = replaceSQLCategory("MainCS", courseCode);
                            break;
                        case "Change Category to IntroComputing":
                            noErrors = replaceSQLCategory("IntroComputing", courseCode);
                            break;
                        case "Change Category to CS 3000+ Elective":
                            noErrors = replaceSQLCategory("CS3000Above", courseCode);
                            break;
                        case "Change Category to NaturalScience":
                            noErrors = replaceSQLCategory("NaturalScience", courseCode);
                            break;
                        case "Change Category to Statistics":
                            noErrors = replaceSQLCategory("Statistics", courseCode);
                            break;
                        case "Change Category to Theory":
                            noErrors = replaceSQLCategory("Theory", courseCode);
                            break;
                        case "Change Category to Writing":
                            noErrors = replaceSQLCategory("Writing", courseCode);
                            break;
                        case "Change Category to Ethics":
                            noErrors = replaceSQLCategory("Ethics", courseCode);
                            break;
                        case "Change Category to Uncategorized":
                            noErrors = replaceSQLCategory("Uncategorized", courseCode);
                            break;
                        default:
                            noErrors = false;
                    }
                }

                if(noErrors) {
                    JOptionPane.showMessageDialog(frame,
                            "You have successfully edited " + courseCode.toUpperCase() + " and updated the database to match.",
                            "Success!",
                            JOptionPane.INFORMATION_MESSAGE);

                    newEditValue.setText("");

                    CardLayout cl = (CardLayout) wrapper.getLayout();
                    cl.show(wrapper, "adminPortalCard");
                } else {
                    newEditValue.setText("");
                }
            }
        });
        confirmEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = fieldEditComboBox.getSelectedItem();
                String actionType = "";
                if (temp != null) {
                    actionType = ((ComboItem)temp).getValue();
                }
                boolean noErrors = true;

                temp = editCourseSelector.getSelectedItem();
                String courseCode = "";
                if (temp != null) {
                    courseCode = ((ComboItem)temp).getValue();
                }

                String newInput = newEditValue.getText();

                if(coursesLoaded == null) noErrors = false;

                if(noErrors) {
                    switch (actionType) {
                        case "Delete Item":
                            if(newInput.trim().equalsIgnoreCase("DELETE")) noErrors = removeFromDBViaCode(courseCode);
                            else {
                                JOptionPane.showMessageDialog(frame,
                                        "Please enter \"DELETE\" into the text box to confirm deletion of the course.",
                                        "ERROR",
                                        JOptionPane.WARNING_MESSAGE);
                                noErrors = false;
                            }
                            break;
                        case "Change Course Code":
                            if (errorCheckCodes(newInput)) {
                                noErrors = replaceSQLCourseCode(coursesLoaded, newInput, courseCode);
                            } else noErrors = false;
                            break;
                        case "Change Description":
                            noErrors = replaceSQLDescription(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Credit Hours":
                            int hours = errorCheckCredits(newInput);
                            if(hours == -1) noErrors = false;
                            else {
                                noErrors = replaceSQLCreditHours(coursesLoaded, hours, courseCode);
                            }
                            break;
                        case "Change Attributes":
                            noErrors = replaceSQLAttributes(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Corequisites":
                            noErrors = replaceSQLCoreqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Required Prerequisites":
                            noErrors = replaceSQLReqPrereqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Group Prerequisites":
                            noErrors = replaceSQLGroupPrereqs(coursesLoaded, newInput, courseCode);
                            break;
                        case "Change Category to MainCS":
                            noErrors = replaceSQLCategory("MainCS", courseCode);
                            break;
                        case "Change Category to IntroComputing":
                            noErrors = replaceSQLCategory("IntroComputing", courseCode);
                            break;
                        case "Change Category to CS 3000+ Elective":
                            noErrors = replaceSQLCategory("CS3000Above", courseCode);
                            break;
                        case "Change Category to NaturalScience":
                            noErrors = replaceSQLCategory("NaturalScience", courseCode);
                            break;
                        case "Change Category to Statistics":
                            noErrors = replaceSQLCategory("Statistics", courseCode);
                            break;
                        case "Change Category to Theory":
                            noErrors = replaceSQLCategory("Theory", courseCode);
                            break;
                        case "Change Category to Writing":
                            noErrors = replaceSQLCategory("Writing", courseCode);
                            break;
                        case "Change Category to Ethics":
                            noErrors = replaceSQLCategory("Ethics", courseCode);
                            break;
                        case "Change Category to Uncategorized":
                            noErrors = replaceSQLCategory("Uncategorized", courseCode);
                            break;
                        default:
                            noErrors = false;
                    }
                }

                if(noErrors) {
                    JOptionPane.showMessageDialog(frame,
                            "You have successfully edited " + courseCode.toUpperCase() + " and updated the database to match.",
                            "Success!",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                newEditValue.setText("");
            }
        });
        typeToView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = typeToView.getSelectedItem();
                String courseCategory = "";
                if (temp != null) {
                    courseCategory = ((ComboItem)temp).getValue();
                }

                textToView.setText(extractTextFromType(courseCategory));
            }
        });
        exitToPortalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        confirmContinueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> preProcessed = inputList.getSelectedValuesList();

                for (String s : preProcessed) {
                    inputCourses.put(s, 1);
                }

                for(int i = 0; i < genEdCategories.length; i++) {
                    if(!inputCourses.containsKey(genEdCategories[i])) {
                        if(genEdCategories[i].equalsIgnoreCase("Social Sciences (6 Credits)")) {
                            genEdsToFulfill.add("Social Sciences (3 Credits)");
                            genEdsToFulfill.add("Social Sciences (3 Credits)");
                        } else genEdsToFulfill.add(genEdCategories[i]);
                    }
                }

                updateCompletedRequirements();
                updateSchedule();
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "semOneCard");
            }
        });
        introCompBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = introCompBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "IntroComputing");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"), "IntroComputing");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        natSciBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = natSciBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "NaturalScience");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"), "NaturalScience");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        ethicsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = ethicsBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "Ethics");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"), "Ethics");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        statisticsBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = statisticsBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "Statistics");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"), "Statistics");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        theoryBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = theoryBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "Theory");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"), "Theory");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        CS3000Box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = CS3000Box.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "CS3000Above");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"), "CS3000Above");
                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        writingBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object temp = writingBox.getSelectedItem();
                String courseLabel = "";
                if (temp != null) {
                    courseLabel = ((ComboItem)temp).getValue();
                }

                Course to = findCourseViaCode(coursesLoaded, courseLabel);
                Course from = findNonLabViaCategory(currentSchedule, "Writing");

                switchCourse(to, from, maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"), "Writing");

                populateUsableCourses(introCompBox, "IntroComputing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "IntroComputing"));
                populateUsableCourses(natSciBox, "NaturalScience", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "NaturalScience"));
                populateUsableCourses(ethicsBox, "Ethics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Ethics"));
                populateUsableCourses(statisticsBox, "Statistics", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Statistics"));
                populateUsableCourses(theoryBox, "Theory", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Theory"));
                populateUsableCourses(CS3000Box, "CS3000Above", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "CS3000Above"));
                populateUsableCourses(writingBox, "Writing", maxCredits - creditsSpent + getGroupCredits(currentSchedule, "Writing"));

                creditsSpent = getGroupCredits(currentSchedule, "MainCS") + getGroupCredits(currentSchedule, "IntroComputing") + getGroupCredits(currentSchedule, "NaturalScience") + getGroupCredits(currentSchedule, "Ethics") + getGroupCredits(currentSchedule, "Statistics") + getGroupCredits(currentSchedule, "Theory") + getGroupCredits(currentSchedule, "CS3000Above") + getGroupCredits(currentSchedule, "Writing");
                creditLabel.setText(Integer.toString(creditsSpent));
            }
        });
        confirmScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(semestersLeft > 1) {
                    schedules.add(currentSchedule);
                    creditsSpent = 0;
                    updateSchedule();
                } else {
                    schedules.add(currentSchedule);
                    if(wantToExport) printToFile();
                    resetVariables();
                    schedules.clear();
                    CardLayout cl = (CardLayout) wrapper.getLayout();
                    cl.show(wrapper, "rootCard");
                }
            }
        });
    }
}
