package Restaurant;

import javax.swing.*;
import java.awt.*;

public class StudentLoginPanel extends JPanel {
    private MainApp mainApp;
    private JTextField studentIdField;

    public StudentLoginPanel(MainApp mainApp) {
        this.mainApp = mainApp;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Welcome to UNI FOOD");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Please enter your Student ID to continue:");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 16));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        instruction.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        studentIdField = new JTextField();
        studentIdField.setMaximumSize(new Dimension(300, 35));
        studentIdField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        studentIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton continueButton = new JButton("Continue");
      
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        continueButton.setForeground(Color.white);
        continueButton.setBackground(new Color(23, 62, 88));
        continueButton.setFocusPainted(false);
        continueButton.setPreferredSize(new Dimension(140, 30)); // Fixed button width
        continueButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // Increased padding

        continueButton.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            if (!studentId.isEmpty()) {
                // mainApp.setCurrentStudentId(studentId);
                // mainApp.showInstitutionPage();
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid Student ID.");
            }
        });

        add(Box.createVerticalStrut(100));
        add(title);
        add(instruction);
        add(studentIdField);
        add(Box.createVerticalStrut(20));
        add(continueButton);
    }
}
