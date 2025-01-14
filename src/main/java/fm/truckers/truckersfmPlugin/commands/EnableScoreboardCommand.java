package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import fm.truckers.truckersfmPlugin.helpers.ErrorLogger;
import fm.truckers.truckersfmPlugin.helpers.JsonValueParser;
import fm.truckers.truckersfmPlugin.helpers.TimeConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class EnableScoreboardCommand extends AbstractCommand {
    private final Set<Player> trackedPlayers = new HashSet<>();

    public void register(TruckersfmPlugin plugin) {
        this.plugin = plugin;
        new CommandAPICommand("enable-scoreboard")
                .withFullDescription("Display the current song and presenter on the side of your screen")
                .executesPlayer(this::handleCommand)
                .register();

        // Schedule periodic updates every 25 seconds (500 ticks)
        startPeriodicUpdates();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        if (trackedPlayers.contains(player)) {
            player.sendMessage(Component.text("TruckersFM scoreboard is already enabled.", NamedTextColor.RED));
            return;
        }

        trackedPlayers.add(player);
        fetchAndUpdateScoreboard(player);

        player.sendMessage(Component.text("TruckersFM scoreboard enabled.", NamedTextColor.GREEN));
    }

    public Set<Player> getTrackedPlayers() {
        return trackedPlayers;
    }

    private void startPeriodicUpdates() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : trackedPlayers) {
                fetchAndUpdateScoreboard(player);
            }
        }, 0L, 500L); // 500 ticks = 25 seconds
    }

    private void fetchAndUpdateScoreboard(Player player) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest songRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://radiocloud.pro/api/public/v1/song/current"))
                .GET()
                .build();

        HttpRequest presenterRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://radiocloud.pro/api/public/v1/presenter/live"))
                .GET()
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                // Fetch song data
                HttpResponse<String> songResponse = client.send(songRequest, HttpResponse.BodyHandlers.ofString());
                String artist = JsonValueParser.parse(songResponse.body(), "data.artist");
                String title = JsonValueParser.parse(songResponse.body(), "data.title");

                // Fetch presenter data
                HttpResponse<String> presenterResponse = client.send(presenterRequest, HttpResponse.BodyHandlers.ofString());
                String presenterName = JsonValueParser.parse(presenterResponse.body(), "data.user.name");
                String presenterDescription = JsonValueParser.parse(presenterResponse.body(), "data.description");
                long presenterEnd = Long.parseLong(JsonValueParser.parse(presenterResponse.body(), "data.end"));

                // Update scoreboard
                Bukkit.getScheduler().runTask(plugin, () -> updateScoreboard(player, artist, title, presenterName, presenterDescription, presenterEnd));
            } catch (Exception e) {
                ErrorLogger.log(plugin, player, e);
            }
        });
    }

    private void updateScoreboard(Player player, String artist, String title, String presenterName, String presenterDescription, long presenterEnd) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("TruckersFM", "dummy", "§d§lTruckers§r§d.FM");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        objective.getScore("§eNow playing:").setScore(5);
        objective.getScore("§f" + artist + " - " + title).setScore(4);
        objective.getScore(" ").setScore(3);
        objective.getScore("§bPresenter:").setScore(2);
        objective.getScore("§f" + presenterName + " - " + presenterDescription).setScore(1);

        if (presenterEnd != 0) {
            objective.getScore("§7Until: " + TimeConverter.convertTimestampToTime(presenterEnd)).setScore(0);
        }

        player.setScoreboard(scoreboard);
    }
}
