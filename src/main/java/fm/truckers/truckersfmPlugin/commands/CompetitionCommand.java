package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import fm.truckers.truckersfmPlugin.enums.MessageType;
import fm.truckers.truckersfmPlugin.helpers.HttpRequestHelper;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class CompetitionCommand extends AbstractCommand {
    public void register(TruckersfmPlugin plugin) {
        new CommandAPICommand("competition")
                .withAliases("submit-competition", "submit-answer", "answer", "enter-competition")
                .withFullDescription("Send in your competition answer to TruckersFM.")
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
                                "&message_type=" + MessageType.Competition +
                                "&identifier=" + player.getUniqueId() +
                                "&platform=Minecraft plugin"
                ))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpRequestHelper.sendRequestAndHandleResponse(client, request, player, plugin);
    }
}
