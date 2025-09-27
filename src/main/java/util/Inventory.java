package util;

import java.util.HashMap;
import java.util.Map;
import model.Material;

/**
 * Minimal inventory store for material stock levels (in grams).
 */
public class Inventory {
    private static final Map<String, Integer> materialToGramsAvailable = new HashMap<>();

    private Inventory() {
    }

    /**
     * Gets a unique key for a material based on brand, type, and color.
     */
    private static String getMaterialKey(Material material) {
        if (material == null) {
            return null;
        }
        return material.getBrand() + "|" + material.getType() + "|" + material.getColor();
    }

    public static void setStock(Material material, int grams) {
        if (material == null || grams < 0) {
            return;
        }
        String key = getMaterialKey(material);
        if (key != null) {
            materialToGramsAvailable.put(key, grams);
        }
    }

    public static int getStock(Material material) {
        if (material == null) {
            return 0;
        }
        String key = getMaterialKey(material);
        if (key == null) {
            return 0;
        }
        Integer grams = materialToGramsAvailable.get(key);
        return grams != null ? grams : 1000;
    }

    public static boolean hasSufficient(Material material, int gramsNeeded) {
        return getStock(material) >= gramsNeeded;
    }

    public static boolean consume(Material material, int grams) {
        if (material == null || grams <= 0) {
            return false;
        }
        String key = getMaterialKey(material);
        if (key == null) {
            return false;
        }
        int current = getStock(material);
        if (current < grams) {
            return false;
        }
        materialToGramsAvailable.put(key, current - grams);
        return true;
    }
}

