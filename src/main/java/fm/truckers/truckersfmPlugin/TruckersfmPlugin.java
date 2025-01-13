package fm.truckers.truckersfmPlugin;

import dev.jorel.commandapi.CommandAPICommand;
import fm.truckers.truckersfmPlugin.enums.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class TruckersfmPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        new CommandAPICommand("presenter")
                .withFullDescription("The current live presenter on TruckersFM, and the next upcoming presenter.")
                .executesPlayer((player, args) -> {
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest livePresenterRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://radiocloud.pro/api/public/v1/presenter/live"))
                            .GET()
                            .build();

                    HttpRequest upcomingPresenterRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://radiocloud.pro/api/public/v1/presenter/upcoming"))
                            .GET()
                            .build();

                    CompletableFuture.runAsync(() -> {
                        try {
                            // Fetch live presenter
                            HttpResponse<String> liveResponse = client.send(livePresenterRequest, HttpResponse.BodyHandlers.ofString());
                            if (liveResponse.statusCode() != 200) {
                                player.sendMessage(Component.text("Failed to fetch live presenter info. HTTP Status: " + liveResponse.statusCode()));
                                return;
                            }

                            String liveResponseBody = liveResponse.body();
                            String livePresenterName = com.google.gson.JsonParser.parseString(liveResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .getAsJsonObject("user")
                                    .get("name")
                                    .getAsString();

                            String livePresenterDescription = com.google.gson.JsonParser.parseString(liveResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .get("description")
                                    .getAsString();

                            long livePresenterEnd = com.google.gson.JsonParser.parseString(liveResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .get("end")
                                    .getAsLong();

                            TextComponent livePresenterText = Component.text("Currently live: ")
                                    .append(Component.text(livePresenterName).decoration(TextDecoration.BOLD, true))
                                    .append(Component.text(" - "))
                                    .append(
                                            Component.text(livePresenterDescription)
                                                    .decoration(TextDecoration.ITALIC, true)
                                    )
                                    .append(Component.text(" until " + TimeConverter.convertTimestampToTime(livePresenterEnd)));

                            player.sendMessage(livePresenterText);

                            // Fetch upcoming presenter
                            HttpResponse<String> upcomingResponse = client.send(upcomingPresenterRequest, HttpResponse.BodyHandlers.ofString());
                            if (upcomingResponse.statusCode() != 200) {
                                player.sendMessage(Component.text("Failed to fetch upcoming presenter info. HTTP Status: " + upcomingResponse.statusCode()));
                                return;
                            }

                            String upcomingResponseBody = upcomingResponse.body();
                            String upcomingPresenterName = com.google.gson.JsonParser.parseString(upcomingResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .getAsJsonObject("user")
                                    .get("name")
                                    .getAsString();

                            String upcomingPresenterDescription = com.google.gson.JsonParser.parseString(upcomingResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .get("description")
                                    .getAsString();

                            long upcomingPresenterEnd = com.google.gson.JsonParser.parseString(upcomingResponseBody)
                                    .getAsJsonObject()
                                    .getAsJsonObject("data")
                                    .get("start")
                                    .getAsLong();

                            TextComponent upcomingPresenterText = Component.text("Next up: ")
                                    .append(Component.text(upcomingPresenterName).decoration(TextDecoration.BOLD, true))
                                    .append(Component.text(" - "))
                                    .append(
                                            Component.text(upcomingPresenterDescription)
                                                    .decoration(TextDecoration.ITALIC, true)
                                    )
                                    .append(Component.text(" at " + TimeConverter.convertTimestampToTime(upcomingPresenterEnd)));

                            player.sendMessage(upcomingPresenterText);
                        } catch (Exception e) {
                            player.sendMessage(Component.text("An error occurred: " + e.getMessage()));
                        }
                    });
                })
                .register();

        new CommandAPICommand("song")
                .withFullDescription("The current song playing on TruckersFM.")
                .executesPlayer((player, args) -> {
                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest currentSongRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://radiocloud.pro/api/public/v1/song/current"))
                            .GET()
                            .build();

                    CompletableFuture.runAsync(() -> {
                        try {
                            HttpResponse<String> response = client.send(currentSongRequest, HttpResponse.BodyHandlers.ofString());
                            if (response.statusCode() != 200) {
                                player.sendMessage(Component.text("Failed to fetch current song info. HTTP Status: " + response.statusCode()));
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

                            TextComponent songText = Component.text("Now Playing: ")
                                    .append(link != null
                                            ? Component.text(artist + " - " + title).clickEvent(ClickEvent.openUrl(link)).decoration(TextDecoration.UNDERLINED, true)
                                            : Component.text(artist + " - " + title))
                                    .append(Component.newline())
                                    .append(Component.text("Play Count: " + playCount));

                            player.sendMessage(songText);
                        } catch (Exception e) {
                            player.sendMessage(Component.text("An error occurred: " + e.getMessage()));
                        }
                    });
                })
                .register();

        new CommandAPICommand("request")
                .withFullDescription("Request a song to be played on TruckersFM. Does not work with AutoDJ.")
                .withArguments(new dev.jorel.commandapi.arguments.GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    String message = (String) args.get("message");

                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://radiocloud.pro/api/public/v1/messages"))
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    "name=" + player.getName() +
                                            "&message=" + message.replace(" ", "%20") +
                                            "&message_type=" + MessageType.Request +
                                            "&identifier=" + player.getUniqueId() +
                                            "&platform=Minecraft plugin"
                            ))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    CompletableFuture.runAsync(() -> {
                        try {
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            if (response.statusCode() != 200) {
                                player.sendMessage(Component.text("Failed to send request. HTTP Status: " + response.statusCode()));
                                return;
                            }

                            player.sendMessage(Component.text("Your request has been sent successfully!"));
                        } catch (Exception e) {
                            player.sendMessage(Component.text("An error occurred: " + e.getMessage()));
                        }
                    });
                })
                .register();

        new CommandAPICommand("shoutout")
                .withFullDescription("Send in a shout-out to TruckersFM")
                .withArguments(new dev.jorel.commandapi.arguments.GreedyStringArgument("message"))
                .executesPlayer((player, args) -> {
                    String message = (String) args.get("message");

                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://radiocloud.pro/api/public/v1/messages"))
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    "name=" + player.getName() +
                                            "&message=" + message.replace(" ", "%20") +
                                            "&message_type=" + MessageType.Shoutout +
                                            "&identifier=" + player.getUniqueId() +
                                            "&platform=Minecraft plugin"
                            ))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    CompletableFuture.runAsync(() -> {
                        try {
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            if (response.statusCode() != 200) {
                                player.sendMessage(Component.text("Failed to send request. HTTP Status: " + response.statusCode()));
                                return;
                            }

                            player.sendMessage(Component.text("Your request has been sent successfully!"));
                        } catch (Exception e) {
                            player.sendMessage(Component.text("An error occurred: " + e.getMessage()));
                        }
                    });
                })
                .register();
    }
}
