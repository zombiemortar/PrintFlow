import java.util.HashMap;
import java.util.Map;

/**
 * Minimal inventory store for material stock levels (in grams).
 */
public class Inventory {
    private static final Map<Material, Integer> materialToGramsAvailable = new HashMap<>();

    private Inventory() {
    }

    public static void setStock(Material material, int grams) {
        if (material == null || grams < 0) {
            return;
        }
        materialToGramsAvailable.put(material, grams);
    }

    public static int getStock(Material material) {
        if (material == null) {
            return 0;
        }
        Integer grams = materialToGramsAvailable.get(material);
        return grams != null ? grams : 1000;
    }

    public static boolean hasSufficient(Material material, int gramsNeeded) {
        return getStock(material) >= gramsNeeded;
    }

    public static boolean consume(Material material, int grams) {
        if (material == null || grams <= 0) {
            return false;
        }
        int current = getStock(material);
        if (current < grams) {
            return false;
        }
        materialToGramsAvailable.put(material, current - grams);
        return true;
    }
}


