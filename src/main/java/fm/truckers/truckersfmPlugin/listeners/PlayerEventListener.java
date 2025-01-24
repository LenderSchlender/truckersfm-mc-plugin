package fm.truckers.truckersfmPlugin.listeners;

import com.earth2me.essentials.Essentials;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import org.bukkit.Bukkit;
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

            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (essentials != null) {
                try {
                    essentials.getUser(event.getPlayer()).setVanished(true);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Failed to vanish player " + event.getPlayer().getName() + " (on join): " + e.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (hiddenPlayers.contains(event.getPlayer().getUniqueId().toString())) {
            event.quitMessage(null); // Hide the message
        }
    }
}
