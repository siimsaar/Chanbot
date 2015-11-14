package gui;

import core.Topic;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller extends OutputStream implements Initializable {

    boolean dlWEBM;

    @FXML
    public Button startButton;
    @FXML
    public ToggleButton enableWEBM;
    @FXML
    public TextArea stdout;

    public void startThread(ActionEvent event) {
        Topic kek = new Topic("mu", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogMu.json", "mu");
        if(dlWEBM){
            kek.setWEBM();
        }
        kek.start();
    }
    public void enableWEBM(ActionEvent event) {
        boolean dlWEBM = true;
    }

    //this is from stackoverflow, i have no idea how it works
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));
    }
    //this is from stackoverflow, i have no idea how it works
    public void appendText(String str) {
        Platform.runLater(() -> stdout.appendText(str));
    }

    @Override
    public void write(int arg0) throws IOException {
        // TODO Auto-generated method stub

    }
}
