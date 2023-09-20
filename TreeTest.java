
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TreeTest {

    static String[] expectedContents = { "some content in file 1", "some content in file 2", "some content in file 3" };
    static String[] expectedSha = { "2e27b4d29c63a1242ee02973f5862cf26cf9679f",
            "d98d670ea7ca145dee0266961b8bf8ee5b12925a", "0a9d1240f29014f6677816388f4763e7fdc41445" };

    static String pathToObjectsFolder = ".\\objects";

    static String pathToIndexFolder = ".\\index.txt";

    @BeforeAll
    static void init() throws Exception {
        Tree tree = new Tree();
        // check if index & Objects exist

        File indexFile = new File("index.txt");
        indexFile.createNewFile();
        Path objectsPath = Paths.get("./objects");

        assertTrue(indexFile.exists());
        assertTrue(Files.exists(objectsPath));
    }

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

        // clears objects folder
        File objectFolder = new File(objectsPath.toString());
        File[] files = objectFolder.listFiles();
        for (File f : files) {
            f.delete();
        }

        Files.delete(objectsPath);
        Path indexPath = Paths.get(pathToIndexFolder);
        Files.delete(indexPath);
    }

    @Test
    void testAdd() throws Exception {
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
    void testCalculateNumberOfCommits() throws Exception {

        int expectedCommits = 2;

        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        tree.remove("testFile1.txt");

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");

        tree.save();

        assertEquals(expectedCommits, tree.calculateNumberOfCommits());

    }

    @Test
    void testRemove() throws Exception {
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

    @Test
    void testGenerateSHA1() throws Exception {

        String expectedSHA = "10f228098914b028963a208273e41be47b4f417d";
        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("blob : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        assertEquals(expectedSHA, tree.generateSHA1());

        tree.remove("testFile1.txt");

        expectedSHA = "6ed2c6ad857af63167055b642e63f9fde2eeb6e9";

        assertEquals(expectedSHA, tree.generateSHA1());

    }

    @Test
    void testReturnStringOfCommits() throws Exception {
        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("tree : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        tree.save();

        StringBuilder sb = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(pathToObjectsFolder + "\\" + tree.generateSHA1()));
        while (br.ready()) {
            sb.append((char) br.read());
        }
        br.close();
        assertEquals(sb.toString(), tree.returnStringOfCommits());
    }

    @Test
    void testSave() throws Exception {

        int expectedCommits = 2;
        Tree tree = new Tree();

        tree.add("blob : 2e27b4d29c63a1242ee02973f5862cf26cf9679f : testFile1.txt");
        tree.add("tree : 0a9d1240f29014f6677816388f4763e7fdc41445 : testFile3.txt");

        tree.save();

        assertEquals(expectedCommits, tree.calculateNumberOfCommits());

        // tried to test if the method would correctly throw an exception
        // was unable to figure out how

        // assertThrows(new Exception("Cannot save when you have not added a new tree or
        // blob"), tree.save());

    }
}
