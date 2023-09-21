import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GitTest {
    public static Index index;

    static String[] expectedContents = { "some content in file 1", "some content in file 2", "some content in file 3" };
    static String[] expectedSha = { "2e27b4d29c63a1242ee02973f5862cf26cf9679f",
            "d98d670ea7ca145dee0266961b8bf8ee5b12925a", "0a9d1240f29014f6677816388f4763e7fdc41445" };

    static String pathToObjectsFolder = ".\\objects";

    static String pathToIndexFolder = ".\\index.txt";

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

    // delete all the added files in setupBeforeClass ()
    @AfterAll
    static void tearDownAfterClass() throws Exception {
        Path tree1 = Paths.get(
                ".\\objects\\10f228098914b028963a208273e41be47b4f417d");
        Path tree2 = Paths.get(
                ".\\objects\\6016cd7c79df2958d3bc74b3dee21c7fe994e592");

        Path textPath1 = Paths
                .get(".\\testFile1.txt");
        Path textPath2 = Paths
                .get(".\\testFile2.txt");
        Path textPath3 = Paths
                .get(".\\testFile3.txt");
        Path objectsPath = Paths.get(pathToObjectsFolder);
        if (Files.exists(textPath1)) {
            Files.delete(textPath1);
            Files.delete(textPath2);
            Files.delete(textPath3);
            // path to each file in the objects folder
            Path p1 = Paths.get(pathToObjectsFolder + "\\" + expectedSha[0]);
            Path p2 = Paths.get(pathToObjectsFolder + "\\" + expectedSha[1]);
            Path p3 = Paths.get(pathToObjectsFolder + "\\" + expectedSha[2]);
            Files.delete(p1);
            Files.delete(p2);
            Files.delete(p3);
        }
        if (Files.exists(tree1)) {
            Files.delete(tree1);
            Files.delete(tree2);
        }
        Files.delete(objectsPath);
        Path indexPath = Paths.get(pathToIndexFolder);
        Files.delete(indexPath);
    }

    @Test
    @DisplayName("[1] Test if initialize and objects are created correctly")
    void testInitialize() throws Exception {
        // check if the file index exists and the path to the objects folder exists
        File file = new File("index.txt");
        Path path = Paths.get(pathToObjectsFolder);
        assertTrue(file.exists());
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("[2] Test if creating a blob works. 3 for sha and location, 3 for file contents")
    void testCreateBlob() throws Exception {
        try {
            // create the 3 blobs for testing
            addBlobs(index);
        } catch (Exception e) {
            System.out.println("An error ocurred: " + e.getMessage());
        }

        // check the sha1 hashes and the correct location (inside the objects folder)
        Path p1 = Paths.get(pathToObjectsFolder, expectedSha[0]);
        Path p2 = Paths.get(pathToObjectsFolder, expectedSha[1]);
        Path p3 = Paths.get(pathToObjectsFolder, expectedSha[2]);
        assertTrue(Files.exists(p1));
        assertTrue(Files.exists(p2));
        assertTrue(Files.exists(p3));

        // check if the file contents are the same
        String content1 = readFile(p1.toString(), StandardCharsets.UTF_8);
        String content2 = readFile(p2.toString(), StandardCharsets.UTF_8);
        String content3 = readFile(p3.toString(), StandardCharsets.UTF_8);
        assertEquals(expectedContents[0], content1);
        assertEquals(expectedContents[1], content2);
        assertEquals(expectedContents[2], content3);
    }

    @Test
    @DisplayName("[3] Test if adding a blob updates the index file.")
    void testIndexBlob() throws Exception {
        try {
            // create the 3 blobs for testing
            addBlobs(index);
        } catch (Exception e) {
            System.out.println("An error ocurred: " + e.getMessage());
        }
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
    @DisplayName("[4] Test if blobs are properly removed after calling remove method.")
    void testRemoveBlob() throws Exception {
        try {
            // create the 3 blobs for testing
            addBlobs(index);
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
    @DisplayName("[5] Test if objects in trees are added properly.")
    void testAddTree() throws Exception {
        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        tree.save();

        StringBuilder sb = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(pathToObjectsFolder + "\\" + "10f228098914b028963a208273e41be47b4f417d"));
        while (br.ready()) {
            sb.append((char) br.read());
        }
        br.close();
        assertTrue(sb.toString().contains("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt"));
        assertTrue(sb.toString().contains("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt"));
    }

    @Test
    @DisplayName("[6] Test if objects in trees are removed properly.")
    void testRemoveTree() throws Exception {
        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        tree.remove("testFile1.txt");
        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");

        tree.remove("0a9d1240f29014f6677816388f4763e7fdc41445");

        tree.save();

        StringBuilder sb = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(pathToObjectsFolder + "\\" + "6016cd7c79df2958d3bc74b3dee21c7fe994e592"));
        while (br.ready()) {
            sb.append((char) br.read());
        }
        br.close();
        assertTrue(sb.toString().contains("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt"));
        assertTrue(!(sb.toString()).contains("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt"));
    }

    // techiedelight.com
    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    // add blobs
    public static void addBlobs(Index name) throws Exception {
        name.add("testFile1.txt");
        name.add("testFile2.txt");
        name.add("testFile3.txt");
    }
}
