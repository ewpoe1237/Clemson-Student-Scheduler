package ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
    private JSlider suggestedSemester;
    private JTextField attributeField;

    private int semestersLeft = 10, maxCredits = 15;
    private boolean honorsStudent = false;

    static ScheduleGUI myGUI = new ScheduleGUI();

    public static void main(String[] args) {
        JFrame frame = new JFrame("ScheduleGUI");
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
                CardLayout cl = (CardLayout) wrapper.getLayout();
                cl.show(wrapper, "rootCard");
            }
        });
        confirmAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseCode = courseField.getText();
                String attributes = attributeField.getText();
                String coreqs = coreqField.getText();
                String requiredPrereqs = reqPreField.getText();
                String groupPrereqs = groupPreField.getText();

                courseField.setText("");
                attributeField.setText("");
                coreqField.setText("");
                reqPreField.setText("");
                groupPreField.setText("");
            }
        });
    }
}
