package hashing;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;

/**
 * Klass tekitab virtuaalse failisüsteemi mällu, mida kasutatakse
 * räsi genereerimiseks ja kontrollimiseks enne kõvakettale salvestamist
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.8
 */
public class ramFilesys {

    Path tempPath;
    FileSystem fs;
    static boolean maxExceeded;

    /**
     * Tekitab mällu ajutise failisüsteemi
     */
    public ramFilesys() {
        try {
            fs = MemoryFileSystemBuilder.newLinux().build("tempfs" + Thread.currentThread().getId());
        } catch (Exception e) {
            System.out.println("[ERROR] Couldn't create RAM filesystem");
            e.printStackTrace();
        }
    }

    /**
     * Salvestab faili mällu ja vea korral loob ajutise faili kõvakettale
     * @param dlUrl faili asukoht
     */
    public void storeInMemory(URL dlUrl){
        try {
            tempPath = fs.getPath("tempfile");
            InputStream dlPic = dlUrl.openStream();
            Files.copy(dlPic, tempPath, StandardCopyOption.REPLACE_EXISTING);
            dlPic.close();
        } catch (FileNotFoundException e) {
            System.err.println("[INFO] Deleted file detected at" + dlUrl);
            tempPath = null;
        } catch (AssertionError e) {
            if(!Files.isDirectory(Paths.get("./temp"))) {
                try {
                    Files.createDirectory(Paths.get("./temp/"));
                } catch (Exception x) {
                    System.err.println("[ERROR] Unrecoverable error at" + dlUrl);
                    x.printStackTrace();
                }
            }
            try {
                if(!Files.exists(Paths.get("./temp/tempfile" + Thread.currentThread().getId()))) {
                    tempPath = Files.createFile(Paths.get("./temp/tempfile" + Thread.currentThread().getId()));
                }
                tempPath = Paths.get("./temp/tempfile" + Thread.currentThread().getId());
                InputStream dlPic = dlUrl.openStream();
                Files.copy(dlPic, tempPath, StandardCopyOption.REPLACE_EXISTING);
                dlPic.close();
                maxExceeded = true;
            } catch (Exception j) {
                System.err.println("[ERROR] Unrecoverable error at" + dlUrl);
                j.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Unrecoverable error at" + dlUrl);
            e.printStackTrace();
        }
    }

    /**
     * Väljastab faili asukoha mälus
     * @return Faili asukoht mälus
     */
    public Path getFileLocation() {
        return tempPath;
    }

    public static boolean getMaxExceededStatus () {
        return maxExceeded;
    }

    /**
     * Puhastab mälu failidest ja sulgeb failisüsteemi
     */
    public void flushFS() {
        try {
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
