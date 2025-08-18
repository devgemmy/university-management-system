package Restaurant;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderNumber;
    private Date orderDate;
    private List<FoodItem> orderedItems;

    public OrderRecord(int orderNumber, Date orderDate, List<FoodItem> orderedItems) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderedItems = orderedItems;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public List<FoodItem> getOrderedItems() {
        return orderedItems;
    }

    // New method to compute the total cost of the order.
    public double getTotalCost() {
        double total = 0.0;
        for (FoodItem item : orderedItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}