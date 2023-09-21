
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IndexTest { // tests largely reused from GitTest

    static Index index = new Index();

    static String[] expectedContents = { "some content in file 1", "some content in file 2", "some content in file 3" };
    static String[] expectedSha = { "2e27b4d29c63a1242ee02973f5862cf26cf9679f",
            "d98d670ea7ca145dee0266961b8bf8ee5b12925a", "0a9d1240f29014f6677816388f4763e7fdc41445" };

    static String pathToIndexFolder = ".\\index.txt";
    static String pathToObjectsFolder = ".\\objects";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        try {
            // Display the current working directory
            System.out.println("Current working directory: " + System.getProperty("user.dir"));

            // initialize index
            index = new Index();
            index.init();

            // create three test files in the workspace with content
            PrintWriter pw1 = new PrintWriter(
                    ".\\testFile1.txt");
            PrintWriter pw2 = new PrintWriter(
                    ".\\testFile2.txt");
            PrintWriter pw3 = new PrintWriter(
                    ".\\testFile3.txt");

            pw1.print("some content in file 1");
            pw2.print("some content in file 2");
            pw3.print("some content in file 3");

            pw1.close();
            pw2.close();
            pw3.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    // just sees if the files from init exist
    static void testInit() throws Exception {
        // check if index & Objects exist

        File indexFile = new File("index.txt");
        indexFile.createNewFile();
        Path objectsPath = Paths.get("./objects");

        assertTrue(indexFile.exists());
        assertTrue(Files.exists(objectsPath));
    }

    @Test
    void testAdd() throws Exception {
        // create the 3 blobs for testing
        index.add("testFile1.txt");
        index.add("testFile2.txt");
        index.add("testFile3.txt");

        Path indexPath = Paths
                .get(pathToIndexFolder);
        String indexContents = "";
        StringBuilder sb = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(pathToIndexFolder));
        while (br.ready()) {
            sb.append(br.readLine());
        }
        br.close();
        indexContents = sb.toString();
        for (int i = 0; i < 2; i++) {
            System.out.println(indexContents);
            assertTrue(indexContents.contains(expectedSha[i]));
        }
    }

    @Test
    void testPrintBlobs() throws Exception {
        try {
            // create the 3 blobs for testing
            index.add("testFile1.txt");
            index.add("testFile2.txt");
            index.add("testFile3.txt");
            // remove the second and last file
            index.remove("testFile2.txt");
            index.remove("testFile3.txt");
        } catch (Exception e) {
            System.out.println("An error ocurred: " + e.getMessage());
        }
        Path path1 = Paths.get(pathToObjectsFolder, expectedSha[1]);
        Path path2 = Paths.get(pathToObjectsFolder, expectedSha[2]);
        Path pathToActualFile2 = Paths
                .get(".\\testFile2.txt");
        Path pathToActualFile3 = Paths
                .get(".\\testFile3.txt");
        Path indexPath = Paths.get(pathToIndexFolder);

        // test if the file still exists in objects folder
        assertTrue(Files.exists(path1));
        assertTrue(Files.exists(path2));
        // test if the file still exists in the workspace
        assertTrue(Files.exists(pathToActualFile2));
        assertTrue(Files.exists(pathToActualFile3));
        String indexContents = readFile(indexPath.toString(), StandardCharsets.UTF_8);
        // test if the index file no longer contains the file
        assertTrue(!indexContents.contains(expectedSha[1]));
        assertTrue(!indexContents.contains(expectedSha[2]));
    }

    @Test
    void testRemove() throws Exception {
        try {
            // create the 3 blobs for testing
            index.add("testFile1.txt");
            index.add("testFile2.txt");
            index.add("testFile3.txt");
            // remove the second and last file
            index.remove("testFile2.txt");
            index.remove("testFile3.txt");
        } catch (Exception e) {
            System.out.println("An error ocurred: " + e.getMessage());
        }
        Path path1 = Paths.get(pathToObjectsFolder, expectedSha[1]);
        Path path2 = Paths.get(pathToObjectsFolder, expectedSha[2]);
        Path pathToActualFile2 = Paths
                .get(".\\testFile2.txt");
        Path pathToActualFile3 = Paths
                .get(".\\testFile3.txt");
        Path indexPath = Paths.get(pathToIndexFolder);

        // test if the file still exists in objects folder
        assertTrue(Files.exists(path1));
        assertTrue(Files.exists(path2));
        // test if the file still exists in the workspace
        assertTrue(Files.exists(pathToActualFile2));
        assertTrue(Files.exists(pathToActualFile3));
        String indexContents = readFile(indexPath.toString(), StandardCharsets.UTF_8);
        // test if the index file no longer contains the file
        assertTrue(!indexContents.contains(expectedSha[1]));
        assertTrue(!indexContents.contains(expectedSha[2]));
    }

    // techiedelight.com
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
