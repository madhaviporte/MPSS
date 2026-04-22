package model;

public class MotorPart {

    private final String partNumber;
    private final  String partName;
    private  int currentStock;
    private final  float thresholdValue; // KEEP for UI compatibility
    private final String rackNumber;
    private final String vendorId;

    public enum PartState { IN_STOCK, LOW_STOCK, ORDER_PENDING, OUT_OF_STOCK, RESTOCKED, DISCONTINUED }
    private PartState state;

    // ✅ SAME constructor (UI break nahi hoga)
    public MotorPart(String partNumber, String partName, int currentStock, float thresholdValue, String rackNumber, String vendorId) {
        this.partNumber   = partNumber;
        this.partName     = partName;
        this.currentStock = currentStock;
        

        // ❌ user input ignore
        this.thresholdValue = calculateThreshold();

        this.rackNumber = rackNumber;
        this.vendorId   = vendorId;

        if (currentStock == 0)
    state = PartState.OUT_OF_STOCK;
else if (currentStock < Math.max(5, currentStock * 0.3f))
    state = PartState.LOW_STOCK;
else
    state = PartState.IN_STOCK;
    }

    // ✅ AUTO threshold logic
    private float calculateThreshold(){
        return Math.max(5, currentStock * 0.3f);
    }

    // ✅ Always return auto value
    public float getThresholdValue(){
        return thresholdValue;
    }

    public void updateState() {
        if (state == PartState.DISCONTINUED) return;

        float threshold = calculateThreshold();

        if (currentStock == 0)
            state = PartState.OUT_OF_STOCK;
        else if (currentStock < threshold)
            state = PartState.LOW_STOCK;
        else
            state = PartState.IN_STOCK;
    }

    public void placeOrder() {
        if (state == PartState.LOW_STOCK || state == PartState.OUT_OF_STOCK)
            state = PartState.ORDER_PENDING;
    }

    public void receiveStock(int qty) {
        currentStock += qty;
        updateState();
    }

    public void discontinue() {
        state = PartState.DISCONTINUED;
    }

    // Getters
    public String getPartNumber() { return partNumber; }
    public String getPartName() { return partName; }
    public int getCurrentStock() { return currentStock; }
    public String getRackNumber() { return rackNumber; }
    public String getVendorId() { return vendorId; }
    public PartState getState() { return state; }

    // Setter
    public void setCurrentStock(int s) {
        this.currentStock = s;
        updateState();
    }

    // (optional debug)
    public String getPartDetails() {
        return String.format("Part#: %s | Name: %s | Stock: %d | Threshold: %.1f | Rack: %s",
                partNumber, partName, currentStock, getThresholdValue(), rackNumber);
    }
}