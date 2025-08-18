package Restaurant;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class fileHandler {

    private static final String STOCK_FILE = "Restaurant_data/stock_dataA.ser";
    private static final String INSTITUTION_FILE = "Restaurant_data/SaveDBB.ser";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");

    // Save stock data before closing the application
    public static void saveStockData(List<FoodItem> foodItems) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STOCK_FILE))) {
            oos.writeObject(foodItems);
            System.out.println("Stock saved successfully.");
        } catch (IOException e) {
            System.err.println("saving stock data!");
            e.printStackTrace();
        }
    }

    // Load stock from file (if it exists) or generate new stock
    public static List<FoodItem> loadStockData() {
        File file = new File(STOCK_FILE);
        if (!file.exists()) {
            System.out.println("Generating new stock...");
            List<FoodItem> newFoodItems = generateRandomFoodItems();
            saveStockData(newFoodItems);
            return newFoodItems;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<FoodItem>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load stock! Creating new...");
            e.printStackTrace();
            List<FoodItem> newFoodItems = generateRandomFoodItems();
            saveStockData(newFoodItems);
            return newFoodItems;
        }
    }

    // Save institutions data
    public static void saveToFile(String filename, List<Institution> institutions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(institutions);
            System.out.println("Institutions saved to: " + new File(filename).getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save institutions!");
            e.printStackTrace();
        }
    }

    // Load institutions from file
    private static List<Institution> readFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Institution>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load institutions!");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Generate new food stock when no saved data is found
    private static List<FoodItem> generateRandomFoodItems() {
        List<FoodItem> foodItems = new ArrayList<>();
        Random random = new Random();
        try (BufferedReader br = getBufferedReader("src/Read_only_original_files/FOODS.csv")) {
            if (br == null) return foodItems;

            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String name = fields[0];
                    String description = fields[1];
                    double price = random.nextDouble() * 49 + 1;
                    int quantity = random.nextInt(100) + 1;

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(3) + 1);
                    calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
                    calendar.set(Calendar.MINUTE, random.nextInt(60));
                    calendar.set(Calendar.SECOND, random.nextInt(60));
                    String expirationDate = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss").format(calendar.getTime());

                    foodItems.add(new FoodItem(name, description, price, quantity, expirationDate));
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read food CSV file!");
            e.printStackTrace();
        }
        return foodItems;
    }

    
    public static List<Institution> loadInstitutions(List<FoodItem> globalFoodItems) {
        File file = new File(INSTITUTION_FILE);
        if (file.exists()) {
            List<Institution> savedInstitutions = readFromFile(INSTITUTION_FILE);

            // Reassign global FoodItems by matching names 
            for (Institution inst : savedInstitutions) {
                List<FoodItem> correctedFoodItems = new ArrayList<>();
                for (FoodItem instItem : inst.getFoodItems()) {
                    for (FoodItem globalItem : globalFoodItems) {
                        if (globalItem.getName().equals(instItem.getName())) {
                            correctedFoodItems.add(globalItem);
                            break;
                        }
                    }
                }
                inst.clearFoodItems();
                inst.getFoodItems().addAll(correctedFoodItems);
            }

            // Check and correct empty institutions 
            int globalIndex = 0;
            for (Institution inst : savedInstitutions) {
                if (inst.getFoodItems().isEmpty()) {
                    for (int i = 0; i < 12; i++) {
                        inst.addFoodItem(globalFoodItems.get(globalIndex % globalFoodItems.size()));
                        globalIndex++;
                    }
                }
            }
            saveToFile(INSTITUTION_FILE, savedInstitutions);
            return savedInstitutions;
        } else {
            // Load from CSV (correct cyclic assignment)
            List<Institution> institutions = new ArrayList<>();
            int foodIndex = 0;

            try (BufferedReader br = getBufferedReader("src/Read_only_original_files/INSTITUTION.csv")) {
                if (br == null) return institutions;

                br.readLine(); // skip header
                String line;

                //CATY PULA
                if (globalFoodItems.isEmpty()) {
                    System.err.println("Global food item list is empty. Cannot assign to institutions.");
                    return institutions; // safely return without crashing
                }

                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    Institution institution = new Institution(fields[0]);

                    // cyclic assignment of global items
                    for (int i = 0; i < 12; i++) {
                        institution.addFoodItem(globalFoodItems.get(foodIndex % globalFoodItems.size()));
                        foodIndex++;
                    }
                    institutions.add(institution);
                }

                saveToFile(INSTITUTION_FILE, institutions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return institutions;
        }
    }



    // Check and reset expired food items
    public static void checkAndResetExpiredFoods(List<FoodItem> foodItems) {
        Date now = new Date();

        for (FoodItem item : foodItems) {
            try {
                Date expiryDate = dateFormat.parse(item.getExpirationDate());
                if (expiryDate.before(now)) {
                    System.out.println("Expired found on load: " + item.getName());
                    if (expiryDate.before(now)) {
                    	item.resetFoodItem(); // reset both stock and expiry
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        saveStockData(foodItems); // Save changes clearly
    }


    // Load CSV files correctly even when running from a JAR
    private static BufferedReader getBufferedReader(String filename) {
        File file = new File(filename);
        if (file.exists()) { 
            try {
                return new BufferedReader(new FileReader(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // If running inside a JAR file, load from resources
            try {
                InputStream inputStream = fileHandler.class.getClassLoader().getResourceAsStream(filename);
                if (inputStream != null) {
                    return new BufferedReader(new InputStreamReader(inputStream));
                }
            } catch (Exception e) {
                System.err.println("Could not load CSV from JAR resources.");
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static final String ORDER_HISTORY_FILE = "order_history.ser";

    public static List<OrderRecord> loadOrderHistory() {
        File file = new File(ORDER_HISTORY_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<OrderRecord>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveOrderHistory(List<OrderRecord> orders) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDER_HISTORY_FILE))) {
            oos.writeObject(orders);
            System.out.println("Order history saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}