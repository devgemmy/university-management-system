package Restaurant;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FoodMenu extends JPanel {

	private final MainApp mainApp;
	private JPanel itemsContainer;
	private Map<String, Integer> userOrders; // Track user orders per item
	private Map<String, JLabel> restockLabels;

	public FoodMenu(MainApp mainApp, List<FoodItem> foodItems) {
		this.mainApp = mainApp;
		this.userOrders = new HashMap<>(); // Initialize the order tracking map
		this.restockLabels = new HashMap<>();

		setLayout(new BorderLayout());

		// Overall layout for the frame: top bar, then content
		setLayout(new BorderLayout());

		// 1. Top bar with brand label and cart icon
		add(createTopBar(), BorderLayout.NORTH);

		// 2. Center content panel with “Choose from:” heading + scrollable items
		add(createCenterPanel(foodItems), BorderLayout.CENTER);
	}
	public void updateStockDisplay(FoodItem updatedItem) {
	    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, itemsContainer);
	    if (scrollPane == null) return;

	    int scrollPosition = scrollPane.getHorizontalScrollBar().getValue();

	    for (Component comp : itemsContainer.getComponents()) {
	        if (comp instanceof JPanel) {
	            JPanel cardPanel = (JPanel) comp;
	            JLabel stockLabel = null;
	            JTextArea expiryLabel = null;
	            JButton addButton = null;

	            for (Component c : cardPanel.getComponents()) {
	                if (c instanceof JLabel && ((JLabel) c).getText().startsWith("Stock:") && c.getName().equals(updatedItem.getName())) {
	                    stockLabel = (JLabel) c;
	                } else if (c instanceof JTextArea && ((JTextArea) c).getText().startsWith("Expires:")) {
	                    expiryLabel = (JTextArea) c;
	                } else if (c instanceof JButton) {
	                    addButton = (JButton) c;
	                }
	            }

	            if (stockLabel != null && expiryLabel != null && addButton != null) {
	                stockLabel.setText("Stock: " + updatedItem.getDisplayQuantity());
	                expiryLabel.setText("Expires: " + updatedItem.getExpirationDate());

	                int currentOrders = userOrders.getOrDefault(updatedItem.getName(), 0);

	                if (updatedItem.getDisplayQuantity() == 0) {
	                    addButton.setEnabled(false);
	                    addButton.setText("Out of Stock");
	                    startRestockingTimers(updatedItem, stockLabel, addButton);
	                } else if (currentOrders >= 10) {
	                    addButton.setEnabled(false);
	                    addButton.setText("Max Limit Reached");
	                } else {
	                    addButton.setEnabled(true);
	                    addButton.setText("Add to Basket");
	                }
	            }
	        }
	    }

	    itemsContainer.revalidate();
	    itemsContainer.repaint();

	    SwingUtilities.invokeLater(() -> {
	        scrollPane.getHorizontalScrollBar().setValue(scrollPosition);
	    });

	    fileHandler.saveStockData(mainApp.foodItems);
	}


	/**
	 * Creates the top bar with a label on the left and a “cart” label/icon on the right.
	 */
	private JPanel createTopBar() {
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(new Color(207, 109, 40)); // orange

		JLabel brandLabel = new JLabel("UNI FOOD");
		brandLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		brandLabel.setForeground(Color.WHITE);
		brandLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		JButton cartButton = new JButton("Cart"); 
		cartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		cartButton.setForeground(Color.WHITE);
		cartButton.setBackground(new Color(207, 109, 40)); // orange
		cartButton.setFocusPainted(false); // Remove focus outline for better UI
		cartButton.setOpaque(true);
		cartButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		cartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Handle adding to basket (implement as needed)
				mainApp.showCartPage();
			}
		});

		topBar.add(brandLabel, BorderLayout.WEST);
		topBar.add(cartButton, BorderLayout.EAST);

		return topBar;
	}

	/**
	 * Creates the center panel with “Choose from:” heading and a scrollable row of item panels.
	 */
	private JPanel createCenterPanel(List<FoodItem> foodItems) {
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(new Color(245, 234, 220));

		JLabel headingLabel = new JLabel("Choose from:");
		headingLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		centerPanel.add(headingLabel, BorderLayout.NORTH);

		itemsContainer = new JPanel();
		itemsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		itemsContainer.setBackground(new Color(245, 234, 220));

		for (FoodItem item : foodItems) {
			itemsContainer.add(createFoodItemPanel(item));
		}

		JScrollPane scrollPane = new JScrollPane(itemsContainer,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(null);
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		return centerPanel;
	}


	/**
	 * Creates an individual “card” for a food item, showing an image, labels, and a button.
	 */
	private JPanel createFoodItemPanel(FoodItem item) {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
		cardPanel.setPreferredSize(new Dimension(180, 270));
		cardPanel.setBackground(Color.WHITE);
		
		// Partial and Full Restock Labels (Centered)
		JLabel partialRestockLabel = new JLabel("Partial restock in: --", SwingConstants.CENTER);
		JLabel fullRestockLabel = new JLabel("Full restock in: --", SwingConstants.CENTER);
		partialRestockLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
		fullRestockLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
		restockLabels.put(item.getName() + "_partial", partialRestockLabel);
		restockLabels.put(item.getName() + "_full", fullRestockLabel);

		// Item Name (Left-aligned)
		// Item Name (Multi-line, Left-aligned)
		JTextArea nameLabel = new JTextArea(item.getName());
		nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		nameLabel.setWrapStyleWord(true); // Enable word wrap
		nameLabel.setLineWrap(true); // Allow text to wrap
		nameLabel.setEditable(false); // Prevent user editing
		nameLabel.setOpaque(false); // Make background transparent
		nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align text to the left
		nameLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0)); // Small padding
		nameLabel.setPreferredSize(new Dimension(160, 30)); // Set preferred size for two rows



		JLabel stockLabel = new JLabel("Stock: " + item.getQuantity(), SwingConstants.CENTER);
		stockLabel.setName(item.getName());
		stockLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

		// Expiry Date (Multi-line Support)
		JTextArea expiryLabel = new JTextArea("Expires: " + item.getExpirationDate());
		expiryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		expiryLabel.setWrapStyleWord(true);
		expiryLabel.setLineWrap(true);
		expiryLabel.setEditable(false);
		expiryLabel.setOpaque(false); // Transparent background
		expiryLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
		expiryLabel.setPreferredSize(new Dimension(160, 20)); // Ensure space for two lines
		expiryLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0)); // Small padding


		JLabel priceLabel = new JLabel("£" + String.format("%.2f", item.getPrice()), SwingConstants.CENTER);
		priceLabel.setForeground(new Color(0, 128, 0));

		JButton addButton = new JButton("Add to Basket");
		addButton.setFont(new Font("SansSerif", Font.BOLD, 10));
		addButton.setForeground(Color.white);
		addButton.setBackground(new Color(23, 62, 88));
		addButton.setOpaque(true);
		addButton.setFocusPainted(false);
		addButton.setPreferredSize(new Dimension(140, 30)); // Fixed button width
		addButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // Increased padding


		addButton.addActionListener(e -> {
		    int currentOrders = userOrders.getOrDefault(item.getName(), 0);
		    if (currentOrders < 10 && item.getDisplayQuantity() > 0) {
		        userOrders.put(item.getName(), currentOrders + 1);
		        
		        mainApp.cartPanel.addItem(item);
		        item.decreaseTempQuantity(1);
		        stockLabel.setText("Stock: " + item.getDisplayQuantity());

		        if (userOrders.get(item.getName()) == 10 && item.getDisplayQuantity() == 0) {
		            addButton.setEnabled(false);
		            addButton.setText("Out of Stock");
		            startRestockingTimers(item, stockLabel, addButton);
		        } else if (userOrders.get(item.getName()) == 10) {
		            addButton.setEnabled(false);
		            addButton.setText("Max Limit Reached");
		        } 
		        else if (item.getDisplayQuantity() == 0) {
		            addButton.setEnabled(false);
		            addButton.setText("Out of Stock");
		            startRestockingTimers(item, stockLabel, addButton);
		        }

		    } else {
		        JOptionPane.showMessageDialog(null,
		                "Cannot add more! Either stock is empty or you've reached the order limit.",
		                "Order Not Allowed", JOptionPane.WARNING_MESSAGE);
		    }
		});


		addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Add components in vertical order

		cardPanel.add(Box.createVerticalStrut(3));
		cardPanel.add(nameLabel);
		cardPanel.add(Box.createVerticalStrut(3));
		cardPanel.add(stockLabel);
		cardPanel.add(Box.createVerticalStrut(3));
		cardPanel.add(expiryLabel);
		cardPanel.add(Box.createVerticalStrut(3));
		cardPanel.add(priceLabel);
		cardPanel.add(Box.createVerticalStrut(5));
		cardPanel.add(addButton);
		cardPanel.add(Box.createVerticalStrut(5));
		cardPanel.add(partialRestockLabel);
		cardPanel.add(fullRestockLabel);

		return cardPanel;
	}

	public void updateFoodItems(List<FoodItem> foodItems)
	{
		itemsContainer.removeAll();
		// Dynamically create panels for each item
		for (FoodItem item : foodItems) {
			itemsContainer.add(createFoodItemPanel(item));
		}
	}

	public void resetUserOrders(String itemName) {
		if (userOrders.containsKey(itemName)) {
			userOrders.put(itemName, 0); // Reset the order count
			System.out.println("Reset order count for: " + itemName);
		}
	}

	private void startRestockingTimers(FoodItem item, JLabel stockLabel, JButton addButton) {
	    item.cancelTimers();

	    Random random = new Random();
	    int partialRestockSeconds = (random.nextInt(5) + 1) * 60; // 1-5 mins
	    int fullRestockSeconds = (random.nextInt(11) + 5) * 60; // 5-15 mins

	    JLabel partialLabel = restockLabels.get(item.getName() + "_partial");
	    JLabel fullLabel = restockLabels.get(item.getName() + "_full");

	    // Partial restock timer
	    item.partialRestockTimer = new Timer();
	    item.partialRestockTimer.scheduleAtFixedRate(new TimerTask() {
	        int remaining = partialRestockSeconds;

	        @Override
	        public void run() {
	            SwingUtilities.invokeLater(() -> {
	                if (partialLabel != null)
	                    partialLabel.setText("Partial restock in: " + formatTime(remaining));
	            });

	            remaining -= 10;

	            if (remaining <= 0) {
	                item.setQuantity(10); // clearly set to 10
	                item.resetExpiryDateAfterRestock();

	                SwingUtilities.invokeLater(() -> {
	                    item.restoreTempQuantity(); // Ensure UI quantity sync
	                    stockLabel.setText("Stock: " + item.getDisplayQuantity());
	                    addButton.setEnabled(true);
	                    addButton.setText("Add to Basket");

	                    // Disable partial timer numbers temporarily (keep text)
	                    if (partialLabel != null)
	                        partialLabel.setText("Partial restock in: --");
	                });

	                item.partialRestockTimer.cancel();
	            }
	        }
	    }, 0, 10000); // every 10 seconds update

	    // Full restock timer
	    item.fullRestockTimer = new Timer();
	    item.fullRestockTimer.scheduleAtFixedRate(new TimerTask() {
	        int remaining = fullRestockSeconds;

	        @Override
	        public void run() {
	            SwingUtilities.invokeLater(() -> {
	                if (fullLabel != null)
	                    fullLabel.setText("Full restock in: " + formatTime(remaining));
	            });

	            remaining -= 10;

	            if (remaining <= 0) {
	                item.setQuantity(100);
	                item.resetExpiryDateAfterRestock();

	                SwingUtilities.invokeLater(() -> {
	                    item.restoreTempQuantity(); // sync UI quantity
	                    stockLabel.setText("Stock: " + item.getDisplayQuantity());
	                    addButton.setEnabled(true);
	                    addButton.setText("Add to Basket");

	                    if (fullLabel != null)
	                        fullLabel.setText("Full restock in: --");
	                });

	                item.fullRestockTimer.cancel();
	            }
	        }
	    }, 0, 10000); // every 10 seconds update
	}

	// Helper method clearly formats time as "mm:ss"
	private String formatTime(int seconds) {
	    int minutes = seconds / 60;
	    int secs = seconds % 60;
	    return String.format("%02d:%02d", minutes, secs);
	}


}