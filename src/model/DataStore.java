package model;

import java.util.*;

/**
 * Central singleton data store.
 * Provides shared Inventory, Vendors, Transactions to all UI panels.
 */
public class DataStore {
    private static DataStore instance;

    public final Inventory                inventory      = new Inventory();
    public final Map<String, Vendor>      vendors        = new LinkedHashMap<>();
    public final List<SaleTransaction>    transactions   = new ArrayList<>();
    public final OrderManager             orderManager   = new OrderManager();
    public final ReportGenerator          reportGenerator= new ReportGenerator();
    public       List<String>             lastOrderList  = new ArrayList<>();

    private DataStore() { seedData(); }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    private void seedData() {
        // --- Vendors ---
        vendors.put("V001", new Vendor("V001","Raj Auto Supplies","MG Road, Ranchi","9801234567"));
        vendors.put("V002", new Vendor("V002","Kumar Motors","Station Rd, Dhanbad","9812345678"));
        vendors.put("V003", new Vendor("V003","Singh Parts","Main Bazar, Bokaro","9823456789"));

        // --- Parts (some below threshold to demonstrate JIT) ---
        inventory.addPart(new MotorPart("P001","Brake Pad",  5, 10.0f,"A1","V001"));
        inventory.addPart(new MotorPart("P002","Oil Filter", 2,  8.0f,"A2","V001"));
        inventory.addPart(new MotorPart("P003","Air Filter",15,  6.0f,"B1","V002"));
        inventory.addPart(new MotorPart("P004","Spark Plug",20,  5.0f,"B2","V002"));
        inventory.addPart(new MotorPart("P005","Clutch Plate",0,  7.0f,"C1","V003"));
        inventory.addPart(new MotorPart("P006","Gear Cable", 3,  4.0f,"C2","V003"));
        inventory.addPart(new MotorPart("P007","Chain Set", 12,  5.0f,"D1","V001"));
        inventory.addPart(new MotorPart("P008","Head Lamp",  8,  3.0f,"D2","V002"));

        // --- Transactions (spread across months) ---
        Calendar cal = Calendar.getInstance();
        Random rnd = new Random(42);
        String[] parts = {"P001","P002","P003","P004","P005","P006","P007","P008"};
        double[] prices = {350,120,90,25,800,45,650,200};
        int txId = 1;
        for (int month = 0; month < 12; month++) {
            int txCount = 3 + rnd.nextInt(5);
            for (int i = 0; i < txCount; i++) {
                int pidx = rnd.nextInt(parts.length);
                cal.set(2024, month, 1 + rnd.nextInt(28));
                transactions.add(new SaleTransaction(
                        "TX" + String.format("%03d", txId++),
                        cal.getTime(),
                        1 + rnd.nextInt(4),
                        prices[pidx],
                        parts[pidx]));
            }
        }
    }
}
