package hashing;

import core.SettingsHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class Hasher {

    SettingsHandler settings = new SettingsHandler();

    final String newLine = System.getProperty("line.separator");

    String filePath;
    String hashType;

    public Hasher(String filePath, String hashType) {
        this.filePath = filePath;
        this.hashType = hashType;
    }

    private String generateMD5() {
        String genHash = null;
        try {
            genHash = DigestUtils.md5Hex(new FileInputStream(filePath));
        } catch (Exception e) {
            System.out.println(e);
        }
        return genHash;
    }

    private String generateSHA1() {
        String genHash = null;
        try {
            genHash = DigestUtils.sha1Hex(new FileInputStream(filePath));
        } catch (Exception e) {
            System.out.println(e);
        }
        return genHash;
    }

    private String getHash() {
        String hash;
        switch (hashType) {
            case "MD5":
                hash = generateMD5();
                break;
            case "SHA1":
                hash = generateSHA1();
                break;
            default:
                hash = generateMD5();
                break;

        }
        return hash;
    }

    public void saveHash() {
        try {
            String data = "hashes.txt";
            File file = new File(data);
            FileUtils.writeStringToFile(file, getHash() + newLine, true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean checkHashes() {
        String currentHash;
        String comparedHash = getHash();
        try {
            Scanner reader = new Scanner(new File("hashes.txt"));
            while (reader.hasNextLine()) {
                currentHash = reader.nextLine();
                if (comparedHash.equals(currentHash)) {
                    System.out.println("[INFO] File has been previously downloaded");
                    return true;
                }
            }
        } catch (NullPointerException e) {
            System.err.print("Failed to compare file existence");
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}

