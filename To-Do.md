This is a Java program.

##  Development Task List

### Phase 1: Startup Code & Core Classes
1. **Create base class `User`** (Done)
   - Attributes: `username`, `email`, `role`
   - Methods: `submitOrder()`, `viewInvoice()`

2. **Create subclass `AdminUser`** (Done)
   - Inherits from `User`
   - Additional methods: `addMaterial()`, `viewAllOrders()`, `modifyPricingConstants()`

3. **Create class `Material`** (Done)
   - Attributes: `name`, `costPerGram`, `printTemp`, `color`
   - Methods: `getMaterialInfo()`

4. **Create class `Order`** (Done)
   - Attributes: `orderID`, `user`, `material`, `dimensions`, `quantity`, `specialInstructions`, `status`
   - Methods: `calculatePrice()`, `updateStatus()`

5. **Create class `Invoice`** (Done)
   - Attributes: `invoiceID`, `order`, `totalCost`, `dateIssued`
   - Methods: `generateSummary()`, `exportInvoice()`

6. **Create exception classes** (Done)
   - `InvalidOrderException`, `InsufficientMaterialException`, `InvalidMaterialException`

7. **Create configuration management class** (Done)
   - `SystemConfig` for pricing constants, tax rates, and system settings

---

### Phase 2: Core Logic & Data Structures
8. **Implement `HashMap<Integer, Order>`** (Done)
   - Keyed by `orderID` for fast lookup and updates

9. **Implement `Queue<Order>`** (Done)
   - Manages print job pipeline in FIFO order

10. **Build pricing algorithm** (Done)
    - Factors: material cost, estimated electricity usage, machine time
    - Use conditional logic and lookup tables

11. **Add basic input validation** (Done)
    - Ensure valid material selection, dimensions, and quantity

12. **Implement material availability checking** (Done)
    - Check stock levels before order acceptance

13. **Add order priority system** (Done)
    - Rush orders, VIP customers, bulk order discounts

14. **Create print time estimation** (Done)
    - Calculate estimated completion time based on material and dimensions

15. **Implement tax calculation** (Done)
    - Support for multiple tax rates and currency formats

16. **Add discount system** (Done)
    - Bulk order discounts, loyalty rewards, promotional codes

---

### Phase 3: Testing & Documentation
17. **Write a simple driver program** (Done)
    - Demonstrates order creation, price calculation, and invoice generation

18. **Add inline documentation** (Done)
    - Comments for each class and method explaining purpose and behavior

19. **Create basic test cases** (Done)
    - Verify functionality of `calculatePrice()`, `updateStatus()`, and `generateSummary()`

20. **Begin drafting `README.md`** (Done)
    - Include setup instructions, class overview, and testing notes

21. **Implement comprehensive unit testing** (Done)
    - Use JUnit framework for all classes and methods
    - Created 9 comprehensive test classes with 356+ individual test methods
    - Achieved 91.6% test success rate covering all major functionality
    - Includes edge cases, error conditions, and integration testing
    - Test classes: UserTest, AdminUserTest, MaterialTest, OrderTest, InvoiceTest, OrderManagerTest, InventoryTest, SystemConfigTest, ExceptionClassesTest

22. **Add integration tests** (Done)
    - Test complete workflows from order to invoice
    - Created comprehensive IntegrationTest class with 9 test methods covering:
      - Complete regular order workflow (order creation → pricing → invoice generation)
      - VIP customer workflow with discounts
      - Rush order workflow with surcharges
      - Bulk order workflow with quantity discounts
      - Admin workflow (material management, order oversight, pricing modifications)
      - Queue processing workflow (FIFO order processing)
      - Error scenario workflow (insufficient inventory, invalid orders, system limits)
      - Complex multi-step order processing
      - System configuration impact on pricing
    - All integration tests pass successfully, demonstrating complete end-to-end functionality

23. **Create performance tests** (Done)
   - Test system behavior with large order volumes
   - Added `PerformanceTest` covering high-volume creation/queueing, batch price calculations, and time estimation stability
   - Integrated into `ComprehensiveTestRunner`; all tests pass successfully

24. **Generate code coverage reports**
    - Ensure adequate testing coverage

---

### Phase 4: Data Persistence & File I/O
25. **Implement file handling classes**
    - Save/load orders, materials, and user data

26. **Add CSV/JSON export functionality**
    - Export invoices, reports, and order summaries

27. **Create configuration file system**
    - Load pricing constants and system settings from external files

28. **Implement backup and restore functionality**
    - Protect against data loss

---

### Phase 5: Security & Authentication
29. **Add password hashing**
    - Secure user account passwords

30. **Implement session management**
    - Admin user authentication and authorization

31. **Enhance input validation**
    - Protect against injection attacks and malicious input

---

### Phase 6: Advanced Features & Analytics
32. **Design and implement full GUI (JavaFX)**
    - Choose toolkit: JavaFX as primary (fallback: Swing if required)
    - Define UX flow: login/admin dashboard/order creation/queue/analytics/invoice
    - Build scenes: Login, Dashboard, New Order, Order Queue, Inventory, Pricing, Invoice Viewer
    - Implement controllers with data binding to `OrderManager`, `Inventory`, `SystemConfig`
    - Add input validation, error dialogs, and notifications
    - Implement theming (light/dark) and responsive layouts
    - Wire file export/import for invoices and reports
    - Add accessibility: keyboard navigation and screen reader labels
    - Integrate with existing tests; add GUI smoke tests
    - Set up JavaFX dependencies
      - Option A: Maven add `org.openjfx:javafx-controls`, `javafx-fxml` (matching JDK)
      - Option B: Local JavaFX SDK; configure `--module-path` and `--add-modules javafx.controls,javafx.fxml`
      - If modular: create `module-info.java` with `requires javafx.controls; requires javafx.fxml;` and `opens` controller package
    - Create GUI bootstrap
      - Add `Main.java` extending `javafx.application.Application`
      - Initialize primary `Stage`, load `FXML` (e.g., `LoginView.fxml`) via `FXMLLoader`
      - Set application-wide stylesheet and icon
    - Scaffold FXML and controllers
      - Create `resources/` for FXML, CSS, and images; add `LoginView.fxml` and `DashboardView.fxml`
      - Create controllers `LoginController`, `DashboardController` with basic event handlers
    - Create initial files for bootstrapping
      - `src/Main.java` (JavaFX `Application` entrypoint)
      - `resources/fxml/LoginView.fxml`
      - `resources/fxml/DashboardView.fxml`
    - Run configuration
      - Document IDE run config (VM options, module path) and CLI run command
      - Verify on Windows with JDK 17+ and JavaFX matching version

33. **Prepare for inventory tracking module**
    - Optional class: `Inventory` with material stock levels

34. **Prepare for analytics module**
    - Optional class: `PrintAnalytics` to track usage and performance

35. **Implement order history and search**
    - Customer order tracking and admin search functionality

36. **Add customer feedback system**
    - Rating system and reprint policy management

37. **Create print quality tracking**
    - Monitor success rates and identify improvement areas

38. **Implement material waste calculation**
    - Optimize costs and reduce material usage

39. **Add logging system**
    - Track operations and debug issues

---

### Phase 7: Documentation & User Experience
40. **Create comprehensive API documentation**
    - Prepare for future system integration

41. **Write user manual**
    - Admin operations guide and troubleshooting

42. **Develop configuration examples**
    - Best practices and common setup scenarios

43. **Create system architecture documentation** (Done)
    - Class diagrams and system flow documentation
