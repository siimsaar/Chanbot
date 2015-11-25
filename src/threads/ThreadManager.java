package threads;

import core.Topic;
import gui.Controller;
import gui.Gui;

import java.util.ArrayList;
import java.util.List;

/**
 * Klassi ülesanne on alustada ja lõpetada protsesside tööd
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public class ThreadManager {

    static boolean startStream = false;
    static boolean startWebmOnly = false;
    static boolean startWithWebm = false;
    //Protsessidest koosnev kollektsioon
    public static List<Topic> runningTopics= new ArrayList<>();

    public static void main(String[] args) {
        Gui.initGui(args);
    }

    /**
     * Loob protsessi vajalike paremeetridega
     * @param board Sisendiks võtab otsitava lehe
     */
    public static void threadFactory(String board) {
        if(checkRunning(board) && !startStream) {
            System.out.printf("%n[ERROR] Topic %s is already being watched", board);
            return;
        }
        runningTopics.add(new Topic(board));
        if (startStream) {
            lastEntry().enableStreaming();
            lastEntry().onlyWEBM();
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

    /**
     * Kontrollib kas sisendit juba otsitakse mingi protsessi poolt
     * @param board Otsitav teema
     * @return Kas protsess eksisteerib või ei
     */
    public static boolean checkRunning(String board) {
        for(Topic i: runningTopics) {
            if (i.getBoard().equals(board)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tapab kõik jooksvad protsessid
     */
    public static void killAll(){
        try {
            for(int i=0; i < runningTopics.size(); i++) {
                runningTopics.get(i).interrupt();
                runningTopics.remove(i);
                killAll();
        }
        } catch (Exception e) {
            System.err.println("Killing threads");
            }
        }

    /**
     * Kontrollib ja tagastab OSi millel programm jookseb
     * @return kasutatav OS
     */
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

    /**
     * Tagastab väärtuse, kas alustakase protsessi webm tõmbamisega
     * @return True or False
     */
    public static boolean isStartWithWebm() {
        return startWithWebm;
    }

    public static List<Topic> getRunningTopics() {
        return runningTopics;
    }

    /**
     * Viimane element runningTopics masiivis
     * @return Viimane element masiivis
     */
    private static Topic lastEntry() {
        return runningTopics.get(runningTopics.size() -1);
    }

    /**
     * Seab programmi alustama protsessi streamina
     * @param startStream True
     */
    public static void setStartStream(boolean startStream) {
        ThreadManager.startStream = startStream;
    }

    public static void setStartWebmOnly(boolean startWebmOnly) {
        ThreadManager.startWebmOnly = startWebmOnly;
    }

    /**
     * Paneb alustatava protsessi tõmbama ka webm faile
     * @param startWithWebm
     */
    public static void setStartWithWebm(boolean startWithWebm) {
        ThreadManager.startWithWebm = startWithWebm;
    }

    public int valueOfRunningThreads() {
        return runningTopics.size();
    }
}
