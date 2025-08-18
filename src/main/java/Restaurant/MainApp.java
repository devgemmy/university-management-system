package Restaurant;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;

public class MainApp extends JFrame {

	private final CardLayout cardLayout;
	private final JPanel mainPanel;      
	public FoodMenu foodMenuPanel;
	private final InstitutionMenuPanel institutionMenuPanel;
	public final CartPanel cartPanel;
	private JPanel currentPanel;

	public List<Institution> institutions;
	public List<FoodItem> foodItems;

	private String currentStudentId;
	private final java.util.HashMap<Integer, Double> orderTotalsMap = new java.util.HashMap<>();

	public void setCurrentStudentId(String id) {
		this.currentStudentId = id;
	}

	public String getCurrentStudentId() {
		return currentStudentId;
	}

	public java.util.HashMap<Integer, Double> getOrderTotalsMap() {
		return orderTotalsMap;
	}

	public MainApp() {
		super("UNI FOOD");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);
		setLocationRelativeTo(null);

		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		// load the GLOBAL FOOD ITEMS
		foodItems = fileHandler.loadStockData();
		for (FoodItem item : foodItems) {
			item.restoreTempQuantity();
		}


		// Load institutions and correctly assign global foodItems
		institutions = fileHandler.loadInstitutions(foodItems); // pass global foodItems here
		System.out.println("Loaded institutions: " + institutions.size());

		cartPanel = new CartPanel(this);
		institutionMenuPanel = new InstitutionMenuPanel(this, institutions);

		mainPanel.add(institutionMenuPanel, "INSTITUTION_PAGE");
		mainPanel.add(cartPanel, "CART_PAGE");


		cardLayout.show(mainPanel, "INSTITUTION_PAGE");

		add(mainPanel);


		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveStockBeforeExit();
			}
		});
	}


	// Update stock when cart changes
	 public void updateFoodMenuStock(FoodItem item) {
	        if (foodMenuPanel != null) {
	            foodMenuPanel.updateStockDisplay(item);
	        }
	        fileHandler.saveStockData(foodItems);
	        fileHandler.saveToFile("SaveDBB.ser", institutions);
	    }

	 
	// Show food menu and start expiration checker
	public void showFoodMenuForInstitution(Institution institution) {
		foodMenuPanel = new FoodMenu(this, institution.getFoodItems());
		mainPanel.add(foodMenuPanel, "FOOD_MENU");
		cardLayout.show(mainPanel, "FOOD_MENU");

		// Start expiration checker thread (if not running)
		ExpirationChecker expirationChecker = new ExpirationChecker(institution.getFoodItems(), foodMenuPanel);
		expirationChecker.start();
	}
	
	

	// Save stock when closing the app
	 public void saveStockBeforeExit() {
	        fileHandler.saveStockData(foodItems);
	    }
	 

	public void showCartPage() {
		cardLayout.show(mainPanel, "CART_PAGE");
		cartPanel.refreshCart();
	}

	public void showFoodMenuPage() {
		cardLayout.show(mainPanel, "FOOD_MENU");
	}

	public void showOrderConfirmationPage(int orderNumber) {
		OrderConfirmationPage orderConfirmationPage = new OrderConfirmationPage(this, orderNumber);
		mainPanel.add(orderConfirmationPage, "ORDER_CONFIRMATION");
		cardLayout.show(mainPanel, "ORDER_CONFIRMATION");
	}
	public void showOrderHistoryPage() {
	    OrderHistoryPage historyPage = new OrderHistoryPage(this);
	    mainPanel.add(historyPage, "ORDER_HISTORY");
	    cardLayout.show(mainPanel, "ORDER_HISTORY");
	}

	
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainApp app = new MainApp();

			// Save stock when the app is closed
			app.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					app.saveStockBeforeExit();
				}
			});

			app.setVisible(true);
		});
	}

}