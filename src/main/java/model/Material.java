package model;

/**
 * Represents a 3D printing material with its properties and costs.
 * Used for calculating print costs and determining print parameters.
 */
public class Material {
    private String brand;
    private String type;
    private double costPerGram;
    private int printTemp;
    private String color;
    
    /**
     * Constructor for creating a new material.
     * @param brand The brand of the material
     * @param type The type of the material (PLA, PETG, ABS, etc.)
     * @param costPerGram The cost per gram of the material
     * @param printTemp The recommended printing temperature in Celsius
     * @param color The available color of the material
     */
    public Material(String brand, String type, double costPerGram, int printTemp, String color) {
        this.brand = brand;
        this.type = type;
        this.costPerGram = costPerGram;
        this.printTemp = printTemp;
        this.color = color;
    }
    
    /**
     * Default constructor.
     */
    public Material() {
        this.brand = "";
        this.type = "";
        this.costPerGram = 0.0;
        this.printTemp = 0;
        this.color = "";
    }
    
    /**
     * Gets comprehensive information about the material.
     * @return A formatted string containing all material information
     */
    public String getMaterialInfo() {
        return String.format("Brand: %s\nType: %s\nCost per gram: $%.2f\nPrint temperature: %d°C\nColor: %s",
                brand, type, costPerGram, printTemp, color);
    }
    
    /**
     * Gets a display name for the material in the format "Type - Brand (Color)"
     * @return A formatted display name
     */
    public String getDisplayName() {
        return String.format("%s - %s (%s)", type, brand, color);
    }
    
    /**
     * Gets the material name for compatibility (combines brand and type)
     * @return A combined name string
     */
    public String getName() {
        return brand + " " + type;
    }
    
    // Getters and Setters
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double getCostPerGram() {
        return costPerGram;
    }
    
    public void setCostPerGram(double costPerGram) {
        this.costPerGram = costPerGram;
    }
    
    public int getPrintTemp() {
        return printTemp;
    }
    
    public void setPrintTemp(int printTemp) {
        this.printTemp = printTemp;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return "Material{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", costPerGram=" + costPerGram +
                ", printTemp=" + printTemp +
                ", color='" + color + '\'' +
                '}';
    }
}

