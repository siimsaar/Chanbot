package core;

import org.apache.commons.io.FileUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import hashing.Hasher;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public abstract class ImgBoard extends Thread {

    final static String[] SUPPORTED_BOARDS = {"hr", "mu", "wg", "pol", "g", "p"};
    final static String[] JSON_BOARDS = {"4chan", "infinitychan"};
    final static String[] BOARDS = {"krautchan", "2ch", "lainchan", "420chan"};
    final static String API_URL = "http://a.4cdn.org/";
    final static String API_EXTENSION = "/catalog.json";

    String fileDestination;
    String apiDestination;
    String board;

    public ImgBoard(String fileDestination, String apiDestination, String board) {
        this.fileDestination = fileDestination;
        this.apiDestination = apiDestination;
        this.board = board;
    }

    String locBoard() {
        String boardLink = null;
        for (int i = 0; i < SUPPORTED_BOARDS.length; i++) {
            if (SUPPORTED_BOARDS[i].equals(board)) {
                boardLink = API_URL + board + API_EXTENSION;
            }
        }
        if (boardLink.equals(null)) {
            System.err.println("no");
        }
        return boardLink;
    }

    void dlJSON() {
        try {
            File dest = new File(apiDestination);
            URL JSON_API = new URL(locBoard());
            FileUtils.copyURLToFile(JSON_API, dest);
        } catch (Exception e) {
            System.out.println(locBoard());
        }
    }

    ArrayList<String> getLinks() {
        ArrayList<String> elemnlist = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://boards.4chan.org/" + board + "/thread/" + locThread()).get();
            Elements links = doc.select(webmCheck());
            for (Element x : links) {
                elemnlist.add("http:" + x.attr("href"));
            }
        } catch (HttpStatusException x) {
            System.out.println(Thread.currentThread().getName() + " Failed to locate thread");
            Thread.interrupted();
        } catch (Exception e) {
            System.out.println(e);
        }
        return elemnlist;
    }


    void dlPictures(ArrayList<String> x) {
        int precentage;
        ArrayList<String> precentageArray;
        try {
            for (int i = 0; i < x.size(); i++) {
                if (i == 0) {
                    System.out.println("Downloading " + x.size() + " pics/vids from " + board);
                }
                precentage = (i * 100 / x.size());
                System.out.printf("\r%d %% %s DLing: %s", precentage, Thread.currentThread().getName(), x.get(i));
                //System.out.println(precentage + "% DLing: " + x.get(i));
                URL website = new URL(x.get(i));
                String convertUrl = x.get(i).toString();
                String completeDest = fileDestination + convertUrl.substring(21, convertUrl.length());
                File destination = new File(completeDest);
                FileUtils.copyURLToFile(website, destination);
                Hasher hasherfunc = new Hasher(completeDest,null,null);
                hasherfunc.saveHash(hasherfunc.generateMD5());
                if (i == x.size() - 1) {
                    System.out.printf("\r100%% DLing Files are located in %s", fileDestination);
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

    abstract String locThread();

    abstract int getThreadNum();


}
