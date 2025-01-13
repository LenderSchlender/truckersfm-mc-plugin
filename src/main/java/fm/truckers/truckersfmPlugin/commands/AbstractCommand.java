package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;

abstract class AbstractCommand {
    public abstract void register();
    protected abstract void handleCommand(Player player, CommandArguments args);
}
