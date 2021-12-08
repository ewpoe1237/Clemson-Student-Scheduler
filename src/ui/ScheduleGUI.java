package ui;

import db.DBUtil;
import scheduling.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private JList inputList;
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

    private int semestersLeft = 10, maxCredits = 15;
    private boolean honorsStudent = false, wantToExport = false;

    private HashMap<String, Integer> inputCourses;
    private HashMap<String, Integer> usedCourses;

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

    private int errorCheckCreditEdit(String creditHours) {
        int hours = -1;
        creditHours = creditHours.trim();

        if(creditHours == "") {
            JOptionPane.showMessageDialog(frame,
                    "Please input a non-empty value for credit hours.",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            newEditValue.setText("");
            return -1;
        } else {
            try {
                hours = Integer.parseInt(creditHours);
            } catch (NumberFormatException m) {
                JOptionPane.showMessageDialog(frame,
                        "Please input only a number value for the number of credit hours.",
                        "ERROR",
                        JOptionPane.WARNING_MESSAGE);
                newEditValue.setText("");
                return -1;
            }
        }

        if(hours < 0 || hours > 12) {
            JOptionPane.showMessageDialog(frame,
                    "Please input only positive numbers between 0 and 12 for the number of credit hours",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
            newEditValue.setText("");
            return -1;
        }

        return hours;
    }

    private void addRequirementComboCats() {
        requirementsBox.addItem(new ComboItem("Main CS Requirement", "MainCS"));
        requirementsBox.addItem(new ComboItem("Intro to Computing", "IntroComputing"));
        requirementsBox.addItem(new ComboItem("CS 3000+ Elective", "CS3000Above"));
        requirementsBox.addItem(new ComboItem("Natural Science", "NaturalScience"));
        requirementsBox.addItem(new ComboItem("Theory", "Theory"));
        requirementsBox.addItem(new ComboItem("Writing", "Writing"));
        requirementsBox.addItem(new ComboItem("Ethics", "Ethics"));
        requirementsBox.addItem(new ComboItem("Uncategorized Prereq/Coreq", "Uncategorized"));
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

    private ArrayList<Course> loadAllCourses() {
        ArrayList<Course> coursesLoaded = new ArrayList<>();
        String courseCode, description, attr, coreqList, requiredList, optionalList, type;
        int creditHours;

        coursesLoaded.clear();
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

    private void addCourseEditCats(ArrayList<Course> DBCourses) {
        for(int i = 0; i < DBCourses.size(); i++) {
            editCourseSelector.addItem(new ComboItem(DBCourses.get(i).getCourseCode(), DBCourses.get(i).getCourseCode()));
        }
    }

    private void addFieldEditCats() {
        fieldEditComboBox.addItem(new ComboItem("Delete Item", "Delete Item"));
        fieldEditComboBox.addItem(new ComboItem("Change Course Code", "Change Course Code"));
        fieldEditComboBox.addItem(new ComboItem("Change Description", "Change Description"));
        fieldEditComboBox.addItem(new ComboItem("Change Credit Hours", "Change Credit Hours"));
        fieldEditComboBox.addItem(new ComboItem("Change Attributes", "Change Attributes"));
        fieldEditComboBox.addItem(new ComboItem("Change Corequisites", "Change Corequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Required Prerequisites", "Change Required Prerequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Group Prerequisites", "Change Group Prerequisites"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to MainCS", "Change Category to MainCS"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to IntroComputing", "Change Category to IntroComputing"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to CS 3000+ Elective", "Change Category to CS 3000+ Elective"));
        fieldEditComboBox.addItem(new ComboItem("Change Category to NaturalScience", "Change Category to NaturalScience"));
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

    private void setComboSelected(JComboBox comboBox, String value)
    {
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

    public ScheduleGUI() {
        continueButtonOne.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "inputCard");
            }
        });
        continueButtonTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "courseInputCard");

                semestersLeft = semesterSlider.getValue();
                maxCredits = creditSlider.getValue();
            }
        });
        honorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                honorsStudent = !honorsStudent;
            }
        });
        adminContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                addRequirementComboCats();
                setComboSelected(requirementsBox, "MainCS");
            }
        });
        honorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                honorsStudent = !honorsStudent;
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
                ArrayList<Course> DBCourses = loadAllCourses();

                addCourseEditCats(DBCourses);
                addFieldEditCats();
            }
        });

        viewCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "DBViewerCard");
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
                String actionType = ((ComboItem)temp).getValue();
                boolean noErrors = true;

                temp = editCourseSelector.getSelectedItem();
                String courseCode = ((ComboItem)temp).getValue();

                String newInput = newEditValue.getText();

                ArrayList<Course> DBCourses = loadAllCourses();
                if(DBCourses == null) noErrors = false;

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
                                noErrors = replaceSQLCourseCode(DBCourses, newInput, courseCode);
                            } else noErrors = false;
                            break;
                        case "Change Description":
                            noErrors = replaceSQLDescription(DBCourses, newInput, courseCode);
                            break;
                        case "Change Credit Hours":
                            int hours = errorCheckCreditEdit(newInput);
                            if(hours == -1) noErrors = false;
                            else {
                                noErrors = replaceSQLCreditHours(DBCourses, hours, courseCode);
                            }
                            break;
                        case "Change Attributes":
                            noErrors = replaceSQLAttributes(DBCourses, newInput, courseCode);
                            break;
                        case "Change Corequisites":
                            noErrors = replaceSQLCoreqs(DBCourses, newInput, courseCode);
                            break;
                        case "Change Required Prerequisites":
                            noErrors = replaceSQLReqPrereqs(DBCourses, newInput, courseCode);
                            break;
                        case "Change Group Prerequisites":
                            noErrors = replaceSQLGroupPrereqs(DBCourses, newInput, courseCode);
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
                String actionType = ((ComboItem)temp).getValue();
                boolean noErrors = true;

                temp = editCourseSelector.getSelectedItem();
                String courseCode = ((ComboItem)temp).getValue();

                String newInput = newEditValue.getText();

                ArrayList<Course> DBCourses = loadAllCourses();
                if(DBCourses == null) noErrors = false;

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
                                noErrors = replaceSQLCourseCode(DBCourses, newInput, courseCode);
                            } else noErrors = false;
                            break;
                        case "Change Description":
                            noErrors = replaceSQLDescription(DBCourses, newInput, courseCode);
                            break;
                        case "Change Credit Hours":
                            int hours = errorCheckCreditEdit(newInput);
                            if(hours == -1) noErrors = false;
                            else {
                                noErrors = replaceSQLCreditHours(DBCourses, hours, courseCode);
                            }
                            break;
                        case "Change Attributes":
                            noErrors = replaceSQLAttributes(DBCourses, newInput, courseCode);
                            break;
                        case "Change Corequisites":
                            noErrors = replaceSQLCoreqs(DBCourses, newInput, courseCode);
                            break;
                        case "Change Required Prerequisites":
                            noErrors = replaceSQLReqPrereqs(DBCourses, newInput, courseCode);
                            break;
                        case "Change Group Prerequisites":
                            noErrors = replaceSQLGroupPrereqs(DBCourses, newInput, courseCode);
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
    }
}
