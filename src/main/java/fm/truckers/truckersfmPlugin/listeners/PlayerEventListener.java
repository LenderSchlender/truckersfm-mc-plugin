package fm.truckers.truckersfmPlugin.listeners;

import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class PlayerEventListener implements Listener {
    private final Set<String> hiddenPlayers;

    public PlayerEventListener(TruckersfmPlugin plugin) {
        this.hiddenPlayers = plugin.getHiddenPlayers();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (hiddenPlayers.contains(event.getPlayer().getUniqueId().toString())) {
            event.joinMessage(null); // Hide the message
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (hiddenPlayers.contains(event.getPlayer().getUniqueId().toString())) {
            event.quitMessage(null); // Hide the message
        }
    }
}
