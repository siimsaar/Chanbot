package core;

import com.jaunt.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.omg.PortableInterceptor.SUCCESSFUL;

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

    private int threadNum; // protsessi number
    private int tryCount = 0; // proovide kord HTML puhul
    private int jsonCatalog; // jsoni proovide puhul
    private String dlWEBM = "default"; // otsitav meedium

    /**
     * Konstruktor annab teema nime edasi superklassile
     *
     * @param board Parameetriks on otsitav teema
     */
    public Topic(String board) {
        super(board);
    }

    /**
     * Meetod otsib ülesse soovitud teema lehelt võttes vajalikud seadmed SettingsHandler
     * klassist. Antud meetod teostab teema otsimist kasutades regexit JSON failis.
     *
     * @return Otsitud teema
     */
    @Override
    String locate4ch() {
        String dataString;
        jsonCatalog = 1;
        while (jsonCatalog <= conf.getMaxJsonPages()) {
            try {
                UserAgent jsonReader = new UserAgent();
                jsonReader.settings.maxBytes = -1;
                jsonReader.settings.readTimeout = 600000;
                jsonReader.sendGET(conf.getApiUrl() + jsonIterator());
                JNode thread = jsonReader.json.findEvery(conf.getPattern()).findEvery("no").get(0);
                dataString = thread.toString();
                if(!dataString.isEmpty()) {
                    return dataString;
                }
                jsonCatalog++;
            } catch (JauntException e) {
                jsonCatalog++;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return null;
    }

    /**
     * Meetod otsib ülesse soovitud teema lehelt võttes vajalikud seadmed SettingsHandler
     * klassist. Antud meetod teostab otsimist HTML dokumendist regexi abil.
     *
     * @return Otsitud teema
     */
    @Override
    String locate2ch() {
        tryCount = 0;
        while (tryCount < conf.getMaxPages()) {
            try {
                Document doc = Jsoup.connect(conf.getUrl() + pageIterator()).maxBodySize(0).timeout(500000).get();
                Element link = doc.select(conf.getPattern()).not(conf.getNotPattern()).first();
                String threadID = link.toString().substring(0, conf.getTopicLength()).replaceAll("[^0-9]", "");
                if(!threadID.isEmpty()) {
                    return (conf.getUrl() + "/res/" + threadID + ".html");
                }
                tryCount++;
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

    String jsonIterator() {
            return jsonCatalog + ".json";
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
            streamWebm(getLinks());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Streaming init failed");
        }
        System.out.println("[SUCCESS] Stream finished");
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
