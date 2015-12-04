package core;

import gui.Controller;
import hashing.RamFS;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOExceptionWithCause;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import hashing.Hasher;
import threads.ThreadManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * Abstraktne klass, mis koosneb universaalsetest funktsioonidest, et leida failid antud lehelt
 * ning edasi need kas allalaadida või striimida mingit tüüpi meediapleierisse nagu mpv.
 * Superklassiks on Thread, mis võimaldab teostada mitmelõimelist programmi jooksutamist.
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public abstract class ImgBoard extends Thread {

    String board;
    RamFS memfs;
    int percentage;
    boolean streaming = false;
    boolean interrupted;
    
   /**
     * Konstruktor, määrab otsitava teema ja paneb seadistused paika
     * @param board otsitud teema
     */
    public ImgBoard(String board) {
        this.board = board;
        conf.setValues(board);
    }

    SettingsHandler conf = new SettingsHandler();

    /**
     * Meetod loob dünaamilise masiivi kuhu lisatakse antud lehelt soovitud failid
     * Meetod rakendab selleks Topic klassis olevaid meetoteid, mis otsivad üles vajaliku teema
     * @return failidele viitavad lingid dünaamilises masiivis
     */
    ArrayList<String> getLinks() {
        ArrayList<String> elemnList = new ArrayList<>();
        Document doc = null;
        try {
            if(conf.getImgBoard().equals("4ch")) {
                doc = Jsoup.connect(conf.getUrl() + locate4ch()).maxBodySize(0).timeout(5000000).get();
            } else if (conf.getImgBoard().equals("2ch")) {
                doc = Jsoup.connect(locate2ch()).maxBodySize(0).timeout(5000000).get();
            }
                Elements links = doc.select(webmCheck());
            for (Element x : links) {
                elemnList.add(conf.getPrefix() + x.attr("href")); // leida HTMList <a href=> lingid
            }
        } catch (NullPointerException | HttpStatusException | IllegalArgumentException ex) { // Leidja suutmatuse korral alustada protsessi uuesti
            try {
                System.out.println("[ERROR] Thread doesn't exist, trying again");
                Thread.sleep(5000);
                if(streaming) {
                    streamWebm(getLinks());
                } else {
                    dlPictures(getLinks());
                }
            } catch (InterruptedException i) {
                interrupted = true;
                System.out.println("[ERROR] Link retrieval has been interrupted");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elemnList;
    }

    /**
     * Meetod teostab striimimist programmi mpv
     * @param linkList linkidest koosnev masiiv, mida kasutatakse mpv-sse linkide söötmiseks
     */
    void streamWebm(ArrayList<String> linkList) {
        String links = "";
        try {
            if(!linkList.isEmpty()) {
                for (int i = linkList.size() - 1; i > 0; i--) {
                    links += linkList.get(i) + " ";
                }
                System.out.println("[INFO] Streaming " + linkList.size() + " webms");
                String processName = "mpv " + links + " "; // koostab käsu stringina
                CommandLine cmdProcess = CommandLine.parse(processName); // Protsessi string cmd-st käivitamiseks
                DefaultExecutor executor = new DefaultExecutor(); // Apache Exec libist käivitaja
                try {
                    executor.execute(cmdProcess); // käivitada cmd-st protsess
                } catch (IOException e) {
                    System.out.println("[ERROR] mpv not found in $PATH");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Meetod teostab failide allalaadimist ning nende salvestamist kõvakettale juhul,
     * kui sarnase hashiga faili pole varem allalaaditud. Algselt laetakse failid mällu
     * ning siis salvestatakse kõvakettale.
     * @param linkList masiiv, mis koosneb linkidest failidele
     */
    void dlPictures(ArrayList<String> linkList) {
        try {
            for (int i = 0; i < linkList.size(); i++) {
                if (i == 0) {
                    System.out.println("[INFO] Downloading " + linkList.size() + " pics/vids from " + conf.getBoard());
                }
                percentage = (i * 100 / linkList.size());
                System.out.printf("[INFO] %d %% %s DLing: %s%n", percentage, Thread.currentThread().getName(), linkList.get(i));
                memfs = new RamFS();
                URL website = new URL(linkList.get(i));
                String url = linkList.get(i);
                memfs.storeInMemory(website);
                if (memfs.getFileLocation() != null) { // Kui on olemas fail siis alustab räsi genereerimist
                    Hasher hasherFunc = new Hasher(memfs.getFileLocation(), conf.getHashType()); // anname räsigeneraatorile faili asukoha mälus ja räsitüübi
                    if (!hasherFunc.checkHashes()) {
                        Path completeDest = Paths.get(conf.getFileDestination() + FilenameUtils.getBaseName(url) + "." + FilenameUtils.getExtension(url));
                        if (!Files.exists(completeDest.getParent())) { // kui faili ei ekisteeri genereeri fail
                            Files.createDirectory(completeDest.getParent()); // kui kausta ei eksisteeri siis uus kaust
                        }
                        Files.copy(memfs.getFileLocation(), completeDest, StandardCopyOption.REPLACE_EXISTING); // kopeerida mälust fail kettale
                        hasherFunc.saveHash();
                        Files.deleteIfExists(Paths.get("./temp/tempfile" + Thread.currentThread().getId())); // mällu salvestamise vea korral kustutada temp fail
                    }
                }
                memfs.flushFS();
                if (i == linkList.size() - 1) {
                    System.out.printf("[SUCCESS] 100%% DLing Files are located in %s", conf.getAbsolutePath());
                }
            }
        } catch (ClosedByInterruptException e) {
            System.out.println("[ERROR] Downloading interrupted");
        } catch (HttpStatusException e) {
            System.out.println("[ERROR] HTTP status error");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                memfs.flushFS();
            } catch (NullPointerException e) {
                if (!interrupted && !Controller.killThreads) {
                    System.out.println("[ERROR] No desired filetypes to download, try including webm");
                    Thread.currentThread().interrupt();
                    ThreadManager.runningTopics.remove(this);
                }
            }
        }
    }

    abstract public void run();

    abstract String webmCheck();
    /**
     * Tagastab otsitava teema nime
     */
    public String getBoard() {
        return board;
    }

    abstract String locate4ch();

    abstract String locate2ch();


}
