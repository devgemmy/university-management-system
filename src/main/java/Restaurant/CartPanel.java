package Restaurant;

import com.example.dashboard.PopulateOrderHistory;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.sql.SQLException;

class CartPanel extends JPanel {

	private int generateOrderNumber() {
		return (int) (Math.random() * 9000) + 1000; // Generate a number between 1000 and 9999
	}

	private MainApp mainApp;
	public List<CartItem> cartItems = new ArrayList<>();

	// UI components we might want to update
	private final JPanel itemsPanel;
	private final JLabel totalLabel;
	private JLabel infoLabel;
	// This map holds orderNumber -> totalCost
	Map<Integer, Double> orderTotals = new HashMap<>();

	//
	private List<FoodItem> cloneCartItems() {
	    List<FoodItem> clones = new ArrayList<>();
	    for (CartItem cartItem : cartItems) {
	        // Create a lightweight copy (without timers)
	        clones.add(new FoodItem(
	            cartItem.item.getName(),
	            cartItem.item.getDescription(),
	            cartItem.item.getPrice(),
	            cartItem.quantity,
	            cartItem.item.getExpirationDate()
	        ));
	    }
	    return clones;
	}

	private void updateInfoLabel() {
		int totalItems = cartItems.stream().mapToInt(ci -> ci.quantity).sum();
		infoLabel.setText("You have " + totalItems + " item(s) in your basket");
	}


