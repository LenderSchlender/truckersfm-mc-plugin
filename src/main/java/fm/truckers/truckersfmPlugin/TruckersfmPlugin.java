package fm.truckers.truckersfmPlugin;

import dev.jorel.commandapi.CommandAPICommand;
import fm.truckers.truckersfmPlugin.commands.PresenterCommand;
import fm.truckers.truckersfmPlugin.commands.RequestCommand;
import fm.truckers.truckersfmPlugin.commands.ShoutoutCommand;
import fm.truckers.truckersfmPlugin.commands.SongCommand;
import fm.truckers.truckersfmPlugin.enums.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public final class TruckersfmPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        new PresenterCommand().register();

        new SongCommand().register();

        new RequestCommand().register();

        new ShoutoutCommand().register();
    }
}
