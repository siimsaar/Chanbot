package core;

/**
 * Created by ayy on 11/11/15.
 */
public class TopicRegex {

    String board;
    String pattern;

    TopicRegex(String board) {
        this.board = board;
    }

    private String regexPattern(String srcBoard) {
        switch(srcBoard) {
            case "mu":
                pattern = "{sub: (Kpop general|KPOP GENERAL|kpop|kpopg|kpg)}";
                break;
            case "p":
                pattern = "{sub: RECENT PHOTO THREAD}";
                break;
            case "wg":
                pattern = "{sub: (black wallpapers)}";
        }
        return pattern;
    }
   public String getPattern() {
       return regexPattern(board);
   }


}