package utils;

/**
 * Represents a 3D printing material with its properties and costs.
 * Used for calculating print costs and determining print parameters.
 */
public class Material {
    private String name;
    private double costPerGram;
    private int printTemp;
    private String color;
    
    /**
     * Constructor for creating a new material.
     * @param name The name of the material
     * @param costPerGram The cost per gram of the material
     * @param printTemp The recommended printing temperature in Celsius
     * @param color The available color of the material
     */
    public Material(String name, double costPerGram, int printTemp, String color) {
        this.name = name;
        this.costPerGram = costPerGram;
        this.printTemp = printTemp;
        this.color = color;
    }
    
    /**
     * Default constructor.
     */
    public Material() {
        this.name = "";
        this.costPerGram = 0.0;
        this.printTemp = 0;
        this.color = "";
    }
    
    /**
     * Gets comprehensive information about the material.
     * @return A formatted string containing all material information
     */
    public String getMaterialInfo() {
        return String.format("Material: %s\nCost per gram: $%.2f\nPrint temperature: %d°C\nColor: %s",
                name, costPerGram, printTemp, color);
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", costPerGram=" + costPerGram +
                ", printTemp=" + printTemp +
                ", color='" + color + '\'' +
                '}';
    }
}
