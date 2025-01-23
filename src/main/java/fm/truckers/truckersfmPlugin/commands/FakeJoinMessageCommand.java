package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class FakeJoinMessageCommand extends AbstractCommand {
    private static final String PERMISSION = "truckersfm.fakejoinmessage";

    @Override
    public void register(TruckersfmPlugin plugin) {
        this.plugin = plugin;
        new CommandAPICommand("fake-join")
                .withFullDescription("Fakes your player join message.")
                .withPermission(PERMISSION)
                .executesPlayer(this::handleCommand)
                .register();
    }

    @Override
    protected void handleCommand(Player player, CommandArguments args) {
        String playerId = player.getUniqueId().toString();

        if (!plugin.getHiddenPlayers().contains(playerId)) {
            player.sendMessage(Component.text("You must hide your join and quit messages before using this command.", NamedTextColor.RED));

            return;
        }

        plugin.getServer().broadcast(Component.text(player.getName() + " joined the game", NamedTextColor.YELLOW));
    }
}
