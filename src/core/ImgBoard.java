package core;

import hashing.TempFS;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public abstract class ImgBoard extends Thread {

    final static String[] SUPPORTED_BOARDS = {"hr", "mu", "wg", "pol", "g", "p", "2webm", "kpg"};

    String board;
    TempFS memfs;
    int precentage;

    public ImgBoard(String board) {
        this.board = board;
        conf.setValues(board);
    }

    protected SettingsHandler conf = new SettingsHandler();


    String locBoard() {
        String boardLink = null;
        for (int i = 0; i < SUPPORTED_BOARDS.length; i++) {
            if (SUPPORTED_BOARDS[i].equals(board)) {
                boardLink = conf.getApiUrl();
            }
        }
        if (boardLink.equals(null)) {
            System.err.println("[ERROR] You shouldn't have this error");
        }
        return boardLink;
    }

    void dlJSON() {
        try {
            File dest = new File(conf.getApiDestination());
            URL JSON_API = new URL(locBoard());
            FileUtils.copyURLToFile(JSON_API, dest);
        } catch (SocketTimeoutException e) {
            System.out.println("[ERROR] Site is down or you're not connected");
        } catch (Exception e) {
            System.out.println(locBoard());
        }
    }

    ArrayList<String> getLinks() {
        ArrayList<String> elemnlist = new ArrayList<>();
        Document doc = null;
        try {
            if(conf.getImgBoard().equals("4ch")) {
                doc = Jsoup.connect(conf.getUrl() + locate4ch()).get();
            } else if (conf.getImgBoard().equals("2ch")) {
                doc = Jsoup.connect(locate2ch()).get();
            }
            //Document doc = Jsoup.connect(locate4ch()).get();
            Elements links = doc.select(webmCheck());
            for (Element x : links) {
                elemnlist.add(conf.getPrefix() + x.attr("href"));
            }
        } catch (NullPointerException | HttpStatusException | IllegalArgumentException ex) {
            try {
                System.out.println("[ERROR] Thread doesn't exist, trying again");
                Thread.sleep(5000);
                dlPictures(getLinks());
            } catch (InterruptedException i) {
                System.out.println("[CRITICAL] Nothing works anymore");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elemnlist;
    }


    void dlPictures(ArrayList<String> x) {
        try {
            for (int i = 0; i < x.size(); i++) {
                if (i == 0) {
                    System.out.println("[INFO] Downloading " + x.size() + " pics/vids from " + board);
                }
                precentage = (i * 100 / x.size());
                System.out.printf("[INFO] %d %% %s DLing: %s%n", precentage, Thread.currentThread().getName(), x.get(i));
                memfs = new TempFS(Paths.get(conf.getFileDestination()));
                URL website = new URL(x.get(i));
                String url = x.get(i);
                memfs.storeInMemory(website);
                Hasher hasherfunc = new Hasher(memfs.getFileLocation(), "SHA1");
                if(!hasherfunc.checkHashes()) {
                    Path completeDest = Paths.get(conf.getFileDestination() + url.substring(conf.getExtensionCutoff(), url.length()));
                    if(!Files.exists(completeDest.getParent())) {
                        Files.createDirectory(completeDest.getParent());
                    }
                    Files.copy(memfs.getFileLocation(), completeDest, StandardCopyOption.REPLACE_EXISTING);
                    hasherfunc.saveHash();
                    Files.deleteIfExists(Paths.get("./temp/tempfile" + Thread.currentThread().getId()));
                }
                memfs.flushFS();
                if (i == x.size() - 1) {
                    System.out.printf("[SUCCESS] 100%% DLing Files are located in %s", conf.getFileDestination());
                }
            }
        } catch (HttpStatusException e) {
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            memfs.flushFS();
        }
    }

    abstract public void run();

    abstract String webmCheck();

    abstract String locate4ch();

    abstract String locate2ch();


}
