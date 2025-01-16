package fm.truckers.truckersfmPlugin.helpers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ErrorLogger {
    public static void log(JavaPlugin plugin, Player player, Exception e) {
        plugin.getLogger().severe(e.getMessage());
        player.sendMessage(Component.text("An error occurred", NamedTextColor.RED));
    }
}
