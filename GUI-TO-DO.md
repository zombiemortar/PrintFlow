### Step 32: JavaFX GUI Implementation Checklist

1) Create JavaFX app skeleton (done)
- Add src layout, minimal Main.java that extends javafx.application.Application and shows an empty scene.
- Verify it compiles and runs without FXML first.

2) Add scene navigation helper (done)
- Create SceneNavigator to load FXML, set scenes on the primary Stage, and pass simple params.

3) Implement Login screen (stubbed) (done)
- resources/fxml/LoginView.fxml with username, password, Login button.
- LoginController performs a stub auth flow and navigates to Dashboard.

4) Add alerts/validation utility (done)
- Create AlertUtil for error/info dialogs and reuse across controllers.

5) Implement Dashboard screen (done)
- resources/fxml/DashboardView.fxml with buttons: New Order, Queue, Inventory, Pricing, Invoices, Logout.

6) Implement Order Form screen (done)
- OrderFormView.fxml with fields: material dropdown, dimensions (LxWxHcm), quantity, special instructions.
- OrderFormController creates Order via User.submitOrder() and shows results.

7) Wire input validation
- Use InputValidator and SystemConfig limits in OrderFormController before submitting; show errors via AlertUtil.

8) Implement Order Queue screen
- OrderQueueView.fxml with TableView<Order> listing ID, user, material, qty, status, priority, est. hours.

9) Queue actions
- Buttons to set status (processing/completed) and change priority (rush if allowed); refresh table.

10) Implement Inventory screen
- InventoryView.fxml listing materials and stock.
- Add material via AdminUser.addMaterial() and allow stock updates.

11) Implement Pricing/Config screen
- PricingConfigView.fxml shows and edits SystemConfig values; Save/Load buttons use file persistence.

12) Implement Invoice screen
- InvoiceView.fxml with order ID search; display Invoice.generateSummary() for selected order.

13) Exports
- Add CSV/JSON export buttons on Invoice and Dashboard using ExportManager.

14) Theming
- Add a global stylesheet and a light/dark theme toggle in Dashboard to swap stylesheets.

15) Run configuration docs
- Document Windows run steps. If using Maven, add org.openjfx dependencies; otherwise provide --module-path and --add-modules.

16) Minimal smoke tests
- Unit-test non-UI controller logic (validation, mapping to models); avoid full UI harness.

