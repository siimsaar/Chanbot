import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

public class Hashing {

    String filePath;
    String listPath;
    String hashType;

    public Hashing(String filePath, String listPath, String hashType) {
        this.filePath = filePath;
        //this.listPath = listPath;
        //this.hashType = hashType;
    }

    String generateMD5() {
        String genHash = null;
        try {
            genHash = DigestUtils.md5Hex(new FileInputStream(filePath));
        } catch (Exception e) {
            System.out.println(e);
        }
        return genHash;
    }

    void saveHash(String listPat) {
        try {
            String data = "/home/ayy/dlthing/hashes.txt";
            File file = new File(data);
            FileUtils.writeStringToFile(file, generateMD5());
        } catch (Exception e) {
            System.out.println("nope");
        }
    }
}
