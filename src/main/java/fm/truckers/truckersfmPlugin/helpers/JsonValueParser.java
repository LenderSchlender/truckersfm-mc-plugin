package fm.truckers.truckersfmPlugin.helpers;

public class JsonValueParser {
    public static String parse(String json, String path) {
        String[] keys = path.split("\\.");
        var element = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        for (String key : keys) {
            element = element.getAsJsonObject(key);
        }
        return element.getAsString();
    }
}
