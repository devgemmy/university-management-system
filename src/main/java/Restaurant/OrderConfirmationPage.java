package Restaurant;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderConfirmationPage extends JPanel {

    private final MainApp mainApp;

    public OrderConfirmationPage(MainApp mainApp, int orderNumber) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 234, 220)); // Light background color, consistent with the app

        // Header: Uni Food branding
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(235, 120, 52)); // Matching the top bar color in FoodMenu

        JLabel brandLabel = new JLabel("UNI FOOD");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        brandLabel.setForeground(Color.WHITE);
        headerPanel.add(brandLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center: Order information
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(new Color(255,255,255)); // Matching menu background
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding

        // "Order No" Label
        JLabel orderTextLabel = new JLabel("ORDER NO:");
        orderTextLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        orderTextLabel.setForeground(Color.BLACK); // Make text black
        orderTextLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Order Number Label
        JLabel orderNumberLabel = new JLabel(String.valueOf(orderNumber));
        orderNumberLabel.setFont(new Font("SansSerif", Font.BOLD, 90)); // Larger font size
        orderNumberLabel.setForeground(Color.BLACK); // Black text
        orderNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel instructionLabel = new JLabel("please pay at the counter to complete the order.");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        centerPanel.add(orderTextLabel);
        centerPanel.add(orderNumberLabel);
        centerPanel.add(instructionLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Footer: Go to Menu button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(255,255,255)); // Matching background color

        JButton goToMenuButton = new JButton("Go to Menu");
        goToMenuButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        goToMenuButton.setForeground(Color.BLACK);
        goToMenuButton.setBackground(new Color(0, 122, 123)); // green
        goToMenuButton.setFocusPainted(false); // Remove focus outline for better UI
        goToMenuButton.setOpaque(true);
        goToMenuButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        goToMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showFoodMenuPage(); // Navigate back to the menu
            }
        });

        footerPanel.add(goToMenuButton);
        add(footerPanel, BorderLayout.SOUTH);
    }
}