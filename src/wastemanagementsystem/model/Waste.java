package wastemanagementsystem.model;

public class Waste {
    private String itemName;
    private String wasteType;
    private double weight;
    private String status = "Pending";

    public Waste(String itemName, String wasteType, double weight) {
        this.itemName = itemName;
        this.wasteType = wasteType;
        this.weight = weight;
    }

    public void markAsCollected() { this.status = "Collected"; }
    public String getStatus() { return status; }
    public String getType() { return wasteType; }
    public double getWeight() { return weight; }
    public String getItemName() { return itemName; }

    public double getGSP() {
        return switch (wasteType.toLowerCase()) {
            case "plastic" -> 3.0;
            case "organic" -> 0.5;
            case "hazardous" -> 6.0;
            default -> 1.0;
        };
    }

    public double impactScore() { return weight * getGSP(); }

    public String getDetails() {
        return wasteType + ": " + itemName + " (" + weight + "kg) > Impact: " + impactScore() + " GSP";
    }
}