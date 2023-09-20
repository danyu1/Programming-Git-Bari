
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BlobTest {

    String[] fileNames = { "testFile1.txt", "testFile2.txt" };
    String[] fileContents = { "testFile1", "test dsanfodnfdsn" };

    static String pathToObjectsFolder = ".\\objects";

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        PrintWriter pw1 = new PrintWriter(
                ".\\testFile1.txt");

        pw1.print("some content in file 1");

        pw1.close();
    }

    // delete all the added files in setupBeforeClass ()
    @AfterAll
    static void tearDownAfterClass() throws Exception {

        Path textPath1 = Paths
                .get(".\\testFile1.txt");
        if (Files.exists(textPath1)) {
            Files.delete(textPath1);
        }
    }

    @Test
    void testFileContents() throws Exception {
        Blob blob = new Blob("testFile1.txt");

        assertEquals("some content in file 1", blob.fileContents());

    }

    @Test
    void testGetFileName() {
        Blob blob = new Blob("testFile1.txt");

        assertEquals("testFile1.txt", blob.getFileName());
    }

    @Test
    void testGetSha1() throws Exception {

        String expectedHash = "2e27b4d29c63a1242ee02973f5862cf26cf9679f";

        Blob blob = new Blob("testFile1.txt");

        assertEquals(expectedHash, blob.getSha1(blob.fileContents()));
    }

    @Test
    void testMakeFile() throws Exception {

        Blob blob = new Blob("testFile1.txt");
        blob.makeFile();

        Path path1 = Paths.get(pathToObjectsFolder, blob.getSha1(blob.fileContents()));

        assertTrue(Files.exists(path1));

        // test if contents same
        String str = "";
        BufferedReader br = new BufferedReader(new FileReader(path1.toString()));

        while (br.ready()) {
            str += (char) br.read();
        }

        br.close();

        assertEquals("some content in file 1", str);

    }

}
