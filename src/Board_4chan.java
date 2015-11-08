import com.jaunt.JNode;
import com.jaunt.UserAgent;

import java.io.File;

/**
 * Created by ayylmao on 06.11.2015.
 */
public class Board_4chan extends FourChan {

    public Board_4chan(String board, String fileDestination, String apiDestination) {
        super(fileDestination, apiDestination, board);

    }

    @Override
    public String locThread() {
        String dataString = null;
        try {
            UserAgent userAgent = new UserAgent();
            userAgent.openJSON(new File(super.apiDestination));
            JNode thread = userAgent.json.findEvery("{sub: (kpop general|KPOP GENERAL|kpop|kpopg|kpg)}");
            dataString = thread.toString();
            if (dataString.equals("[]\n")) {
                System.out.println("Couldnt find the thread");
                Thread.sleep(10000);
            }
            dataString = dataString.substring(15, 23);
        } catch (Exception e) {
            System.err.println(e);
        }
        return dataString;
    }

    @Override
    public void run() {
        System.out.println("thread started tbh");
        dlJSON();
        dlPictures(getLinks());
    }

    public static void main(String[] args) {
        Board_4chan kek = new Board_4chan("mu", "C:\\plswork\\", "C:\\plswork\\catalog.json");
        kek.run();
    }

}
