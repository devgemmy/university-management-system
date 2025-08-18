package Restaurant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Institution implements Serializable {
	private static final long serialVersionUID = 1L;   

    private String name;
    private ArrayList<FoodItem> foodItems;

    public Institution(String name) {
        this.name = name;
        this.foodItems = new ArrayList<>(12);
    }

    public String getName() {
        return name;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void addFoodItem(FoodItem item) {
        foodItems.add(item);
    }
    
    public void clearFoodItems() {
        this.foodItems.clear(); 
    }
}