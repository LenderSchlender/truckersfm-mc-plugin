package fm.truckers.truckersfmPlugin.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonValueParser {
    public static String parse(String json, String path) {
        String[] keys = path.split("\\.");
        JsonElement element = JsonParser.parseString(json);

        for (String key : keys) {
            if (element.isJsonObject()) {
                element = element.getAsJsonObject().get(key);
            } else {
                throw new IllegalArgumentException("Invalid JSON path: '" + path + "'. Key '" + key + "' does not point to an object.");
            }
        }

        if (element == null || element.isJsonNull()) {
            return null;
        }

        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else {
            throw new IllegalArgumentException("Expected primitive value at the end of path: '" + path + "', but found: " + element);
        }
    }
}
