package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static md2html.Md2HtmlTools.*;


public class Md2Html {
    public static void main(String[] args) {
        try (MdReader input = new MdReader(args[0])) {
            StringBuilder htmlText = new StringBuilder();
            while (!input.isEnd()) {
                String currentParagraph = input.readParagraph();
                htmlText.append(getTagsInParagraph(currentParagraph));
            }
            //System.out.println(htmlText);
            try (PrintWriter output = new PrintWriter(new File(args[1]), StandardCharsets.UTF_8)) {
                output.print(htmlText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
