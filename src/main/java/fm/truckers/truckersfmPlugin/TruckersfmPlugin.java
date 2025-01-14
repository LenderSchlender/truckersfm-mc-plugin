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
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        CommandAPI.onEnable();

        new PresenterCommand().register(this);

        new SongCommand().register(this);

        new RequestCommand().register(this);

        new ShoutoutCommand().register(this);

        new CompetitionCommand().register(this);

        new JokeCommand().register(this);

        new OtherCommand().register(this);

        EnableScoreboardCommand enableScoreboardCommand = new EnableScoreboardCommand();
        enableScoreboardCommand.register(this);

        new DisableScoreboardCommand(enableScoreboardCommand.getTrackedPlayers())
                .register(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
