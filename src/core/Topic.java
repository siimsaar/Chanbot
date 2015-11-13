package core;

import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;

import java.io.File;

public class Topic extends ImgBoard {

    String topicRgx;
    int threadNum;
    boolean dlWEBM = false;

    public Topic(String board, String fileDestination, String apiDestination, String topicRgx) {
        super(fileDestination, apiDestination, board);
        this.topicRgx = topicRgx;

    }

    @Override
    public String locThread() {
        TopicRegex pattern = new TopicRegex(topicRgx);
        String dataString = null;
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.openJSON(new File(super.apiDestination));
            JNode thread = userAgent.json.findEvery(pattern.getPattern()).findEvery("no").get(0);
            dataString = thread.toString();
            if (dataString.equals("[]\n")) {
                System.out.println("Couldnt find the thread");
            }
        } catch (NotFound e) {
            Thread.interrupted();
        } catch (Exception e) {
            System.err.println(e);
        }
        return dataString;
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
        System.out.println("Starting " + Thread.currentThread().getName());
        setThreadNum();
        dlJSON();
        dlPictures(getLinks());
    }

    void setThreadNum() {
        threadNum++;
    }

    public int getThreadNum() {
        return threadNum;
    }

    void setWEBM() {
        dlWEBM = true;
    }

}
