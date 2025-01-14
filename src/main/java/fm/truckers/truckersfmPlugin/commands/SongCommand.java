package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import fm.truckers.truckersfmPlugin.helpers.ErrorLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class SongCommand extends AbstractCommand {
    @Override
    public void register(TruckersfmPlugin plugin) {
        new CommandAPICommand("song")
                .withAliases("current-song", "now-playing")
                .withFullDescription("The current song playing on TruckersFM.")
                .executesPlayer(this::handleCommand)
                .register();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest currentSongRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://radiocloud.pro/api/public/v1/song/current"))
                .GET()
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = client.send(currentSongRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    player.sendMessage(Component.text("Failed to fetch current song info. HTTP Status: " + response.statusCode(), NamedTextColor.RED));
                    return;
                }

                String responseBody = response.body();
                String artist = com.google.gson.JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .get("artist")
                        .getAsString();

                String title = com.google.gson.JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .get("title")
                        .getAsString();

                String link = com.google.gson.JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .get("link")
                        .isJsonNull() ? null : com.google.gson.JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .get("link")
                        .getAsString();

                int playCount = com.google.gson.JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .get("playcount")
                        .getAsInt();

                TextComponent songText = Component.text("Now Playing: ", NamedTextColor.GOLD)
                        .append(link != null
                                ? Component.text(artist + " - " + title, NamedTextColor.GREEN, TextDecoration.UNDERLINED)
                                .clickEvent(ClickEvent.openUrl(link))
                                : Component.text(artist + " - " + title, NamedTextColor.GREEN))
                        .append(Component.newline())
                        .append(Component.text("Play Count: ", NamedTextColor.AQUA))
                        .append(Component.text(String.valueOf(playCount), NamedTextColor.WHITE));

                player.sendMessage(songText);
            } catch (Exception e) {
                ErrorLogger.log(plugin, player, e);
            }
        });
    }
}
