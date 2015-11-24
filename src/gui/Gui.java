package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


public class Gui extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        stage.setTitle("ChanBot");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void initGui(String[] args) {
        launch(args);
    }
}