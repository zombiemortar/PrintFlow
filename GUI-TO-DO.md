### Step 32: JavaFX GUI Implementation Checklist

1) Create JavaFX app skeleton (done)
- Add src layout, minimal Main.java that extends javafx.application.Application and shows an empty scene.
- Verify it compiles and runs without FXML first.

2) Add scene navigation helper (done)
- Create SceneNavigator to load FXML, set scenes on the primary Stage, and pass simple params.

3) Implement Login screen (stubbed) (done)
- resources/fxml/LoginView.fxml with username, password, Login button.
- LoginController performs a stub auth flow and navigates to Dashboard.

3a) Enhance Login screen with user management (done)
- Add Create Account button and Remember Me checkbox to LoginView.fxml.
- LoginController performs credential verification via SecurityManager.authenticateUser().
- Add Create Account functionality: validate input, check username availability, create user via SecurityManager.createUser().
- Implement login persistence: save/load user session data, auto-login on app startup if Remember Me was checked.
- Show appropriate error messages for invalid credentials, username conflicts, or validation failures.

4) Add alerts/validation utility (done)
- Create AlertUtil for error/info dialogs and reuse across controllers.

5) Implement Dashboard screen (done)
- resources/fxml/DashboardView.fxml with buttons: New Order, Queue, Inventory, Pricing, Invoices, Logout.

6) Implement Order Form screen (done)
- OrderFormView.fxml with fields: material dropdown, dimensions (LxWxHcm), quantity, special instructions.
- OrderFormController creates Order via User.submitOrder() and shows results.

7) Wire input validation (done)
- Use InputValidator and SystemConfig limits in OrderFormController before submitting; show errors via AlertUtil.

8) Implement Order Queue screen (done)
- OrderQueueView.fxml with TableView<Order> listing ID, user, material, qty, status, priority, est. hours.

9) Queue actions (done)
- Buttons to set status (processing/completed) and change priority (rush if allowed); refresh table.

10) Implement Inventory screen (done)
- InventoryView.fxml listing materials and stock.
- Add material via AdminUser.addMaterial() and allow stock updates.

11) Implement Pricing/Config screen (done)
- PricingConfigView.fxml shows and edits SystemConfig values; Save/Load buttons use file persistence.

12) Implement Invoice screen (done)
- InvoiceView.fxml with order ID search; display Invoice.generateSummary() for selected order.

13) Full Data Persistence (done)
- Implement comprehensive data persistence across application restarts.
- Ensure all data (invoices, materials, orders, users, system config) is automatically saved and loaded.
- Use DataManager to persist state changes immediately when data is modified.
- Add startup data loading in Main.java to restore complete application state.
- Implement graceful shutdown handling to save all pending changes.

14) Factory Reset Functionality
- Add a "Factory Reset" button to the Dashboard (admin-only access).
- Implement factory reset functionality that:
  - Resets all price baselines to system defaults (use SystemConfig default values).
  - Clears all materials from inventory (empty materials.txt).
  - Removes all user accounts except the default admin account.
  - Creates a single default admin account with username "admin" and password "admin".
  - Clears all orders and order queue data.
  - Resets system configuration to factory defaults.
- Add confirmation dialog before performing factory reset (warn user that all data will be lost).
- Implement proper data cleanup in DataManager to handle factory reset operations.
- Ensure factory reset is irreversible and provides clear feedback to user.

15) Exports
- Add CSV/JSON export buttons on Invoice and Dashboard using ExportManager.

16) Theming
- Add a global stylesheet and a light/dark theme toggle in Dashboard to swap stylesheets.

17) Run configuration docs
- Document Windows run steps. If using Maven, add org.openjfx dependencies; otherwise provide --module-path and --add-modules.

18) Minimal smoke tests
- Unit-test non-UI controller logic (validation, mapping to models); avoid full UI harness.

