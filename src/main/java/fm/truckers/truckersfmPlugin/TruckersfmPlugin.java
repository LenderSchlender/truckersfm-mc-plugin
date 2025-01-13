package fm.truckers.truckersfmPlugin;

import fm.truckers.truckersfmPlugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TruckersfmPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        new PresenterCommand().register();

        new SongCommand().register();

        new RequestCommand().register();

        new ShoutoutCommand().register();

        new CompetitionCommand().register();

        new JokeCommand().register();

        new OtherCommand().register();
    }
}
