package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.enums.MessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class RequestCommand extends AbstractCommand {
    public void register() {
        new CommandAPICommand("request")
                .withAliases("submit-request", "submit-song", "song-request")
                .withFullDescription("Request a song to be played on TruckersFM. Does not work with AutoDJ.")
                .withArguments(new dev.jorel.commandapi.arguments.GreedyStringArgument("message"))
                .executesPlayer(this::handleCommand)
                .register();
    }

    protected void handleCommand(Player player, CommandArguments args) {
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
    }
}
