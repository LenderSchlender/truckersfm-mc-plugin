package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class HidePlayerMessagesCommand extends AbstractCommand {
    private static final String PERMISSION = "truckersfm.hideplayermessages";

    @Override
    public void register(TruckersfmPlugin plugin) {
        this.plugin = plugin;
        new CommandAPICommand("hide-player-messages")
                .withFullDescription("Hides your join and quit messages.")
                .withPermission(PERMISSION)
                .executesPlayer(this::handleCommand)
                .register();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        String playerId = player.getUniqueId().toString();

        if (plugin.getHiddenPlayers().contains(playerId)) {
            plugin.getHiddenPlayers().remove(playerId);
            plugin.saveHiddenPlayers();

            player.sendMessage(Component.text("Your join and quit messages are now visible.", NamedTextColor.GREEN));

            return;
        }

        plugin.getHiddenPlayers().add(playerId);
        plugin.saveHiddenPlayers();
        player.sendMessage(Component.text("Your join and quit messages are now hidden.", NamedTextColor.GREEN));
    }
}
