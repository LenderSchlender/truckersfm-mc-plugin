package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopDeathsCommand extends AbstractCommand {
    public void register(TruckersfmPlugin plugin) {
        new CommandAPICommand("top-deaths")
                .withAliases("most-deaths", "death-leaderboard")
                .withFullDescription("Displays the top 10 players with the most deaths.")
                .executesPlayer(this::handleCommand)
                .register();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        // Fetch all online players and sort them by deaths
        List<Player> topPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) > 0) // Only include players with at least 1 death
                .sorted(Comparator.comparingInt((Player p) -> p.getStatistic(Statistic.DEATHS)).reversed())
                .limit(10)
                .collect(Collectors.toList());

        // Build the leaderboard message
        Component leaderboard = Component.text("Top 10 players by deaths", NamedTextColor.GOLD).append(Component.newline());
        int rank = 1;
        for (Player topPlayer : topPlayers) {
            leaderboard = leaderboard.append(
                    Component.text(rank + ". ", NamedTextColor.YELLOW)
                            .append(Component.text(topPlayer.getName(), NamedTextColor.WHITE))
                            .append(Component.text(" - ", NamedTextColor.GRAY))
                            .append(Component.text(topPlayer.getStatistic(Statistic.DEATHS) + " " +
                                    (topPlayer.getStatistic(Statistic.DEATHS) == 1 ? "death" : "deaths"), NamedTextColor.RED))
                            .append(Component.newline())
            );
            rank++;
        }

        // Send the leaderboard to the player
        player.sendMessage(leaderboard);
    }
}
