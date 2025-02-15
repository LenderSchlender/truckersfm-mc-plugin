package fm.truckers.truckersfmPlugin.listeners;

import com.earth2me.essentials.Essentials;
import fm.truckers.truckersfmPlugin.TruckersfmPlugin;
import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
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

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        // If the player isn't hidden, do nothing
        // this means the advancement notification will proceed as usual
        if (!hiddenPlayers.contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }

        event.message(null); // Hide the message

        // Get the advancement name (display title)
        Advancement advancement = event.getAdvancement();
        AdvancementDisplay display = advancement.getDisplay();

        if (display != null) {
            Component advancementName = display.title(); // Fetch the display title

            // Send a custom message to the player
            event.getPlayer().sendMessage(
                    Component.text("Your advancement ", NamedTextColor.YELLOW)
                            .append(advancementName.color(NamedTextColor.GREEN)) // Display title in green
                            .append(Component.text(" was unlocked but not announced in chat, because your player messages are hidden.", NamedTextColor.YELLOW))
            );
        }
    }
}
