package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopicRegex {

    private String pickedtopic;
    private String boardn;
    private String pattern;
    private String url;
    private String prefix = "http:";
    private String notPattern;
    private String imgBoard;
    private boolean jsonApi = false;
    private int topicLength;
    private int extensionCutoff;

    public void setValues(String boardName) {
        switch (boardName) {
            case "kpg":
                jsonApi = true;
                imgBoard = "4ch";
                pattern = "{sub: ((?i)KPOP GENERAL|kpop|kpopg|kpg)}";
                url = "http://boards.4chan.org/mu/thread/";
                extensionCutoff = 21;
                boardn = "mu";
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
                topicLength = 50;
                extensionCutoff = 32;
                break;
        }
    }

    public String getPattern() {
        return pattern;
    }

    public String getBoard() {
        return boardn;
    }

    public String getPickedtopic() {
        return pickedtopic;
    }

    public String getUrl() {
        return url;
    }

    public String getNotPattern() {
        return notPattern;
    }

    public boolean isJsonApi() {
        return jsonApi;
    }

    public int getTopicLength() {
        return topicLength;
    }

    public int getExtensionCutoff() {
        return extensionCutoff;
    }
    public String getImgBoard() {
        return  imgBoard;
    }
    public String getPrefix() {
        return  prefix;
    }
}