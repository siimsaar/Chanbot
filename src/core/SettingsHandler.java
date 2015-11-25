package core;

import java.nio.file.Paths;

/**
 * Klass määrab erinevad seadmed programmi kasutamiseks
 */
public class SettingsHandler {

    public String rawFileDestination = ".";
    public String fileDestination = Paths.get(rawFileDestination).toAbsolutePath().normalize().toString();
    public String apiDestination = "./api_";
    public String hashDestination = Paths.get(".").toAbsolutePath().normalize().toString() + "hashes.txt";

    private String boardn;
    private String pattern; // REGEX
    private String url; // URL koos boardi nimega
    private String prefix = "http:";
    private String notPattern = ":contains(null)";
    private String imgBoard;
    private String apiUrl;
    private int maxPages = 5;
    private boolean jsonApi = false;
    private int topicLength = 50;
    private int extensionCutoff = 21;
    static private int updateInt;

    /**
     * Meetod määrab muutujad vastavalt otsitavale teemale
     * @param boardName Otsitav teema
     */
    public void setValues(String boardName) {
        switch (boardName) {
            case "kpg":
                jsonApi = true;
                imgBoard = "4ch";
                pattern = "{sub: ((?i)KPOP GENERAL|kpop|kpopg|kpg)}";
                url = "http://boards.4chan.org/mu/thread/";
                extensionCutoff = 21;
                boardn = "mu";
                apiUrl = "http://a.4cdn.org/" + boardn + "/catalog.json";
                break;
            case "p":
                pattern = "{sub: RECENT PHOTO THREAD}";
                url = "http://boards.4chan.org/p/";
                break;
            case "wg":
                pattern = "{sub: (black wallpapers)}";
                break;
            case "2webm":
                imgBoard = "2ch";
                pattern = "blockquote:matches((?i)webm)";
                notPattern = ":contains(Анимублядский WebM-тред)";
                url = "https://2ch.hk/b/";
                boardn = "b";
                prefix = "https://2ch.hk/";
                extensionCutoff = 32;
                maxPages = 6;
                break;
            case "rukpg":
                imgBoard = "2ch";
                pattern = "blockquote:matches((?i)Вечноживой)";
                url = "https://2ch.hk/kpop/";
                boardn = "kpop";
                prefix = "https://2ch.hk/";
                extensionCutoff = 28;
                maxPages = 6;
                break;
            default:
                System.out.println("[ERROR] Invalid board");
                Thread.currentThread().interrupt();
        }
        fileDestination = fileDestination + "/" + boardName + "/";
        apiDestination = apiDestination + getBoard();
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