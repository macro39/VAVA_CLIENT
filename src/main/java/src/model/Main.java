package src.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("lang", Locale.getDefault());

        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"), bundle);
        primaryStage.setTitle("Vehicle List");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/image/logo.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
