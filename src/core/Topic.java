package core;

import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.IOException;

/**
 * Klass koosneb meetoditest, mis teostavad erinevate lehtedel soovitava
 * teema ülesotsimist vastavalt konfiguratsiooni seadmetele.
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public class Topic extends ImgBoard {

    int threadNum; // protsessi number
    int tryCount = 0; // proovide kord HTML puhul
    String dlWEBM = "default"; // otsitav meedium

    /**
     * Konstruktor annab teema nime edasi superklassile
     * @param board Parameetriks on otsitav teema
     */
    public Topic(String board) {
        super(board);
    }

    /**
     * Meetod otsib ülesse soovitud teema lehelt võttes vajalikud seadmed SettingsHandler
     * klassist. Antud meetod teostab teema otsimist kasutades regexit JSON failis.
     * @return Otsitud teema
     */
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

    /**
     * Meetod otsib ülesse soovitud teema lehelt võttes vajalikud seadmed SettingsHandler
     * klassist. Antud meetod teostab otsimist HTML dokumendist regexi abil.
     * @return Otsitud teema
     */
    @Override
    public String locate2ch() {
        tryCount = 0;
        while(tryCount < conf.getMaxPages()) {
            try {
                Document doc = Jsoup.connect(conf.getUrl() + pageIterator()).maxBodySize(0).timeout(5000000).get();
                Element link = doc.select(conf.getPattern()).not(conf.getNotPattern()).first();
                String threadID = link.toString().substring(0, conf.getTopicLength()).replaceAll("[^0-9]", "");
                return (conf.getUrl() + "/res/" + threadID + ".html");
            } catch (NullPointerException | IOException er) {
                tryCount++;
            }
        }
        return null;
    }

    /**
     * Kui teema otsiv meetod ei leia ülesse HTML dokumendist soovitavat teemat, siis
     * antud meetod annab ette järgmise lehe otsijameetodile.
     * @return järgmine leht kust otsida teemat
     */
    String pageIterator() {
        if (tryCount == 0) {
            return "";
        } else {
            return tryCount + ".html";
        }
    }

    /**
     * Meetod annab faili linke otsivale meetodile ette selle, mis tüüpi faile on soovitud
     * @return soovitud andmetüübid
     */
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

    /**
     * Meetod käivitab protsessi, mis rakendab kõiki vajalikke meetodeid
     * programmi jooksutamiseks ja vajaliku tulemuse saamiseks
     */
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
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " has been killed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Streamimise puhul käivitab antud meetod kõik vajalikud meetodid, et
     * teostada streamimist
     */
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

    /**
     * Lisab protsessile numbri
     */
    void setThreadNum() {
        threadNum++;
    }

    /**
     * Protsessi numbri tagastav meetod
     * @return Protsessi number
     */
    public int getThreadNum() {
        return threadNum;
    }

    /**
     * Seab programmi ainult otsima .webm faile
     */
    public void onlyWEBM() {
        dlWEBM = "webm";
    }

    /**
     * Seab programmi otsima peale jpg ja png ka webm faile
     */
    public void includeWEBM() {
        dlWEBM = "pluswebm";
    }

    /**
     * Meetod seab programmi teostama striimist
     */
    public void enableStreaming() { streaming = true; }

}
