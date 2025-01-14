package fm.truckers.truckersfmPlugin.commands;

import dev.jorel.commandapi.executors.CommandArguments;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import org.bukkit.entity.Player;

abstract class AbstractCommand {
    protected TruckersfmPlugin plugin;

    public abstract void register(TruckersfmPlugin plugin);
    protected abstract void handleCommand(Player player, CommandArguments args);
}
