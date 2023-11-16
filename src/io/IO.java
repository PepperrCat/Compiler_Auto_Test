package io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class IO {
    public static String read(String fileName) throws IOException {
        InputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(fileName)));
        Scanner scanner = new Scanner(inputStream);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine() + '\n');
        }
        scanner.close();
        inputStream.close();
        return sb.toString();
    }
    public static void write(String fileName, String fileData) throws IOException {
//        System.out.println(fileData);
        if (fileName == null)
            fileName = "output.txt";
        File outputFile = new File(fileName);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile, true);
        fileOutputStream.write(fileData.getBytes());
        fileOutputStream.close();
    }
}
