import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public abstract class FourChan extends Thread {

    final static String[] SUP_BOARDS = {"hr", "mu", "wpg", "pol", "g"};
    final static String API_URL = "http://a.4cdn.org/";
    final static String API_EXTENSION = "/catalog.json";

    String fileDestination;
    String apiDestination;
    String board;

    public FourChan(String fileDestination, String apiDestination, String board) {
        this.fileDestination = fileDestination;
        this.apiDestination = apiDestination;
        this.board = board;
    }

    String locBoard() {
        String boardLink = null;
        for (int i = 0; i < SUP_BOARDS.length; i++) {
            if (SUP_BOARDS[i].equals(board)) {
                boardLink = API_URL + board + API_EXTENSION;
            }
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

    abstract String locThread();

    ArrayList<String> getLinks() {
        ArrayList<String> elemnlist = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://boards.4chan.org/" + board + "/thread/" + locThread()).get();
            Elements links = doc.select("a[href~=(?i)\\.(png|jpe?g)]:not(a[class])");
            for (Element x : links) {
                elemnlist.add("http:" + x.attr("href"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return elemnlist;
    }

    void dlPictures(ArrayList<String> x) {
        int precentage;
        try {
            for (int i = 0; i < x.size(); i++) {
                if (i == 0) {
                    System.out.println("Downloading " + x.size() + " pics/vids");
                }
                precentage = (i * 100 / x.size());
                System.out.println(precentage + "% DLing: " + x.get(i));
                URL website = new URL(x.get(i));
                String convertUrl = x.get(i).toString();
                File destination = new File(fileDestination + convertUrl.substring(21, convertUrl.length()));
                FileUtils.copyURLToFile(website, destination);
                if (i == x.size() - 1) {
                    System.out.println("100% DLing DONE");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    abstract public void run();


}
