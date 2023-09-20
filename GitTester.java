import java.security.*;
import java.util.*;
import java.io.*;

public class GitTester {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        Index index = new Index();

        index.init();

        Blob a = new Blob("balls.txt");
        System.out.println();
        System.out.println(a.getSha1(a.fileContents()));
        System.out.println();
        a.makeFile();

        index.add("balls2.txt");
        index.add("balls.txt");
        index.add("balls3.txt");

        index.printBlobs();
        index.remove("balls2.txt");

        System.out.println("test done");
    }
}
