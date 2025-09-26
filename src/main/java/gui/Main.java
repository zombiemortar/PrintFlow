package gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize the scene navigator and start with login screen
        SceneNavigator navigator = new SceneNavigator(primaryStage);
        navigator.navigate("/fxml/LoginView.fxml", "PrintFlow - Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}



