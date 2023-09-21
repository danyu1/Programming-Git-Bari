import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Index {

    File tree;

    HashMap<String, String> blobs = new HashMap();

    public Index() {

    }

    public void init() throws FileNotFoundException // credit from stackoverflow.com
    {
        File objects = new File("objects");
        if (!objects.exists()) {
            objects.mkdirs();
        }

        PrintWriter pw = new PrintWriter(
                ".\\index.txt");

        String words = "";
        pw.print(words);

        pw.close();

    }

    public void add(String name) throws NoSuchAlgorithmException, IOException {
        Blob blob = new Blob(name);
        blob.makeFile();

        blobs.put(blob.getFileName(), blob.getSha1(blob.fileContents()));
        printBlobs();
    }

    public void remove(String fileName) {
        blobs.remove(fileName);
        printBlobs();

    }

    public void printBlobs() {
        try {
            PrintWriter pw = new PrintWriter(
                    ".\\index.txt");

            String s = "";
            for (HashMap.Entry<String, String> entry : blobs.entrySet()) {
                s += entry.getKey() + " : " + entry.getValue() + "\n";
            }

            pw.print(s);
            pw.close();

        } catch (Exception e) {

        }

    }
}
