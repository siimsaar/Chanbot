package hashing;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Klass teostab räside genereerimist failidele
 *
 * @version 1.0 25 Nov 2015
 * @author Siim Saar
 * @since 1.7
 */
public class Hasher {

    final String newLine = System.getProperty("line.separator");

    Path filePath;
    String hashType;

    /**
     * Konstruktor võtab sisse faili asukoha ja soovitava räsitüübi
     * @param filePath Fail millele genereeritakse räsi
     * @param hashType Genereeritava räsi tüüp
     */
    public Hasher(Path filePath, String hashType) {
        this.filePath = filePath;
        this.hashType = hashType;
        if (!Files.exists(Paths.get("hashes.txt"))) {
            try {
                Files.createFile(Paths.get("hashes.txt"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Genereerib failile MD5 tüüpi räsi
     * @return MD5 räsi
     */
    private String generateMD5() {
        String genHash = null;
        try {
            genHash = DigestUtils.md5Hex(Files.newInputStream(filePath));
        } catch (Exception e) {
            System.out.println(e);
        }
        return genHash;
    }

    /**
     * Genereerib failile SHA1 tüüpi räsi
     * @return SHA1 räsi
     */
    private String generateSHA1() {
        String genHash = null;
        try {
            genHash = DigestUtils.sha1Hex(Files.newInputStream(filePath));
        } catch (Exception e) {
            System.out.println(e);
        }
        return genHash;
    }

    /**
     * Tagastab soovitud tüüpi räsi
     * @return genereeritud räsi
     */
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

    /**
     * Salvestab räsi faili
     */
    public void saveHash() {
        try {
            String data = "hashes.txt";
            File file = new File(data);
            FileUtils.writeStringToFile(file, getHash() + newLine, true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Kontrollib kas sama räsiga fail juba on allalaaditud
     * @return Kas räsi eksisteerib või mitte
     */
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

