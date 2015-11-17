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

public class Topic extends ImgBoard {

    int threadNum;
    boolean dlWEBM = false;

    public Topic(String board) {
        super(board);
    }

    @Override
    public String locate4ch() {
        String dataString = null;
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.openJSON(new File(settings.apiDestination));
            JNode thread = userAgent.json.findEvery(settings.getPattern()).findEvery("no").get(0);
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
        try {
            Document doc = Jsoup.connect(settings.getUrl()).maxBodySize(0).timeout(0).get();
            Element link = doc.select(settings.getPattern()).not(settings.getNotPattern()).first();
            String threadID = link.toString().substring(0, settings.getTopicLength()).replaceAll("[^0-9]", "");
            System.out.println(threadID);
            if(threadID.isEmpty()) {
                System.out.println("empty, retrying");
                locate2ch();
            }
            return (settings.getUrl() + "res/" + threadID + ".html");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkIfRunning() {
        if(ThreadManager.runningTopics.contains(board)) {
            System.out.printf("%n[ERROR] Topic is already being watched]%n");
            Thread.currentThread().interrupt();
        }
    }

    String webmCheck() {;
        if (dlWEBM) {
            return "a[href~=(?i)\\.(png|jpe?g|webm)]:not(a[class])";
        } else {
            return "a[href~=(?i)\\.(png|jpe?g)]:not(a[class])";
        }
    }

    @Override
    public void run() {
        try {
            checkIfRunning();
            while (!Thread.interrupted()) {
                ThreadManager.runningTopics.add(board);
                System.out.println("[INFO] Starting " + Thread.currentThread().getName() + " Updating every: " + settings.getUpdateInt() / 1000 + "s");
                setThreadNum();
                if (settings.isJsonApi()) {
                    dlJSON();
                }
                dlPictures(getLinks());
                Thread.sleep(settings.getUpdateInt());
            }
            ThreadManager.runningTopics.remove(board);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setThreadNum() {
        threadNum++;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setWEBM() {
        dlWEBM = true;
    }

}
