package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Gui extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        stage.setTitle("ChanMux");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        stage.setResizable(false);

    }

    public static void initGui(String[] args) {
        launch(args);
    }
}