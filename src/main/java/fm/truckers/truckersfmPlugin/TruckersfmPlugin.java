package fm.truckers.truckersfmPlugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import fm.truckers.truckersfmPlugin.commands.*;
import fm.truckers.truckersfmPlugin.listeners.PlayerEventListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class TruckersfmPlugin extends JavaPlugin implements Listener {
    private Set<String> hiddenPlayers;
    private File hiddenPlayersFile;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));

        // Ensure the data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize the hidden players file
        hiddenPlayersFile = new File(getDataFolder(), "hidden_players.yml");
        if (!hiddenPlayersFile.exists()) {
            try {
                hiddenPlayersFile.createNewFile(); // Create the file if it doesn't exist
                getLogger().info("Created hidden_players.yml file.");
            } catch (IOException e) {
                getLogger().severe("Could not create hidden_players.yml file: " + e.getMessage());
            }
        }

        loadHiddenPlayers();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        new HidePlayerMessagesCommand().register(this);

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

        new TopDeathsCommand().register(this);

        new FakeLeaveMessageCommand().register(this);

        new FakeJoinMessageCommand().register(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    private void loadHiddenPlayers() {
        hiddenPlayers = new HashSet<>();
        if (hiddenPlayersFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(hiddenPlayersFile);
            hiddenPlayers.addAll(config.getStringList("hidden_players"));
        }
    }

    public void saveHiddenPlayers() {
        try (FileWriter writer = new FileWriter(hiddenPlayersFile)) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("hidden_players", new ArrayList<>(hiddenPlayers));
            config.save(hiddenPlayersFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save hidden_players.yml: " + e.getMessage());
        }
    }

    public Set<String> getHiddenPlayers() {
        return hiddenPlayers;
    }
}
