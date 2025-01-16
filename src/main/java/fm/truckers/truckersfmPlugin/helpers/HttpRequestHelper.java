package fm.truckers.truckersfmPlugin.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import fm.truckers.truckersfmPlugin.enums.RequestResponseStatus;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpRequestHelper {
    public static void sendRequestAndHandleResponse(HttpClient client, HttpRequest request, Player player, TruckersfmPlugin plugin) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the response body
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                RequestResponseStatus status = RequestResponseStatus.valueOf(jsonResponse.get("status").getAsString().toLowerCase());
                String msg = jsonResponse.get("msg").getAsString();

                // Map status to color and send the message
                NamedTextColor color = switch (status) {
                    case success -> NamedTextColor.GREEN;
                    case error -> NamedTextColor.RED;
                };
                player.sendMessage(Component.text(msg, color));

            } catch (IllegalArgumentException e) {
                // Handle unknown status
                ErrorLogger.log(plugin, player, new Exception("Unexpected response status in the API response."));
            } catch (Exception e) {
                ErrorLogger.log(plugin, player, e);
            }
        });
    }
}
