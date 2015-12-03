package core;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Klass määrab erinevad seadmed programmi kasutamiseks
 */
public class SettingsHandler {
    // global
    public static String[] items = {"/wsg/ - ylyl", "/p/ - Recent Photo", "/wg/ - Desktops", "/g/ - Battlestations", "/wg/ - Homescreens", "/p/ - Film photography", "/wg/ - Space", "/wsg/ - Terrible music", "/fa/ - waywt"};
    public static String customDestination;
    public static String fileDestination;
    public static String hashDestination = Paths.get(".").toAbsolutePath().normalize().toString() + "hashes.txt";
    public static int updateInt;
    // local
    private String boardn;
    private int maxJsonPages = 10;
    private String pattern; // REGEX
    private String url; // URL koos boardi nimega
    private String prefix = "http:";
    private String notPattern = ":contains(null)";
    private String imgBoard;
    private String apiUrl;
    private String hashType = "SHA1";
    private int maxPages = 6;
    private boolean jsonApi = false;
    private int topicLength = 50;

    /**
     * Meetod määrab muutujad vastavalt otsitavale teemale
     * @param boardName Otsitav teema
     */
    void setValues(String boardName) {
        switch (boardName) {
            case "p":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)recent photo thread)}";
                boardn = "p";
                break;
            case "ylyl":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)ylyl)}";
                boardn = "wsg";
                break;
            case "desktop":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(desktop)(.*))}";
                boardn = "wg";
                break;
            case "battlestations":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(battlestation)(.*))}";
                boardn = "g";
                break;
            case "homescreens":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(homescreen)(.*))}";
                boardn = "wg";
                break;
            case "music":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(music)(.*))}";
                boardn = "wsg";
                break;
            case "waywt":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(waywt)(.*))}";
                boardn = "fa";
                break;
            case "space":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(space)(.*))}";
                boardn = "wg";
                break;
            case "film":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)(.*)(film)(.*))}";
                boardn = "p";
                break;
            default:
                System.out.println("[ERROR] Invalid board ("+boardName+")");
                System.exit(-1);
        }
        if(customDestination == null || customDestination.isEmpty()) {
            fileDestination = "./" + boardName + "/";
        } else {
            fileDestination = customDestination;
        }
        if(imgBoard.equals("4ch")) {
            url = String.format("http://boards.4chan.org/%s/thread/", boardn);
            apiUrl = String.format("http://a.4cdn.org/%s/", boardn);
            jsonApi = true;
        } else {
            url = "https://2ch.hk/" + boardn;
            prefix = "https://2ch.hk/";
        }
    }

    /**
     * Regex
     * @return Tagastab regexi
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Board
     * @return Tagastab otsitava teema
     */
    public String getBoard() {
        return boardn;
    }

    /**
     * Url koos boardiga
     * @return tagastab lehe kust otsitakse
     */
    public String getUrl() {
        return url;
    }

    /**
     * Filter
     * @return tagastab väljafiltreerimise regexi
     */
    public String getNotPattern() {
        return notPattern;
    }

    /**
     * JSON
     * @return Tagastab kas on tegemist JSON apiga või mitte
     */
    public boolean isJsonApi() {
        return jsonApi;
    }

    /**
     * Reapikkus teemale
     * @return Tagastab teema nime maksimaalse pikkuse
     */
    public int getTopicLength() {
        return topicLength;
    }

    /**
     * Kui mitu lehte on võimalik läbi minna
     * @return lehtede arv
     */
    public int getMaxJsonPages() {
        return maxJsonPages;
    }

    /**
     * Alafoorum ehk board
     * @return Tagastab otsitava lehe boardi
     */
    public String getImgBoard() {
        return  imgBoard;
    }

    /**
     * 2ch jaoks on vaja veel anda meetodile URL ilma boardita
     * @return Tagastab lehe lehe ilma täpsema boardita
     */
    public String getPrefix() {
        return  prefix;
    }

    /**
     * Koht failisüsteemis kuhu kõik failid salvestatakse
     * @return Tagastab faili salvestamise asukoha
     */
    public String getFileDestination() {
        return fileDestination;
    }

    /**
     * Annab täpse koha kus failid asuvad
     * @return absolute path
     */
    public String getAbsolutePath() {
        return Paths.get(fileDestination).normalize().toAbsolutePath().toString();

    }

    /**
     * Väljastab konfigureeritud räsitüübi
     * @return räsitüüp
     */
    public String getHashType() {
        return hashType;
    }


    public String getHashDestination() {
        return hashDestination;
    }

    /**
     *
     * @return Tagastab lehe JSON API adressi
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     *
     * @return Tagastab teema allalaadimise sageuse
     */
    public int getUpdateInt() {
        return updateInt;
    }

    /**
     *
     * @return Tagastab arvu mitmelt lehelt otsitakse
     */
    public int getMaxPages() {
        return maxPages;
    }

    /**
     *
     * @param value Seab teema uuesti allalaadimise sageduse
     */
    static public void setUpdateInt(int value) {
        updateInt = value;
    }
}