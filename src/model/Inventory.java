package model;

import java.util.*;

/**
 * Traceability: Class Diagram -> Inventory class
 * Aggregates MotorPart objects and computes average weekly sales.
 */
public class Inventory {
    private final List<MotorPart> partsList;
    private Date lastUpdated;

    public Inventory() {
        partsList   = new ArrayList<>();
        lastUpdated = new Date();
    }

    /** Traceability: Class Diagram -> Inventory::updateStock() */
    public void updateStock(String partNumber, int newQty) {
        MotorPart part = getPartByNumber(partNumber);
        if (part != null) {
            part.setCurrentStock(newQty);
            lastUpdated = new Date();
        }
    }

    /** Traceability: Class Diagram -> Inventory::getPartByNumber() */
    public MotorPart getPartByNumber(String partNumber) {
        for (MotorPart p : partsList)
            if (p.getPartNumber().equals(partNumber)) return p;
        return null;
    }

    /**
     * Traceability: Class Diagram -> Inventory::getAvgWeeklySales()
     */
    public float getAvgWeeklySales(String partNumber, List<SaleTransaction> transactions) {
        float total = 0;
        int count = 0;
        for (SaleTransaction t : transactions) {
            if (t.getPartNumber().equals(partNumber)) {
                total += t.getQuantitySold();
                count++;
            }
        }
        return count == 0 ? 0 : (total / count);
    }

    // ✅ OPTIONAL: future auto-threshold support (safe, not breaking anything)
    public float calculateDynamicThreshold(String partNumber, List<SaleTransaction> transactions) {
        float avgSales = getAvgWeeklySales(partNumber, transactions);
        return Math.max(5, avgSales * 1.5f); // safety factor
    }

    /**
     * Traceability: Class Diagram -> Inventory::generateSalesGraph()
     */
    public Map<String, Integer> generateSalesGraph(List<SaleTransaction> transactions) {
        Map<String, Integer> monthly = new LinkedHashMap<>();
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for (String m : months) monthly.put(m, 0);

        Calendar cal = Calendar.getInstance();
        for (SaleTransaction t : transactions) {
            cal.setTime(t.getSaleDate());
            String m = months[cal.get(Calendar.MONTH)];
            monthly.put(m, monthly.get(m) + t.getQuantitySold());
        }
        return monthly;
    }

    public void addPart(MotorPart part) {
        partsList.add(part);
        lastUpdated = new Date();
    }

    public boolean removePart(String partNumber) {
        return partsList.removeIf(p -> p.getPartNumber().equals(partNumber));
    }

    public List<MotorPart> getPartsList() {
       return partsList;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
}