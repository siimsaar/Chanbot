package core;

import java.nio.file.Paths;

/**
 * Klass määrab erinevad seadmed programmi kasutamiseks
 */
public class SettingsHandler {
    // global
    public static String rawFileDestination = ".";
    public static String fileDestination = Paths.get(rawFileDestination).toAbsolutePath().normalize().toString();
    public static String apiDestination = "./api_";
    public static String hashDestination = Paths.get(".").toAbsolutePath().normalize().toString() + "hashes.txt";
    public static int updateInt;
    // local
    private String boardn;
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
    private int extensionCutoff = 21;

    /**
     * Meetod määrab muutujad vastavalt otsitavale teemale
     * @param boardName Otsitav teema
     */
    void setValues(String boardName) {
        switch (boardName) {
            case "p":
                jsonApi = true;
                imgBoard = "4ch";
                pattern = "{sub: ((?i)recent photo thread)}";
                boardn = "p";
                break;
            case "ylyl":
                jsonApi = true;
                imgBoard = "4ch";
                pattern = "{sub: ((?i)ylyl)}";
                boardn = "wsg";
                break;
            case "wg":
                pattern = "{sub: (black wallpapers)}";
                break;
            case "2webm":
                imgBoard = "2ch";
                pattern = "blockquote:matches((?i)webm)";
                notPattern = ":contains(Анимублядский WebM-тред)";
                boardn = "b";
                extensionCutoff = 32;
                break;
            case "desktop":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)Desktop Thread|dpg)}";
                boardn = "g";
                break;
            case "battlestations":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)Gaming pc)}";
                boardn = "g";
                break;
            case "rukpg":
                imgBoard = "2ch";
                pattern = "blockquote:matches((?i)Вечноживой)";
                boardn = "kpop";
                extensionCutoff = 28;
                break;
            case "kpg":
                imgBoard = "4ch";
                pattern = "{sub: ((?i)KPOP GENERAL|kpop|kpopg|kpg|/kpg/ - KPOP GENERAL|korean pop general)}";
                boardn = "mu";
                break;

            default:
                System.out.println("[ERROR] Invalid board ("+boardName+")");
                System.exit(-1);
        }
        fileDestination = fileDestination + "/" + boardName + "/";
        apiDestination = apiDestination + getBoard();
        if(imgBoard.equals("4ch")) {
            url = String.format("http://boards.4chan.org/%s/thread/", boardn);
            apiUrl = String.format("http://a.4cdn.org/%s/catalog.json", boardn);
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
     *
     * @return Tagastab asukoha kust lõigatakse lehe muu osa välja
     */
    public int getExtensionCutoff() {
        return extensionCutoff;
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
     * Väljastab konfigureeritud räsitüübi
     * @return räsitüüp
     */
    public String getHashType() {
        return hashType;
    }

    /**
     *
     * @return Tagastab API faili salvestamise asukoha
     */
    public String getApiDestination() {
        return apiDestination;
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