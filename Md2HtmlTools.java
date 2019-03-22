package md2html;

import java.util.HashMap;
import java.util.Map;


public class Md2HtmlTools {
    private static Map<String, Integer> countMap;

    private static void createCountMap() {
        countMap = new HashMap<>();
        countMap.put("*", 0);
        countMap.put("_", 0);
        countMap.put("**", 0);
        countMap.put("__", 0);
        countMap.put("--", 0);
        countMap.put("`", 0);
        countMap.put("++", 0);
        countMap.put("~", 0);
    }

    private static Map<String, String> mapToHtmlOpen =
            Map.of("*", "<em>", "_", "<em>", "**", "<strong>", "__", "<strong>",
                    "--", "<s>", "`", "<code>", "++", "<u>", "~", "<mark>");
    private static Map<String, String> mapToHtmlClose =
            Map.of("*", "</em>", "_", "</em>", "**", "</strong>", "__", "</strong>",
                    "--", "</s>", "`", "</code>", "++", "</u>", "~", "</mark>");
    private static Map<String, String> singleTags =
            Map.of("&", "&amp;", "<", "&lt;", ">", "&gt;");

    private static StringBuilder htmlParagraph;
    private static boolean hadTagBefore;


    private static void findTags(String tag) {
        if (countMap.containsKey(tag)) {
            countMap.replace(tag, countMap.get(tag) - 1);
            hadTagBefore = true;
        }
    }

    private static String replaceTags(String tag) {
        if (countMap.containsKey(tag)) {
            int numberOfTags = countMap.get(tag);
            if (numberOfTags < -1) {
                if (numberOfTags % 2 != 0) {
                    ++numberOfTags;
                }
                numberOfTags = -numberOfTags;
                countMap.replace(tag, numberOfTags);
            }
            if (numberOfTags > 0) {
                hadTagBefore = true;
                countMap.replace(tag, numberOfTags - 1);
                if (numberOfTags % 2 == 0) {
                    return mapToHtmlOpen.get(tag);
                } else {
                    return mapToHtmlClose.get(tag);
                }
            }
        }
        return singleTags.getOrDefault(tag, tag);
    }

    private static String getParagraphTag(String currentParagraph) {
        int countLvl = 0;
        if (currentParagraph.charAt(countLvl) == ' ') {
            StringBuilder currentStringWithSpace = new StringBuilder(" ");
            currentParagraph = currentStringWithSpace.append(currentParagraph).toString();
        }
        while (currentParagraph.length() > countLvl && currentParagraph.charAt(countLvl) == '#') {
            ++countLvl;
        }
        if (countLvl == currentParagraph.length()) {
            return "<h" + countLvl + "></h>" + System.lineSeparator();
        }
        int lvlOfHeader = (currentParagraph.charAt(countLvl) == ' ') ? countLvl : -1;
        currentParagraph = currentParagraph.substring(lvlOfHeader + 1);
        if (lvlOfHeader > 0) {
            return "<h" + lvlOfHeader + ">" + currentParagraph + "</h" + lvlOfHeader + ">" + System.lineSeparator();
        } else {
            return "<p>" + currentParagraph + "</p>" + System.lineSeparator();
        }
    }

    private static void checkForTags(String currentParagraph, boolean firstTime) {
        int textLength = currentParagraph.length();
        for (int i = 0; i < textLength; i++) {
            hadTagBefore = false;
            String currentTag = Character.toString(currentParagraph.charAt(i));
            if (i < textLength - 1) {
                if (currentTag.equals("\\")) {
                    if (!firstTime) {
                        htmlParagraph.append(currentParagraph.charAt(i + 1));
                    }
                    i++;
                    hadTagBefore = true;
                } else {
                    String longTag = currentTag + currentParagraph.charAt(i + 1);
                    if (countMap.containsKey(longTag)) {
                        if (!firstTime) {
                            htmlParagraph.append(replaceTags(longTag));
                        } else {
                            findTags(longTag);
                        }
                        i++;
                    }
                }
            }
            if (!hadTagBefore) {
                if (!firstTime) {
                    htmlParagraph.append(replaceTags(currentTag));
                } else {
                    findTags(currentTag);
                }
            }
        }
    }

    public static String getTagsInParagraph(String currentParagraph) {
        htmlParagraph = new StringBuilder();
        createCountMap();
        checkForTags(currentParagraph, true);
        checkForTags(currentParagraph, false);
        return getParagraphTag(htmlParagraph.toString());
    }
}
