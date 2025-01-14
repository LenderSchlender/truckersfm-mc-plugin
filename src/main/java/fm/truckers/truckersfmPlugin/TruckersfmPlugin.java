package fm.truckers.truckersfmPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import fm.truckers.truckersfmPlugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TruckersfmPlugin extends JavaPlugin implements Listener {
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)); // Load with verbose output
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        CommandAPI.onEnable();

        new PresenterCommand().register();

        new SongCommand().register();

        new RequestCommand().register();

        new ShoutoutCommand().register();

        new CompetitionCommand().register();

        new JokeCommand().register();

        new OtherCommand().register();

        EnableScoreboardCommand enableScoreboardCommand = new EnableScoreboardCommand(this);
        enableScoreboardCommand.register();

        new DisableScoreboardCommand(enableScoreboardCommand.getTrackedPlayers()).register();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
