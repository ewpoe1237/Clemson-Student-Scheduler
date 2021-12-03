package ui;

import db.DBException;
import db.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private JComboBox editSelector;
    private JPanel DBViewerCard;
    private JPanel viewerPanel;
    private JButton editCoursesButton;
    private JButton viewCoursesButton;
    private JButton cancelAndExitButton;
    private JButton exitToMainMenuButton;
    private JButton confirmEditButton;
    private JButton cancelToPortalButton;
    private JButton confirmToPortalButton;
    private JComboBox editorComboBox;
    private JTextField textField1;
    private JRadioButton introToComputingRadioButton;
    private JRadioButton naturalScienceRadioButton;
    private JRadioButton theoryRadioButton;
    private JRadioButton writingRadioButton;
    private JRadioButton csElectiveRadioButton;

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

        if(creditHours == "") {
            JOptionPane.showMessageDialog(frame,
                    "Please input a non-empty value for credit hours.",
                    "ERROR",
                    JOptionPane.WARNING_MESSAGE);
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

        return hours;
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
                boolean introToComp = introToComputingRadioButton.isSelected();
                boolean naturalSci = naturalScienceRadioButton.isSelected();
                boolean theory = theoryRadioButton.isSelected();
                boolean writing = writingRadioButton.isSelected();
                boolean csElective = csElectiveRadioButton.isSelected();
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

                if(introToComp) category = "IntroComputing";
                else if(naturalSci) category = "NaturalScience";
                else if(theory) category = "Theory";
                else if(writing) category = "Writing";
                else if(csElective) category = "CSElective";
                else category = "MainCS";

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
                boolean introToComp = introToComputingRadioButton.isSelected();
                boolean naturalSci = naturalScienceRadioButton.isSelected();
                boolean theory = theoryRadioButton.isSelected();
                boolean writing = writingRadioButton.isSelected();
                boolean csElective = csElectiveRadioButton.isSelected();
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

                if(introToComp) category = "IntroComputing";
                else if(naturalSci) category = "NaturalScience";
                else if(theory) category = "Theory";
                else if(writing) category = "Writing";
                else if(csElective) category = "CSElective";
                else category = "MainCS";

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
        confirmToPortalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "adminPortalCard");
            }
        });
        introToComputingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(introToComputingRadioButton.isSelected()) {
                    if (naturalScienceRadioButton.isSelected()) naturalScienceRadioButton.setSelected(false);
                    if (theoryRadioButton.isSelected()) theoryRadioButton.setSelected(false);
                    if(writingRadioButton.isSelected()) writingRadioButton.setSelected(false);
                    if(csElectiveRadioButton.isSelected()) csElectiveRadioButton.setSelected(false);
                }
            }
        });
        naturalScienceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(naturalScienceRadioButton.isSelected()) {
                    if (introToComputingRadioButton.isSelected()) introToComputingRadioButton.setSelected(false);
                    if (theoryRadioButton.isSelected()) theoryRadioButton.setSelected(false);
                    if(writingRadioButton.isSelected()) writingRadioButton.setSelected(false);
                    if(csElectiveRadioButton.isSelected()) csElectiveRadioButton.setSelected(false);
                }
            }
        });
        theoryRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(theoryRadioButton.isSelected()) {
                    if (naturalScienceRadioButton.isSelected()) naturalScienceRadioButton.setSelected(false);
                    if (introToComputingRadioButton.isSelected()) introToComputingRadioButton.setSelected(false);
                    if(writingRadioButton.isSelected()) writingRadioButton.setSelected(false);
                    if(csElectiveRadioButton.isSelected()) csElectiveRadioButton.setSelected(false);
                }
            }
        });
        writingRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(writingRadioButton.isSelected()) {
                    if (naturalScienceRadioButton.isSelected()) naturalScienceRadioButton.setSelected(false);
                    if (theoryRadioButton.isSelected()) theoryRadioButton.setSelected(false);
                    if(introToComputingRadioButton.isSelected()) introToComputingRadioButton.setSelected(false);
                    if(csElectiveRadioButton.isSelected()) csElectiveRadioButton.setSelected(false);
                }
            }
        });

        csElectiveRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(csElectiveRadioButton.isSelected()) {
                    if (naturalScienceRadioButton.isSelected()) naturalScienceRadioButton.setSelected(false);
                    if (theoryRadioButton.isSelected()) theoryRadioButton.setSelected(false);
                    if(writingRadioButton.isSelected()) writingRadioButton.setSelected(false);
                    if(introToComputingRadioButton.isSelected()) introToComputingRadioButton.setSelected(false);
                }
            }
        });
    }
}
