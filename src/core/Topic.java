package core;

import com.jaunt.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.omg.PortableInterceptor.SUCCESSFUL;
import threads.ThreadManager;

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
        while (jsonCatalog <= conf.getMaxJsonPages()) { // töötab niikaua kuni on seadetes max lehtede arv ettenähtud
            try {
                UserAgent jsonReader = new UserAgent(); // pseudobrowser JSONi jaoks
                jsonReader.settings.maxBytes = -1;
                jsonReader.settings.readTimeout = 600000;
                jsonReader.sendGET(conf.getApiUrl() + jsonCatalog + ".json"); // GET päring JSON failile
                JNode thread = jsonReader.json.findEvery(conf.getPattern()).findEvery("no").get(0); // kasutab regexit JSON failis
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
        while (tryCount < conf.getMaxPages()) { // töötab niikaua kuni on seadetes max lehtede arv ettenähtud
            try {
                Document htmlFile = Jsoup.connect(conf.getUrl() + page()).maxBodySize(0).timeout(500000).get(); // tõmbab HTML faili
                Element htmlLinks = htmlFile.select(conf.getPattern()).not(conf.getNotPattern()).first(); // kasutab regexit HTML faili peal
                String threadID = htmlLinks.toString().substring(0, conf.getTopicLength()).replaceAll("[^0-9]", ""); // ebavajalik välja
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
    String page() {
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
            return "a[href~=(?i)\\.(png|jpe?g)]:not(a[class])"; // leia JPG ja PNG lingid
        } else if (dlWEBM.equals("pluswebm")) {
            return "a[href~=(?i)\\.(png|jpe?g|webm)]:not(a[class])"; // leia PNG, JPG ja WEBM lingid
        } else if (dlWEBM.equals("webm")) {
            return "a[href~=(?i)\\.(webm)]:not(a[class])"; // leia ainult WEBM lingid
        }
        return null;
    }

    /**
     * Meetod käivitab protsessi, mis rakendab kõiki vajalikke meetodeid
     * programmi jooksutamiseks ja vajaliku tulemuse saamiseks
     */
    @Override
    public void run() {
        if(streaming) { // kui streamimine on soovitud siis käivitada thread streamina
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
            System.out.println("[INFO] Starting streaming from " + conf.getBoard());
            setThreadNum();
            streamWebm(getLinks());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] Streaming init failed");
        }
        System.out.println("[SUCCESS] Stream finished");
        ThreadManager.runningTopics.remove(this);
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
