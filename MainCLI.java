import java.util.Arrays;
import java.util.Scanner;

/**
 * Command-line interface for the 3D Printing Service Management System.
 * Provides interactive and argument-based operations for users, materials,
 * inventory, orders, queue, invoices, and system configuration.
 */
public class MainCLI {

    private static final Scanner INPUT = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== PrintFlow CLI ===");
        System.out.println("Type the number of an option, or 'q' to quit.\n");

        // Lifecycle: load persisted data at startup
        loadOnStartup();

        // If arguments were provided, treat as single-shot command
        if (args != null && args.length > 0) {
            runArgsCommand(args);
            saveBeforeExit();
            return;
        }

        // Interactive menu loop
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select> ");
            String choice = INPUT.nextLine();
            if (choice == null) {
                continue;
            }
            choice = choice.trim();
            if (choice.equalsIgnoreCase("q") || choice.equalsIgnoreCase("quit") || choice.equalsIgnoreCase("exit")) {
                running = false;
                break;
            }

            switch (choice) {
                case "1":
                    handleListUsers();
                    break;
                case "2":
                    handleCreateUser();
                    break;
                case "3":
                    handleListMaterials();
                    break;
                case "4":
                    handleAddMaterial();
                    break;
                case "5":
                    handleSetStock();
                    break;
                case "6":
                    handleListOrders();
                    break;
                case "7":
                    handlePlaceOrder();
                    break;
                case "8":
                    handleDequeueNext();
                    break;
                case "9":
                    handleViewConfig();
                    break;
                case "10":
                    handleSetConfig();
                    break;
                case "11":
                    handleSaveNow();
                    break;
                default:
                    System.out.println("Unknown option. Please try again.\n");
            }
        }

        // Lifecycle: save before exit
        saveBeforeExit();
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1) List users");
        System.out.println("2) Create user");
        System.out.println("3) List materials");
        System.out.println("4) Add material");
        System.out.println("5) Set material stock (grams)");
        System.out.println("6) List orders");
        System.out.println("7) Place order");
        System.out.println("8) Dequeue next order (process queue)");
        System.out.println("9) View system config");
        System.out.println("10) Set system config values");
        System.out.println("11) Save now");
        System.out.println("q) Quit");
    }

    private static void loadOnStartup() {
        try {
            // Order matters: materials before inventory/ orders; users before orders
            DataManager.loadUsers();
            DataManager.loadMaterials();
            DataManager.loadOrders();
        } catch (Throwable t) {
            System.out.println("[Warn] Failed to load some data: " + t.getMessage());
        }
    }

    private static void saveBeforeExit() {
        try {
            boolean users = DataManager.saveUsers();
            boolean materials = DataManager.saveMaterials();
            boolean orders = DataManager.saveOrders();
            System.out.println("Saved: users=" + users + ", materials=" + materials + ", orders=" + orders);
        } catch (Throwable t) {
            System.out.println("[Warn] Failed to save some data: " + t.getMessage());
        }
    }

    // --- Handlers ---

    private static void handleListUsers() {
        User[] users = DataManager.getAllUsers();
        if (users.length == 0) {
            System.out.println("No users.");
            return;
        }
        Arrays.stream(users).forEach(u -> System.out.println("- " + u));
    }

    private static void handleCreateUser() {
        System.out.print("Username: ");
        String username = INPUT.nextLine();
        System.out.print("Email: ");
        String email = INPUT.nextLine();
        System.out.print("Role (customer/admin): ");
        String role = INPUT.nextLine();
        System.out.print("Password: ");
        String password = INPUT.nextLine();

        if (role == null || role.trim().isEmpty() || role.equalsIgnoreCase("customer")) {
            User user = new User(username, email, "customer", password);
            DataManager.addUser(user);
            System.out.println("Created user: " + user);
        } else {
            AdminUser admin = new AdminUser(username, email, password);
            DataManager.addUser(admin);
            System.out.println("Created admin: " + admin);
        }
    }

    private static void handleListMaterials() {
        Material[] materials = DataManager.getAllMaterials();
        if (materials.length == 0) {
            System.out.println("No materials.");
            return;
        }
        for (Material m : materials) {
            System.out.println("- " + m.getMaterialInfo() + ", stock=" + Inventory.getStock(m) + "g");
        }
    }

    private static void handleAddMaterial() {
        System.out.print("Name: ");
        String name = INPUT.nextLine();
        System.out.print("Cost per gram (e.g., 0.02): ");
        double cost = parseDouble(INPUT.nextLine(), -1.0);
        System.out.print("Print temp (C): ");
        int temp = parseInt(INPUT.nextLine(), -1);
        System.out.print("Color: ");
        String color = INPUT.nextLine();

        AdminUser sysAdmin = new AdminUser("system_admin", "system@example.com", "admin123");
        Material material = sysAdmin.addMaterial(name, cost, temp, color);
        if (material != null) {
            DataManager.addMaterial(material);
            System.out.println("Added material: " + material);
        } else {
            System.out.println("Failed to add material.");
        }
    }

    private static void handleSetStock() {
        System.out.print("Material name: ");
        String name = INPUT.nextLine();
        Material m = DataManager.getMaterialByName(name);
        if (m == null) {
            System.out.println("Material not found.");
            return;
        }
        System.out.print("New stock (grams): ");
        int grams = parseInt(INPUT.nextLine(), -1);
        if (grams < 0) {
            System.out.println("Invalid grams.");
            return;
        }
        Inventory.setStock(m, grams);
        System.out.println("Stock set. Now " + Inventory.getStock(m) + "g");
    }

    private static void handleListOrders() {
        Order[] orders = OrderManager.getAllOrders();
        if (orders.length == 0) {
            System.out.println("No orders.");
            return;
        }
        for (Order o : orders) {
            System.out.println("- " + o);
        }
    }

    private static void handlePlaceOrder() {
        System.out.print("Username: ");
        String username = INPUT.nextLine();
        User u = DataManager.getUserByUsername(username);
        if (u == null) {
            System.out.println("User not found. Create the user first.");
            return;
        }

        System.out.print("Material name: ");
        String matName = INPUT.nextLine();
        Material m = DataManager.getMaterialByName(matName);
        if (m == null) {
            System.out.println("Material not found.");
            return;
        }

        System.out.print("Dimensions (e.g., 10cm x 5cm x 3cm): ");
        String dims = INPUT.nextLine();
        System.out.print("Quantity: ");
        int qty = parseInt(INPUT.nextLine(), 0);
        System.out.print("Special instructions: ");
        String notes = INPUT.nextLine();

        Order order = u.submitOrder(m, dims, qty, notes);
        if (order != null) {
            System.out.println("Order placed: " + order + "; est. price=$" + order.calculatePrice());
        } else {
            System.out.println("Failed to place order. Check inputs/stock/config.");
        }
    }

    private static void handleDequeueNext() {
        Order next = OrderManager.dequeueNextOrder();
        if (next == null) {
            System.out.println("Queue is empty.");
            return;
        }
        next.updateStatus("processing");
        System.out.println("Dequeued: " + next);
    }

    private static void handleViewConfig() {
        System.out.println(SystemConfig.getConfigurationSummary());
    }

    private static void handleSetConfig() {
        System.out.print("Electricity cost per hour: ");
        double e = parseDouble(INPUT.nextLine(), -1.0);
        System.out.print("Machine time cost per hour: ");
        double m = parseDouble(INPUT.nextLine(), -1.0);
        System.out.print("Base setup cost: ");
        double b = parseDouble(INPUT.nextLine(), -1.0);
        AdminUser sysAdmin = new AdminUser("system_admin", "system@example.com", "admin123");
        boolean ok = sysAdmin.modifyPricingConstants(e, m, b);
        System.out.println(ok ? "Config updated." : "Failed to update config.");
    }

    private static void handleSaveNow() {
        saveBeforeExit();
    }

    // --- Arg-based commands ---

    private static void runArgsCommand(String[] args) {
        String cmd = args[0];
        if ("add-material".equalsIgnoreCase(cmd)) {
            // java MainCLI add-material name cost temp color
            if (args.length < 5) {
                System.out.println("Usage: java MainCLI add-material <name> <costPerGram> <printTemp> <color>");
                return;
            }
            AdminUser sysAdmin = new AdminUser("system_admin", "system@example.com", "admin123");
            Material material = sysAdmin.addMaterial(args[1], parseDouble(args[2], -1.0), parseInt(args[3], -1), args[4]);
            if (material != null) {
                DataManager.addMaterial(material);
                System.out.println("Added material: " + material);
            } else {
                System.out.println("Failed to add material.");
            }
            return;
        }

        if ("place-order".equalsIgnoreCase(cmd)) {
            // java MainCLI place-order username material qty dims [notes]
            if (args.length < 5) {
                System.out.println("Usage: java MainCLI place-order <username> <material> <qty> <dims> [notes]");
                return;
            }
            User u = DataManager.getUserByUsername(args[1]);
            Material m = DataManager.getMaterialByName(args[2]);
            int qty = parseInt(args[3], 0);
            String dims = args[4];
            String notes = args.length >= 6 ? args[5] : "";
            if (u == null || m == null) {
                System.out.println("User or material not found.");
                return;
            }
            Order o = u.submitOrder(m, dims, qty, notes);
            if (o != null) {
                System.out.println("Order placed: id=" + o.getOrderID());
            } else {
                System.out.println("Failed to place order.");
            }
            return;
        }

        System.out.println("Unknown command. Supported: add-material, place-order");
    }

    // --- Utils ---

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private static double parseDouble(String s, double def) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}


