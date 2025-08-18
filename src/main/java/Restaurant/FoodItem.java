package Restaurant;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class FoodItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private double price;
	private int quantity;        // Permanent (persisted)
	private int tempQuantity;  // Temporary (NOT persisted)

	public transient Timer partialRestockTimer;
	public transient Timer fullRestockTimer;
	private String expirationDate;

	private int orderQuantity = 0; //  Add this near the top with other fields

	public int getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}


	public FoodItem(String name, String description, double price, int quantity, String expirationDate) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.quantity = Math.min(quantity, 100);
		this.tempQuantity = quantity;  // Initially match the persistent quantity
		this.expirationDate = expirationDate;
	}



	// New method clearly to restore tempQuantity on app load
	public void restoreTempQuantity() {
		this.tempQuantity = quantity;
	}

	public int getDisplayQuantity() {
		return tempQuantity;  // use temp for UI
	}

	public void decreaseTempQuantity(int amount) {
		if (tempQuantity >= amount) {
			tempQuantity -= amount;
		}
	}

	public void increaseTempQuantity(int amount) {
		this.tempQuantity = Math.min(tempQuantity + amount, 100);
	}

	// Call clearly ONLY upon payment
	public void commitPurchase(int amount) {
		if (quantity >= amount) {
			quantity -= amount;
		}
		tempQuantity = quantity;  //  resync after payment
	}
	public void cancelTimers() {
		if (partialRestockTimer != null) partialRestockTimer.cancel();
		if (fullRestockTimer != null) fullRestockTimer.cancel();
	}



	// Getters and Setters
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getExpirationDate()
	{
		return expirationDate;
	}


	public void setQuantity(int quantity) {
		if (quantity > 100) {
			quantity = 100; //  cap at 100
		}
		this.quantity = quantity;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return name + " - " + description + " - £" + price + " - " + quantity + " in stock";
	}



	public void resetFoodItem() {
		Random random = new Random();

		// Reset stock to a random number between 1-100
		this.quantity = random.nextInt(100) + 1;

		// Generate new expiry date within next 3 days
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(3) + 1);
		calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
		calendar.set(Calendar.MINUTE, random.nextInt(60));
		calendar.set(Calendar.SECOND, random.nextInt(60));

		this.expirationDate = sdf.format(calendar.getTime());

		System.out.println("Food Reset (expired): " + name + " | New Stock: " + quantity + " | New Expiry: " + expirationDate);
	}



	public void resetExpiryDateAfterRestock() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		Random random = new Random();

		try {
			Date currentExpiry = sdf.parse(this.expirationDate);
			Date newExpiry;

			do {
				calendar.setTime(new Date()); // Start from now
				calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(3) + 1); // 1-3 days in future
				calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
				calendar.set(Calendar.MINUTE, random.nextInt(60));
				calendar.set(Calendar.SECOND, random.nextInt(60));

				newExpiry = calendar.getTime();
			} while (newExpiry.before(currentExpiry)); // Ensure it's not earlier than current expiry

			this.expirationDate = sdf.format(newExpiry);

			System.out.println("Expiry Date Reset After Restock: " + name + " | New Expiry: " + expirationDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	private String generateRandomExpirationDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		Random random = new Random();
		calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(3) + 1);
		return dateFormat.format(calendar.getTime());
	}

	public void resetExpiryDateOnly() {
		Calendar calendar = Calendar.getInstance();
		Random random = new Random();

		calendar.setTime(new Date());  // NOW
		calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(3) + 1); // +1 to +3 days clearly random
		calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
		calendar.set(Calendar.MINUTE, random.nextInt(60));
		calendar.set(Calendar.SECOND, random.nextInt(60));

		this.expirationDate = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss").format(calendar.getTime());
		System.out.println("Expiry Reset Only " + name + " | New Expiry: " + expirationDate);
	}


}