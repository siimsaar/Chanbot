package threads;

import core.Topic;
import gui.Gui;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ThreadManager {

    static boolean startStream = false;
    static boolean startWebmOnly = false;
    static boolean startWithWebm = false;

    public static List<Topic> runningTopics= new ArrayList<>();

    public static void main(String[] args) {
        Gui.initGui(args);
    }
    public static void threadFactory(String board) {
        if(checkRunning(board)) {
            return;
        }
        runningTopics.add(new Topic(board));
        if (startStream) {
            lastEntry().enableStreaming();
            lastEntry().onlyWEBM();
            startStream = false;
            lastEntry().setName("STREAM");
        }
        if (startWebmOnly) {
            lastEntry().onlyWEBM();
        }
        if (startWithWebm) {
            lastEntry().includeWEBM();
        }
        lastEntry().start();
    }

    public static boolean checkRunning(String board) {
        for(Topic i: runningTopics) {
            if (i.getBoard().equals(board) && !i.getName().equals("STREAM")) {
                System.out.printf("%n[ERROR] Topic %s is already being watched", board);
                return true;
            }
        }
        return false;
    }

    public static void killAll(){
        try {
            for(Topic i: runningTopics) {
                i.interrupt();
                runningTopics.remove(i);
        }
        } catch (Exception e) {
            System.err.println("RIP Threads");
        }
    }

    public static String getOS() {
            if (System.getProperty("os.name").toLowerCase().equals("windows")) {
                return "win";
    } else {
                return "unix";
            }
    }

    public static boolean isStartStream() {
        return startStream;
    }

    public static boolean isStartWebmOnly() {
        return startWebmOnly;
    }

    public static boolean isStartWithWebm() {
        return startWithWebm;
    }

    public static List<Topic> getRunningTopics() {
        return runningTopics;
    }

    private static Topic lastEntry() {
        return runningTopics.get(runningTopics.size() -1);
    }

    public static void setStartStream(boolean startStream) {
        ThreadManager.startStream = startStream;
    }

    public static void setStartWebmOnly(boolean startWebmOnly) {
        ThreadManager.startWebmOnly = startWebmOnly;
    }

    public static void setStartWithWebm(boolean startWithWebm) {
        ThreadManager.startWithWebm = startWithWebm;
    }

    public int valueOfRunningThreads() {
        return runningTopics.size();
    }
}
