package gui;

import core.Topic;
import core.SettingsHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import threads.ThreadManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
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
    @FXML
    public Button streamButton;
    @FXML
    public Button killButton;


    public void startThread(ActionEvent event) {
        if(refreshInt.getText().isEmpty()) {
            SettingsHandler.setUpdateInt(10000);
        } else {
            SettingsHandler.setUpdateInt(Integer.parseInt(refreshInt.getText()) * 1000);
        }
        try {
            if (topicBox.getValue().equals("/mu/ - kpg")) {
                ThreadManager.threadFactory("kpg");
            } else if (topicBox.getValue().equals("/p/ - Recent Photo")) {
                System.out.println("[INFO] na man");
            } else if (topicBox.getValue().equals("/2ch/ - Webm Thread")) {
                ThreadManager.threadFactory("2webm");
            } else if (topicBox.getValue().equals("/2ch/ - Webm kpg")) {
                ThreadManager.threadFactory("rukpg");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("[WARNING] Pick a topic!");
        }
    }

    public void streamTo(ActionEvent event) {
        ThreadManager.setStartStream(true);
        try {
            if (topicBox.getValue().equals("/2ch/ - Webm Thread")) {
                ThreadManager.threadFactory("2webm");
            } else if (topicBox.getValue().equals("/2ch/ - Webm kpg")) {
                ThreadManager.threadFactory("rukpg");
            } else {
                System.out.println("[ERROR] Streaming isnt supported on this topic");
                ThreadManager.setStartStream(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void killAll(ActionEvent event) {
        try {
            ThreadManager.killAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWEBM(ActionEvent event) {
        if(ThreadManager.isStartWithWebm()) {
            ThreadManager.setStartWithWebm(false);
        } else {
            ThreadManager.setStartWithWebm(true);
        }
    }


    //this is from stackoverflow, i have no idea how it works
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topicBox.getItems().addAll("/mu/ - kpg", "/p/ - Recent Photo", "/2ch/ - Webm Thread", "/2ch/ - Webm kpg");
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
