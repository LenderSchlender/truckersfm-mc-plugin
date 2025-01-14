package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.helpers.JsonValueParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import fm.truckers.truckersfmPlugin.helpers.TimeConverter;

public class PresenterCommand extends AbstractCommand {
    public void register() {
        new CommandAPICommand("presenter")
                .withAliases("dj", "live")
                .withFullDescription("The current live presenter on TruckersFM, and the next upcoming presenter.")
                .executesPlayer(this::handleCommand)
                .register();
    }

    protected void handleCommand(Player player, CommandArguments args) {
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
                    player.sendMessage(Component.text("Failed to fetch live presenter info. HTTP Status: " + liveResponse.statusCode(), NamedTextColor.RED));
                    return;
                }

                String livePresenterName = JsonValueParser.parse(liveResponse.body(), "data.user.name");
                String livePresenterDescription = JsonValueParser.parse(liveResponse.body(), "data.description");
                long livePresenterEnd = Long.parseLong(JsonValueParser.parse(liveResponse.body(), "data.end"));

                TextComponent livePresenterText = Component.text("Currently live: ", NamedTextColor.GOLD)
                        .append(Component.text(livePresenterName, NamedTextColor.GREEN, TextDecoration.BOLD))
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(livePresenterDescription, NamedTextColor.WHITE, TextDecoration.ITALIC));

                if (livePresenterEnd != 0) {
                    livePresenterText = livePresenterText
                            .append(Component.text(" until ", NamedTextColor.GRAY))
                            .append(Component.text(TimeConverter.convertTimestampToTime(livePresenterEnd), NamedTextColor.AQUA));
                }

                player.sendMessage(livePresenterText);

                // Fetch upcoming presenter
                HttpResponse<String> upcomingResponse = client.send(upcomingPresenterRequest, HttpResponse.BodyHandlers.ofString());
                if (upcomingResponse.statusCode() != 200) {
                    player.sendMessage(Component.text("Failed to fetch upcoming presenter info. HTTP Status: " + upcomingResponse.statusCode(), NamedTextColor.RED));
                    return;
                }

                String upcomingPresenterName = JsonValueParser.parse(upcomingResponse.body(), "data.user.name");
                String upcomingPresenterDescription = JsonValueParser.parse(upcomingResponse.body(), "data.description");
                long upcomingPresenterStart = Long.parseLong(JsonValueParser.parse(upcomingResponse.body(), "data.start"));
                long upcomingPresenterEnd = Long.parseLong(JsonValueParser.parse(upcomingResponse.body(), "data.end"));

                TextComponent upcomingPresenterText = Component.text("Next up: ", NamedTextColor.GOLD)
                        .append(Component.text(upcomingPresenterName, NamedTextColor.GREEN, TextDecoration.BOLD))
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(upcomingPresenterDescription, NamedTextColor.WHITE, TextDecoration.ITALIC))
                        .append(Component.text(" from ", NamedTextColor.GRAY))
                        .append(Component.text(TimeConverter.convertTimestampToTime(upcomingPresenterStart), NamedTextColor.AQUA));

                if (upcomingPresenterEnd != 0) {
                    upcomingPresenterText = upcomingPresenterText
                            .append(Component.text(" until ", NamedTextColor.GRAY))
                            .append(Component.text(TimeConverter.convertTimestampToTime(upcomingPresenterEnd), NamedTextColor.AQUA));
                }

                player.sendMessage(upcomingPresenterText);
            } catch (Exception e) {
                player.sendMessage(Component.text("An error occurred", NamedTextColor.RED));

                e.printStackTrace();
            }
        });
    }
}
