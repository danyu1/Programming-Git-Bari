import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Commit {
    boolean previousCommit = false;
    String previousCommitSha = "";

    public Commit(String summary, String author) throws NoSuchAlgorithmException, IOException {
        String date = getDate();
        String toSha = summary + author + date;
        String lol = getSha1(toSha);
        writeLine(lol, makeTree());
        if (previousCommit)
            writeLine(lol, previousCommitSha);
        else {
            writeLine(lol, "null");
            previousCommitSha = lol;
            previousCommit = true;
        }

        // idk what an ext commit is
        writeLine(lol, "null");

        writeLine(lol, "author");
        writeLine(lol, date);
    }

    public String makeTree() throws NoSuchAlgorithmException {
        Tree tre = new Tree();
        return tre.generateSHA1();
    }

    // FROM STACK
    public static String getDate() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public String getSha1(String input) throws NoSuchAlgorithmException { // credit to
                                                                          // http://www.sha1-online.com/sha1-java/
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static void writeLine(String filename, String content) throws IOException {
        File file = new File(filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            writer.write(content);
        }
    }
}
