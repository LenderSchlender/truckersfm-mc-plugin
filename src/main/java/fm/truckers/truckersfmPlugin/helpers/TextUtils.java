package fm.truckers.truckersfmPlugin.helpers;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    /**
     * Splits the input text into multiple lines, each not exceeding the specified maximum length.
     *
     * @param text      The input text to split.
     * @param maxLength The maximum length of each line.
     * @return A list of strings, each representing a line.
     */
    public static List<String> splitTextIntoLines(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        while (text.length() > maxLength) {
            int splitIndex = text.lastIndexOf(' ', maxLength);
            if (splitIndex == -1) splitIndex = maxLength; // If no spaces, split at maxLength
            lines.add(text.substring(0, splitIndex));
            text = text.substring(splitIndex).trim();
        }
        if (!text.isEmpty()) {
            lines.add(text);
        }
        return lines;
    }
}
