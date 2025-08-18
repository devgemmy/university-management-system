package Restaurant;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderHistoryPage extends JPanel {

    public OrderHistoryPage(MainApp mainApp) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Order History", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Load orders from history using your fileHandler
        List<OrderRecord> history = fileHandler.loadOrderHistory();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (history.isEmpty()) {
            historyArea.setText("No orders yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (OrderRecord record : history) {
                sb.append("Order #").append(record.getOrderNumber())
                        .append(" (").append(sdf.format(record.getOrderDate())).append(")\n");

                // List each item in the order
                for (FoodItem item : record.getOrderedItems()) {
                    sb.append("   • ").append(item.getName())
                            .append(" x").append(item.getQuantity()).append("\n");
                }
                // Append the total cost for this order
                sb.append("Total: £")
                        .append(String.format("%.2f", record.getTotalCost()))
                        .append("\n\n");
            }
            historyArea.setText(sb.toString());
        }

        JScrollPane scrollPane = new JScrollPane(historyArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(207, 109, 40)); // orange
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> mainApp.showFoodMenuPage());
        add(backButton, BorderLayout.SOUTH);
    }
}