package threads;

import core.Topic;
import gui.Gui;
import java.util.TreeSet;

public class ThreadManager {

    public static TreeSet<String> runningTopics = new TreeSet<>();

    public static void main(String[] args) {
        Gui.initGui(args);
        //Topic kek = new Topic("2webm", "./topics/", "./topics/kpg/catalogMu.json");
        //kek.setWEBM();
        //kek.start();
        //TODO
    }
    public static void threadFactory(String topic) {
    //TODO
    }
    public int valueOfRunningThreads() {
        return runningTopics.size();
    }
}
