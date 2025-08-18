package Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpirationChecker extends Thread {
    private List<FoodItem> foodItems;
    private FoodMenu foodMenu;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");

    public ExpirationChecker(List<FoodItem> foodItems, FoodMenu foodMenu) {
        this.foodItems = foodItems;
        this.foodMenu = foodMenu;
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkForExpiredFoods();
                Thread.sleep(10000); //  Check every 10 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForExpiredFoods() {
        Date now = new Date();

        for (FoodItem item : foodItems) {
            try {
                Date expiryDate = dateFormat.parse(item.getExpirationDate());
                if (expiryDate.before(now)) {
                    item.resetFoodItem();
                    foodMenu.updateStockDisplay(item);

                    fileHandler.saveStockData(foodItems); //  Save immediately after expiry reset
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }



} 