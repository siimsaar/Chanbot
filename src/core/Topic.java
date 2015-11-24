package core;

import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import threads.ThreadManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Topic extends ImgBoard {

    int threadNum;
    int tryCount = 0;
    String dlWEBM = "default";

    public Topic(String board) {
        super(board);
    }

    @Override
    public String locate4ch() {
        String dataString = null;
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.openJSON(new File(conf.apiDestination));
            JNode thread = userAgent.json.findEvery(conf.getPattern()).findEvery("no").get(0);
            dataString = thread.toString();
            if (dataString.equals("[]\n")) {
                System.out.println("[ERROR] Couldnt find the thread");
            }
        } catch (NotFound e) {
            Thread.interrupted();
        } catch (Exception e) {
            System.err.println(e);
        }
        return dataString;
    }
    public String locate2ch() {
        tryCount = 0;
        while(tryCount < conf.getMaxPages()) {
            try {
                Document doc = Jsoup.connect(conf.getUrl() + pageIterator()).maxBodySize(0).timeout(0).get();
                Element link = doc.select(conf.getPattern()).not(conf.getNotPattern()).first();
                String threadID = link.toString().substring(0, conf.getTopicLength()).replaceAll("[^0-9]", "");
                return (conf.getUrl() + "res/" + threadID + ".html");
            } catch (NullPointerException | IOException er) {
                System.out.println(pageIterator());
                tryCount++;
            }
        }
        return null;
    }

    String pageIterator() {
        if (tryCount == 0) {
            return "";
        } else {
            return tryCount + ".html";
        }
    }

    String webmCheck() {;
        if (dlWEBM.equals("default")) {
            return "a[href~=(?i)\\.(png|jpe?g)]:not(a[class])";
        } else if (dlWEBM.equals("pluswebm")) {
            return "a[href~=(?i)\\.(png|jpe?g|webm)]:not(a[class])";
        } else if (dlWEBM.equals("webm")) {
            return "a[href~=(?i)\\.(webm)]:not(a[class])";
        }
        return null;
    }

    @Override
    public void run() {
        if(streaming) {
        stream();
            Thread.currentThread().interrupt();
        }
        try {
            while (!Thread.interrupted()) {
                System.out.println("[INFO] Starting " + Thread.currentThread().getName().toLowerCase() + " updating every: " + conf.getUpdateInt() / 1000 + "s");
                setThreadNum();
                if (conf.isJsonApi()) {
                    dlJSON();
                }
                dlPictures(getLinks());
               Thread.sleep(conf.getUpdateInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stream() {
        try {
            System.out.println("[INFO] Starting streaming from " + board);
            setThreadNum();
            if (conf.isJsonApi()) {
                dlJSON();
            }
            streamWebm(getLinks());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Streaming init failed");
        }
    }

    void setThreadNum() {
        threadNum++;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void onlyWEBM() {
        dlWEBM = "webm";
    }

    public void includeWEBM() {
        dlWEBM = "pluswebm";
    }

    public void enableStreaming() { streaming = true; }

}
