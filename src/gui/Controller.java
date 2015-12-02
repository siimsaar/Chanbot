package gui;

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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Antud klass annab GUI elementidele funktsionaalsuse
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public class Controller extends OutputStream implements Initializable {

    @FXML
    public Button startButton;
    @FXML
    public CheckBox enableWEBM;
    @FXML
    public  TextArea stdout;
    @FXML
    public ComboBox<String> topicBox = new ComboBox<>();
    @FXML
    public TextField refreshInt;
    @FXML
    public Button streamButton;
    @FXML
    public Button killButton;

    /**
     * Meetod alustab CheckBoxis valitud teemat protsessina ja loeb ka intervalli
     * mille jooksul alustatakse uuesti failide allalaadimist
     *
     * @param event Mouseclick
     */
    public void startThread(ActionEvent event) {
        if (refreshInt.getText().isEmpty()) {
            SettingsHandler.setUpdateInt(10000);
        } else {
            SettingsHandler.setUpdateInt(Integer.parseInt(refreshInt.getText()) * 1000);
        }
        try {
            if (topicBox.getValue().equals("/p/ - Recent Photo")) {
                ThreadManager.threadFactory("p");
            } else if (topicBox.getValue().equals("/2ch/ - Webm Thread")) {
                ThreadManager.threadFactory("2webm");
            } else if (topicBox.getValue().equals("/wsg/ - ylyl")) {
                ThreadManager.threadFactory("ylyl");
            } else if (topicBox.getValue().equals("/wg/ - Desktops")) {
                ThreadManager.threadFactory("desktop");
            } else if (topicBox.getValue().equals("/wg/ - Homescreens")) {
                ThreadManager.threadFactory("homescreens");
            } else if (topicBox.getValue().equals("/p/ - Film photography")) {
                ThreadManager.threadFactory("film");
            } else if (topicBox.getValue().equals("/g/ - Battlestations")) {
                ThreadManager.threadFactory("battlestations");
            } else if (topicBox.getValue().equals("/wsg/ - Terrible music")) {
                ThreadManager.threadFactory("music");
            } else if (topicBox.getValue().equals("/fa/ - waywt")) {
                ThreadManager.threadFactory("waywt");
            } else if (topicBox.getValue().equals("/wg/ - Space")) {
                ThreadManager.threadFactory("space");
            }
        } catch (NullPointerException e) {
            System.out.println("[WARNING] Pick a topic!");
        }
    }

    /**
     * Meetod alustab Checkboxis valitud teemat protsessina streamimis režiimis
     * Funktsionaalsus antakse Stream nupule
     *
     * @param event Mouseclick
     */
    public void streamTo(ActionEvent event) {
        ThreadManager.setStartWithWebm(false);
        ThreadManager.setStartStream(true);
        try {
            if (topicBox.getValue().equals("/2ch/ - Webm Thread")) {
                ThreadManager.threadFactory("2webm");
            } else if (topicBox.getValue().equals("/wsg/ - ylyl")) {
                ThreadManager.threadFactory("ylyl");
            } else if (topicBox.getValue().equals("/wsg/ - Terrible music")) {
                ThreadManager.threadFactory("music");
            } else {
                System.out.println("[ERROR] Streaming isnt supported on this topic");
                ThreadManager.setStartStream(false);
            }
            ThreadManager.setStartStream(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop nupu funktsionaalsus mis alustab protsesside sulgemist
     *
     * @param event Mouseclcik
     */
    public void killAll(ActionEvent event) {
        try {
            ThreadManager.killAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Seab loodava protsessi allalaadima ka webm laiendusega faile
     *
     * @param event Mouseclick
     */
    public void setWEBM(ActionEvent event) {
        if (ThreadManager.isStartWithWebm()) {
            ThreadManager.setStartWithWebm(false);
        } else {
            ThreadManager.setStartWithWebm(true);
        }
    }

    /**
     * Suunab kõik stdout teated TextArea osasse GUI-s
     *
     * @author Taras (Stackoverflow)
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Arrays.sort(SettingsHandler.items);
        topicBox.getItems().addAll(SettingsHandler.items);
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    /**
     * Kirjutab teksti TextArea-sse
     * @param text String mida kirjutada
     */
    public void appendText(String text) {
        Platform.runLater(() -> stdout.appendText(text));
    }

    @Override
    public void write(int arg0) throws IOException {
    }
}