/**
 * Combined tests for the 3D Printing Service System.
 * Includes original Phase 1 checks plus additional validations for order queue,
 * inventory, pricing updates, priority handling, and time estimates.
 */
public class Tests {
    public static void main(String[] args) {
        System.out.println("=== SYSTEM TESTS ===\n");

        // User and Admin
        System.out.println("1. Users:");
        User customer = new User("john_doe", "john@example.com", "customer");
        AdminUser admin = new AdminUser("admin_user", "admin@example.com");
        System.out.println("Customer: " + customer);
        System.out.println("Admin: " + admin);
        System.out.println();

        // Materials
        System.out.println("2. Materials:");
        Material pla = new Material("PLA", 0.02, 200, "White");
        Material abs = new Material("ABS", 0.025, 230, "Black");
        System.out.println(pla.getMaterialInfo());
        System.out.println(abs.getMaterialInfo());
        System.out.println();

        // Config
        System.out.println("3. SystemConfig:");
        System.out.println(SystemConfig.getConfigurationSummary());
        System.out.println();

        // Admin adds stock for PLA
        System.out.println("4. Admin adds stock:");
        Material plaStocked = admin.addMaterial("PLA", 0.02, 200, "White");
        System.out.println("Added material: " + plaStocked);
        System.out.println("PLA stock: " + Inventory.getStock(pla) + "g");
        System.out.println();

        // Place orders, includes rush and normal
        System.out.println("5. Orders and pricing:");
        Order order1 = customer.submitOrder(pla, "10cm x 5cm x 3cm", 2, "High quality finish");
        Order order2 = customer.submitOrder(abs, "5cm x 5cm x 5cm", 12, "rush, need ASAP");
        if (order1 != null) {
            System.out.println("Order1: " + order1);
            System.out.println("Order1 est hours: " + order1.getEstimatedPrintHours());
            System.out.println("Order1 price: $" + String.format("%.2f", order1.calculatePrice()));
        }
        if (order2 != null) {
            System.out.println("Order2: " + order2);
            System.out.println("Order2 est hours: " + order2.getEstimatedPrintHours());
            System.out.println("Order2 price (qty discount + rush + tax): $" + String.format("%.2f", order2.calculatePrice()));
        }
        System.out.println();

        // Queue checks
        System.out.println("6. Print Queue:");
        System.out.println("Queue size: " + OrderManager.getQueueSize());
        Order next = OrderManager.dequeueNextOrder();
        System.out.println("Dequeued: " + next);
        System.out.println("Queue size now: " + OrderManager.getQueueSize());
        System.out.println();

        // Invoice
        System.out.println("7. Invoice:");
        if (order1 != null) {
            Invoice inv = new Invoice(order1, order1.calculatePrice());
            System.out.println(inv.generateSummary());
        }
        System.out.println();

        // Admin view all orders
        System.out.println("8. Admin views all orders:");
        Order[] all = admin.viewAllOrders();
        if (all != null) {
            System.out.println("Total orders: " + all.length);
            for (Order o : all) {
                System.out.println(o);
            }
        }

        System.out.println("\n=== TESTS COMPLETE ===");
    }
}





