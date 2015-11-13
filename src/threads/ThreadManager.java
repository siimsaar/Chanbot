package threads;

import core.Topic;

/**
 * Created by ayy on 11/11/15.
 */
public class ThreadManager {
    public static void main(String[] args) {
        Topic kek = new Topic("mu", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogMu.json", "mu");
        kek.start();
        Topic kuk = new Topic("p", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogP.json", "p");
        kuk.start();
        Topic kik = new Topic("wg", "/home/ayy/dlthing/", "/home/ayy/dlthing/catalogwg.json", "wg");
        kik.start();
    }
}
