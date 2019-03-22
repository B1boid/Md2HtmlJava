package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MdReader implements AutoCloseable {

    private static Scanner reader;

    public MdReader(final String fileName) {
        try {
            reader = new Scanner(new File(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String readParagraph() {
        StringBuilder currentParagraph = new StringBuilder();
        if (!isEnd()) {
            String currentString = reader.nextLine();
            while (!isEnd() && currentString.isEmpty()) {
                currentString = reader.nextLine();
            }
            currentParagraph.append(currentString);
            while (!isEnd()) {
                currentString = reader.nextLine();
                if (currentString.isEmpty()) {
                    break;
                }
                currentParagraph.append(System.lineSeparator());
                currentParagraph.append(currentString);
            }
        }
        return currentParagraph.toString();
    }

    protected boolean isEnd() {
        return !reader.hasNext();
    }

    @Override
    public void close() {
        reader.close();
    }
}