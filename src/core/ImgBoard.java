package core;

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
import java.util.ArrayList;

public abstract class ImgBoard extends Thread {

    final static String[] SUPPORTED_BOARDS = {"hr", "mu", "wg", "pol", "g", "p", "2webm", "kpg"};

    String board;

    public ImgBoard(String board) {
        this.board = board;
        settings.setValues(board);
    }

    protected TopicRegex settings = new TopicRegex();


    String locBoard() {
        String boardLink = null;
        for (int i = 0; i < SUPPORTED_BOARDS.length; i++) {
            if (SUPPORTED_BOARDS[i].equals(board)) {
                boardLink = settings.getApiUrl();
            }
        }
        if (boardLink.equals(null)) {
            System.err.println("[ERROR] You shouldn't have this error");
        }
        return boardLink;
    }

    void dlJSON() {
        try {
            File dest = new File(settings.getApiDestination());
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
            if(settings.getImgBoard().equals("4ch")) {
                doc = Jsoup.connect(settings.getUrl() + locate4ch()).get();
            } else if (settings.getImgBoard().equals("2ch")) {
                doc = Jsoup.connect(locate2ch()).get();
            }
            //Document doc = Jsoup.connect(locate4ch()).get();
            Elements links = doc.select(webmCheck());
            for (Element x : links) {
                elemnlist.add(settings.getPrefix() + x.attr("href"));
            }
        } catch (HttpStatusException x) {
            System.out.println("[ERROR] " + Thread.currentThread().getName() + " Failed to locate thread");
            x.printStackTrace();
            Thread.interrupted();
        } catch (NullPointerException e) {
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
        int precentage;
        try {
            for (int i = 0; i < x.size(); i++) {
                if (i == 0) {
                    System.out.println("[INFO] Downloading " + x.size() + " pics/vids from " + board);
                }
                precentage = (i * 100 / x.size());
                System.out.printf("[INFO] %d %% %s DLing: %s%n", precentage, Thread.currentThread().getName(), x.get(i));
                //System.out.println(precentage + "% DLing: " + x.get(i));
                URL website = new URL(x.get(i));
                String convertUrl = x.get(i).toString();
                String completeDest = settings.getFileDestination() + convertUrl.substring(settings.getExtensionCutoff(), convertUrl.length());
                String tempDest = settings.getFileDestination() + "Temp" + convertUrl.substring(settings.getExtensionCutoff(), convertUrl.length());
                File destination = new File(tempDest);
                FileUtils.copyURLToFile(website, destination);
                Hasher hasherfunc = new Hasher(destination.toString(), "SHA1");
                if(hasherfunc.checkHashes()) {
                    FileUtils.deleteQuietly(destination);
                } else {
                    hasherfunc.saveHash();
                    File fdestination = new File (completeDest);
                    FileUtils.copyFile(destination,fdestination);
                    FileUtils.deleteQuietly(destination);
                }
                if (i == x.size() - 1) {
                    System.out.printf("[SUCCESS] 100%% DLing Files are located in %s", settings.getFileDestination());
                }
            }
        } catch (HttpStatusException e) {
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    abstract public void run();

    abstract String webmCheck();

    abstract String locate4ch();

    abstract String locate2ch();


}
