package ui;

import db.DBException;
import db.DBUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    private JRadioButton areYouInTheRadioButton;
    private JButton adminContinue;
    private JPanel adminCard;
    private JPanel adminLogin;
    private JTextArea usernameInput;
    private JPasswordField passwordInput;
    private JButton loginButton;
    private JPanel configCard;
    private JPanel configPanel;
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

    private int semestersLeft = 10, maxCredits = 15;
    private boolean honorsStudent = false;

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
        myGUI.wrapper.add(myGUI.configCard, "configCard");
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
        areYouInTheRadioButton.addActionListener(new ActionListener() {
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
                String userText = usernameInput.getText();
                String passText = String.valueOf(passwordInput.getPassword());

                //add sql stuff here!

                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "configCard");
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

                if(courseCode == "") {
                    JOptionPane.showMessageDialog(frame,
                            "Please input a non-empty value for course code.",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    courseField.setText("");
                    return;
                } else if(courseCode.split(" ").length != 2) {
                    JOptionPane.showMessageDialog(frame,
                            "Please input course code with the title and the number separated by one space: E.G. 'CPSC 2810'",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    courseField.setText("");
                    return;
                } else {
                    try {
                        Integer.parseInt(courseCode.split(" ")[1]);
                    } catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Please input a valid number in the second portion of your course code.",
                                "ERROR",
                                JOptionPane.WARNING_MESSAGE);
                        courseField.setText("");
                        return;
                    }
                }

                int hours;

                if(creditHours == "") {
                    JOptionPane.showMessageDialog(frame,
                            "Please input a non-empty value for credit hours.",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    creditHourField.setText("");
                    return;
                } else {
                    try {
                        hours = Integer.parseInt(creditHours);
                    } catch (NumberFormatException m) {
                        JOptionPane.showMessageDialog(frame,
                                "Please input only a number value for the number of credit hours.",
                                "ERROR",
                                JOptionPane.WARNING_MESSAGE);
                        creditHourField.setText("");
                        return;
                    }
                }

                String sql =
                        "INSERT INTO Courses " +
                                "(Code, Description, CreditHours, GroupPrereqs, ReqPrereqs, Coreqs, Attributes) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?) ";
                Connection connection = null;

                try {
                    connection = DBUtil.getConnection();
                } catch (DBException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to connect to the DB. Please try again.",
                            "ERROR",
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
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to insert the values into the DB. Please try again.",
                            "ERROR",
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
                cl.show(wrapper, "rootCard");
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

                if(courseCode == "") {
                    JOptionPane.showMessageDialog(frame,
                            "Please input a non-empty value for course code.",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    courseField.setText("");
                    return;
                } else if(courseCode.split(" ").length != 2) {
                    JOptionPane.showMessageDialog(frame,
                            "Please input course code with the title and the number separated by one space: E.G. 'CPSC 2810'",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    courseField.setText("");
                    return;
                } else {
                    try {
                        Integer.parseInt(courseCode.split(" ")[1]);
                    } catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Please input a valid number in the second portion of your course code.",
                                "ERROR",
                                JOptionPane.WARNING_MESSAGE);
                        courseField.setText("");
                        return;
                    }
                }

                int hours;

                if(creditHours == "") {
                    JOptionPane.showMessageDialog(frame,
                            "Please input a non-empty value for credit hours.",
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                    creditHourField.setText("");
                    return;
                } else {
                    try {
                        hours = Integer.parseInt(creditHours);
                    } catch (NumberFormatException m) {
                        JOptionPane.showMessageDialog(frame,
                                "Please input only a number value for the number of credit hours.",
                                "ERROR",
                                JOptionPane.WARNING_MESSAGE);
                        creditHourField.setText("");
                        return;
                    }
                }

                String sql =
                        "INSERT INTO Courses " +
                                "(Code, Description, CreditHours, GroupPrereqs, ReqPrereqs, Coreqs, Attributes) " +
                                "VALUES " +
                                "(?, ?, ?, ?, ?, ?, ?) ";
                Connection connection = null;

                try {
                    connection = DBUtil.getConnection();
                } catch (DBException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to connect to the DB. Please try again.",
                            "ERROR",
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
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "There was an error trying to insert the values into the DB. Please try again.",
                            "ERROR",
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
    }
}
