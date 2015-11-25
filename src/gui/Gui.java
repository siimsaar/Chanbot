package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Klass alustab GUI tööd
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public class Gui extends Application {
    /**
     * Loob akna FXML failist ja kuvab seda ekraanil
     * @param stage
     * @throws Exception
     */
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

    /**
     * Alustab GUI tööd
     * @param args
     */
    public static void initGui(String[] args) {
        launch(args);
    }
}