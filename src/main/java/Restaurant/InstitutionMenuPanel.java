package Restaurant;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

public class InstitutionMenuPanel extends JPanel {
    private MainApp mainApp;
    private List<Institution> institutions;
    
    public InstitutionMenuPanel(MainApp mainApp, List<Institution> institutions) {
        this.mainApp = mainApp;
        this.institutions = institutions;
        setLayout(new BorderLayout());
        
        // Top bar with a title
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(207, 109, 40)); // orange color
        
        JLabel titleLabel = new JLabel("SELECT YOUR UNIVERSITY");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);
        
        add(topBar, BorderLayout.NORTH);
        
        // Panel that holds institution buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        //
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // 🧠 Force sizing with this layout wrapper
        buttonPanel.setPreferredSize(new Dimension(600, institutions.size() * 50));
        buttonPanel.setMaximumSize(new Dimension(600, institutions.size() * 50));
        buttonPanel.setBackground(Color.WHITE);
        //
        // add(buttonPanel, BorderLayout.SOUTH);
        
        // Create a button for each institution
        for (Institution inst : institutions) {
            System.out.println("Institution name: " + inst.getName());
            JButton instButton = new JButton(inst.getName());
            instButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            instButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            instButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            instButton.setForeground(Color.WHITE);
            instButton.setBackground(new Color(0, 122, 123)); // green
            instButton.setOpaque(true);
            instButton.setFocusPainted(false); // Remove focus outline for better UI
            instButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
            instButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // When an institution is picked, show its food menu
                    mainApp.showFoodMenuForInstitution(inst);
                }
            });
            buttonPanel.add(instButton);
            buttonPanel.add(Box.createVerticalStrut(10));
        }
        
        // Place the buttons inside a scroll pane
        buttonPanel.setPreferredSize(new Dimension(400, institutions.size() * 55));
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        //new
        revalidate();
        repaint();
    }
} 