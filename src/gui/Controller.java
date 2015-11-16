package gui;

import core.Topic;
import javafx.application.Platform;
import javafx.beans.value.ObservableListValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller extends OutputStream implements Initializable {

    @FXML
    public Button startButton;
    @FXML
    public CheckBox enableWEBM;
    @FXML
    public TextArea stdout;
    @FXML
    public ComboBox<String> topicBox = new ComboBox<>();
    @FXML
    public TextField refreshInt;


    public void startThread(ActionEvent event) {
        try {
            if (topicBox.getValue().equals("/mu/ - kpg")) {
                Topic kek = new Topic("kpg", "./topics/", "./topics/kpg/catalogMu.json");
                if (enableWEBM.isSelected()) {
                    kek.setWEBM();
                }
                kek.start();
            } else if (topicBox.getValue().equals("/p/ - Recent Photo")) {
                Topic kuk = new Topic("p", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogP.json");
                kuk.start();
            } else if (topicBox.getValue().equals("/2ch/ - Webm Thread")) {
            Topic kuk = new Topic("2webm", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogP.json");
            kuk.start();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("[WARNING] Pick a topic!");
        }
    }

    //this is from stackoverflow, i have no idea how it works
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topicBox.getItems().addAll("/mu/ - kpg", "/p/ - Recent Photo", "/2ch/ - Webm Thread");
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
    }
}
