package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;

public class DisableScoreboardCommand extends AbstractCommand {
    private final Set<Player> trackedPlayers;

    public DisableScoreboardCommand(Set<Player> trackedPlayers) {
        this.trackedPlayers = trackedPlayers;
    }

    public void register(TruckersfmPlugin plugin) {
        new CommandAPICommand("disable-scoreboard")
                .withFullDescription("Disable the TruckersFM scoreboard")
                .executesPlayer(this::handleCommand)
                .register();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        if (trackedPlayers.remove(player)) {
            Scoreboard emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(emptyScoreboard);
            player.sendMessage(Component.text("TruckersFM scoreboard has been disabled.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("No active TruckersFM scoreboard to disable.", NamedTextColor.RED));
        }
    }
}
