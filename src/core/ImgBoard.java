package core;

import hashing.ramFilesys;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import hashing.Hasher;
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
    ramFilesys memfs;
    int precentage;
    boolean streaming = false;

    public ImgBoard(String board) {
        this.board = board;
        conf.setValues(board);
    }

    protected SettingsHandler conf = new SettingsHandler();

    /**
     * Kontroll, kas JSONi link on konfiguratsioonis üldse olemas
     * @return link JSON failile
     */
    String locBoard() {
        String boardLink = conf.getApiUrl();
        if (boardLink == null) {
            System.out.println("[ERROR] Board isn't JSON enabled");
        }
        return boardLink;
    }

    /**
     * Meetod võtab lingi ja tõmbab alla JSON faili
     */
    void dlJSON() {
        try {
            File dest = new File(conf.getApiDestination());
            URL JSON_API = new URL(locBoard());
            FileUtils.copyURLToFile(JSON_API, dest);
        } catch (SocketTimeoutException e) {
            System.out.println("[ERROR] Site is down or you're not connected");
        } catch (Exception e) {
            System.out.println("[ERROR] Unidentified error");
            System.out.println(locBoard());
            e.printStackTrace();
        }
    }

    /**
     * Meetod loob dünaamilise masiivi kuhu lisatakse antud lehelt soovitud failid
     * Meetod rakendab selleks Topic klassis olevaid meetoteid, mis otsivad üles vajaliku teema
     * @return failidele viitavad lingid dünaamilises masiivis
     */
    ArrayList<String> getLinks() {
        ArrayList<String> elemnlist = new ArrayList<>();
        Document doc = null;
        try {
            if(conf.getImgBoard().equals("4ch")) {
                doc = Jsoup.connect(conf.getUrl() + locate4ch()).get();
            } else if (conf.getImgBoard().equals("2ch")) {
                doc = Jsoup.connect(locate2ch()).get();
            }
                Elements links = doc.select(webmCheck());
            for (Element x : links) {
                elemnlist.add(conf.getPrefix() + x.attr("href"));
            }
        } catch (NullPointerException | HttpStatusException | IllegalArgumentException ex) {
            try {
                System.out.println("[ERROR] Thread doesn't exist, trying again");
                Thread.sleep(5000);
                if (conf.isJsonApi()) {
                    dlJSON();
                }
                if(streaming) {
                    streamWebm(getLinks());
                } else {
                    dlPictures(getLinks());
                }
            } catch (InterruptedException i) {
                System.out.println("[ERROR] Unrecoverable error");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Unidentified error");
            e.printStackTrace();
        }
        return elemnlist;
    }

    /**
     * Meetod teostab striimimist programmi mpv
     * @param linkList linkidest koosnev masiiv, mida kasutatakse mpv-sse linkide söötmiseks
     */
    void streamWebm(ArrayList<String> linkList) {
        String links = "";
        try {
            for(int i = linkList.size() - 1; i > 0; i--) {
                links += linkList.get(i) + " ";
            }
            System.out.println("[INFO] Streaming " + linkList.size() + " webms");
            String processName = "mpv " + links + " "; // koostab käsu stringina
            CommandLine cmdProcess = CommandLine.parse(processName);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdProcess);
            //Runtime.getRuntime().exec("bash mpv " + links);
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
                    System.out.println("[INFO] Downloading " + linkList.size() + " pics/vids from " + board);
                }
                precentage = (i * 100 / linkList.size());
                System.out.printf("[INFO] %d %% %s DLing: %s%n", precentage, Thread.currentThread().getName(), linkList.get(i));
                memfs = new ramFilesys();
                URL website = new URL(linkList.get(i));
                String url = linkList.get(i);
                memfs.storeInMemory(website);
                if (memfs.getFileLocation() != null) {
                    Hasher hasherFunc = new Hasher(memfs.getFileLocation(), "SHA1");
                    if (!hasherFunc.checkHashes()) {
                        Path completeDest = Paths.get(conf.getFileDestination() + url.substring(conf.getExtensionCutoff(), url.length()));
                        if (!Files.exists(completeDest.getParent())) {
                            Files.createDirectory(completeDest.getParent());
                        }
                        Files.copy(memfs.getFileLocation(), completeDest, StandardCopyOption.REPLACE_EXISTING);
                        hasherFunc.saveHash();
                        Files.deleteIfExists(Paths.get("./temp/tempfile" + Thread.currentThread().getId()));
                    }
                }
                memfs.flushFS();
                if (i == linkList.size() - 1) {
                    System.out.printf("[SUCCESS] 100%% DLing Files are located in %s", conf.getFileDestination());
                }
            }
        } catch (ClosedByInterruptException e) {
            System.out.println("[ERROR] Downloading interrupted");
        } catch (HttpStatusException e) {
            System.out.println("[ERROR] HTTP status error");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ERROR] Unidentified error");
            e.printStackTrace();
        } finally {
            memfs.flushFS();
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