	public CartPanel(MainApp mainApp) {
		this.mainApp = mainApp;
		setLayout(new BorderLayout());

		// 1. Top bar
		add(createTopBar(), BorderLayout.NORTH);

		// 2. Main content
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(contentPanel, BorderLayout.CENTER);

		// Left side: “Shopping cart” and items
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		// Heading “Shopping cart”
		JLabel cartHeading = new JLabel("Shopping cart");
		cartHeading.setFont(new Font("SansSerif", Font.BOLD, 18));
		leftPanel.add(cartHeading);

		// Info line: “You have X items in your basket”
		//JLabel infoLabel = new JLabel("You have " + cartItems.size() + " items in your basket");
		infoLabel = new JLabel();
		infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		leftPanel.add(infoLabel);

		leftPanel.add(Box.createVerticalStrut(15));

		// The panel that holds individual item rows
		itemsPanel = new JPanel();
		itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
		refreshItemsList();

		// Put itemsPanel in a scroll pane if you have many items
		JScrollPane scrollPane = new JScrollPane(itemsPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);

		leftPanel.add(scrollPane);

		contentPanel.add(leftPanel, BorderLayout.CENTER);

		// Right side: total and “Pay at Counter”
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

		// Spacing at top
		rightPanel.add(Box.createVerticalStrut(50));

		// Total label
		totalLabel = new JLabel();
		totalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightPanel.add(totalLabel);

		// Spacing
		rightPanel.add(Box.createVerticalStrut(20));


		// “Pay at Counter” button

		JButton payButton = new JButton("Pay at Counter");
		payButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		payButton.setBackground(new Color(0, 122, 123));
		payButton.setForeground(Color.WHITE);
		payButton.setFocusPainted(false);
		payButton.setOpaque(true);
		payButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
		payButton.setAlignmentX(Component.LEFT_ALIGNMENT);

		payButton.addActionListener(e -> {
			if (cartItems.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Cart is empty!", "Payment Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Variables for order creation
			int orderNumber = generateOrderNumber();
			Date orderDate = new Date();

			List<FoodItem> orderedItems = cloneCartItems();

			// Compute order total. Adjust quantity getter if needed.
			double orderTotal = 0.0;
			for (FoodItem item : orderedItems) {
				orderTotal += item.getPrice() * item.getQuantity();
			}

			// Retrieve student ID from MainApp
			String studentID = mainApp.getCurrentStudentId();

			// Debug: Print out order details
			System.out.println("==== PAY BUTTON CLICKED ====");
			System.out.println("Student ID: " + studentID);
			System.out.println("Order Number: " + orderNumber);
			System.out.println("Order Total: £" + String.format("%.2f", orderTotal));
			for (FoodItem item : orderedItems) {
				System.out.println("  ORDERED: " + item.getName() + " x" + item.getQuantity()
						+ " @ £" + item.getPrice());
			}

			// Create and store the order record
			OrderRecord orderRecord = new OrderRecord(orderNumber, orderDate, orderedItems);
			List<OrderRecord> history = fileHandler.loadOrderHistory();
			history.add(orderRecord);
			fileHandler.saveOrderHistory(history);

			// Store the order total in the HashMap (orderNumber -> orderTotal)
			mainApp.getOrderTotalsMap().put(orderNumber, orderTotal);


			// Pass the orderTotalsMap to the Finance module
			try {
				PopulateOrderHistory.setOrderHistory(mainApp.getOrderTotalsMap());
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}

			// Update stock permanently
			for (CartPanel.CartItem cartItem : cartItems) {
				cartItem.item.commitPurchase(cartItem.quantity);
			}

			mainApp.saveStockBeforeExit();
			fileHandler.saveToFile("SaveDBB.ser", mainApp.institutions);

			// Clear UI
			cartItems.clear();
			updateTotal();
			refreshItemsList();

			JOptionPane.showMessageDialog(null, "Order completed! Stock updated.", "Payment Success", JOptionPane.INFORMATION_MESSAGE);

			// Navigate to order confirmation page
			mainApp.showOrderConfirmationPage(orderNumber);
		});

		rightPanel.add(payButton);

		// Spacing after "Pay at Counter"
		rightPanel.add(Box.createVerticalStrut(15));

		// “Order History” button (smaller & styled)
		JButton historyButton = new JButton("Order History");
		historyButton.setFont(new Font("SansSerif", Font.BOLD, 15)); // Smaller font
		historyButton.setPreferredSize(new Dimension(140, 30));
		historyButton.setBackground(new Color(207, 109, 40)); // Orange theme
		historyButton.setForeground(Color.WHITE);
		historyButton.setFocusPainted(false);
		historyButton.setOpaque(true);
		historyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		historyButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

		historyButton.addActionListener(e -> mainApp.showOrderHistoryPage());
		rightPanel.add(historyButton);
		contentPanel.add(rightPanel, BorderLayout.EAST);
		updateTotal();
	}

	private JPanel createTopBar() {
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBackground(new Color(235, 120, 52)); // Example orange

		// “Back” or “Shopping Continue” label
		JButton backButton = new JButton("< Shopping Continue");
		backButton.setForeground(Color.WHITE);
		backButton.setContentAreaFilled(false);
		backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainApp.showFoodMenuPage();
			}
		});

		// Cart icon or label (top-right)
		JLabel cartLabel = new JLabel("Cart");
		cartLabel.setForeground(Color.WHITE);
		cartLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		topBar.add(backButton, BorderLayout.WEST);
		topBar.add(cartLabel, BorderLayout.EAST);

		return topBar;
	}

	/**
	 * Create a row panel for each cart item.
	 */
	private void refreshItemsList() {
		itemsPanel.removeAll();

		for (CartItem item : cartItems) {
			itemsPanel.add(createItemRow(item));
			itemsPanel.add(Box.createVerticalStrut(10));
		}

		itemsPanel.revalidate();
		itemsPanel.repaint();
	}

	/**
	 * Create a single row showing  name, quantity, and price.
	 */
	private JPanel createItemRow(CartItem cartItem) {
	    JPanel rowPanel = new JPanel(new BorderLayout());
	    rowPanel.setPreferredSize(new Dimension(500, 60));
	    rowPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	    // Left: Item name
	    JPanel left = new JPanel();
	    left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
	    JLabel nameLabel = new JLabel("  " + cartItem.item.getName());
	    nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
	    left.add(nameLabel);

	    // Middle: Quantity controls
	    JPanel center = new JPanel();
	    center.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

	    // Minus button
	    JButton minusButton = new JButton("-");
	    minusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
	    minusButton.setForeground(Color.BLACK);
	    minusButton.setBackground(new Color(0, 122, 123));
	    minusButton.setFocusPainted(false);
		minusButton.setOpaque(true);
	    minusButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

	    minusButton.addActionListener(e -> {
	        removeItem(cartItem.item);
	        mainApp.updateFoodMenuStock(cartItem.item);  // Notify menu
	        refreshItemsList();

	    });

	 // Quantity label (updates dynamically)
	    JLabel quantityLabel = new JLabel(String.valueOf(cartItem.quantity));
	    quantityLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

	    // Plus button
	    JButton plusButton = new JButton("+");
	    plusButton.setFont(new Font("SansSerif", Font.BOLD, 14));
	    plusButton.setForeground(Color.BLACK);
	    plusButton.setBackground(new Color(0, 122, 123));
	    plusButton.setFocusPainted(false);
		plusButton.setOpaque(true);
	    plusButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

	    plusButton.addActionListener(e -> {
	        if (cartItem.quantity < 10 && cartItem.item.getQuantity() > 0) {
	            addItem(cartItem.item);
	            mainApp.updateFoodMenuStock(cartItem.item);  // Notify menu
	            refreshItemsList();
	        }
	    });



	    //  Disable + button only if max order or stock = 0
	    if (cartItem.quantity >= 10 || cartItem.item.getQuantity() == 0) {
	        plusButton.setEnabled(false);
	    }

	    //  Disable - button if quantity is 1 (prevent negative)
	    if (cartItem.quantity <= 0) {
	        minusButton.setEnabled(false);
	    }

	    // Add buttons and label to center panel
	    center.add(minusButton);
	    center.add(quantityLabel);
	    center.add(plusButton);

		// Right: price
		JLabel priceLabel = new JLabel("£" + String.format("%.2f", cartItem.item.getPrice() * cartItem.quantity));
		priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

		rowPanel.add(left, BorderLayout.WEST);
		rowPanel.add(center, BorderLayout.CENTER);
		rowPanel.add(priceLabel, BorderLayout.EAST);

		return rowPanel;
	}


	// Recalculate and display the total cost.

	private void updateTotal() {
		double total = 0;
		for (CartItem item : cartItems) {
			total += item.item.getPrice() * item.quantity;
		}
		totalLabel.setText("Total (Tax incl.): £" + String.format("%.2f", total));
	}


	public void refreshCart() {
		// For demonstration, we’ll just recalc total in case it changed
		updateTotal();
		updateInfoLabel();
	}

	public void addItem(FoodItem item) {
	    boolean itemExists = false;

	    for (CartItem cartItem : cartItems) {
	        if (cartItem.item.getName().equals(item.getName())) {
	            if (cartItem.quantity < 10 && cartItem.quantity < item.getQuantity()) {
	                cartItem.quantity += 1;
	                System.out.println("Added to cart: " + item.getName());
	            }
	            itemExists = true;
	            break;
	        }
	    }

	    if (!itemExists && item.getQuantity() > 0) {
	        cartItems.add(new CartItem(item, "", 1));
	        System.out.println("New item added to cart: " + item.getName());
	    }

	    updateTotal();
	    refreshItemsList();
	}



	public void removeItem(FoodItem item) {
	    for (int i = 0; i < cartItems.size(); i++) {
	        CartItem cartItem = cartItems.get(i);

	        if (cartItem.item.getName().equals(item.getName())) {
	            cartItem.quantity--;

	            item.setQuantity(item.getQuantity() + 1);  // Restore stock in menu
	            mainApp.updateFoodMenuStock(item);  //  Ensure the menu updates

	            if (cartItem.quantity <= 0) {
	                cartItems.remove(i);

	                if (mainApp.foodMenuPanel != null) {
	                    mainApp.foodMenuPanel.resetUserOrders(item.getName());
	                } else {
	                    System.out.println("Warning: foodMenuPanel is NULL, cannot reset orders.");
	                }

	            }

	            System.out.println("Cart Removed: " + item.getName() + ", New Stock: " + item.getQuantity());
	            break;
	        }
	    }

	    updateTotal();
		updateInfoLabel();
	    refreshItemsList();
	}



	/**
	 * Simple model for a cart item.
	 */
	class CartItem {
		FoodItem item;
		int quantity;

		String imagePath;


		public CartItem(FoodItem item, String imagePath, int quantity) {
			this.item = item;
			this.imagePath = imagePath;
			this.quantity = quantity;
		}
	}
}