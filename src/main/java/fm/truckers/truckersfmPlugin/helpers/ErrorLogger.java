package fm.truckers.truckersfmPlugin.helpers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ErrorLogger {
    public static void log(JavaPlugin plugin, Player player, Exception e) {
        plugin.getLogger().severe("An error occurred");
        player.sendMessage("An error occurred");
    }
}
