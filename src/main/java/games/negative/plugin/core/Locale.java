package games.negative.plugin.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import games.negative.alumina.logger.Logs;
import games.negative.plugin.Plugin;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public enum Locale {

    ;

    private String content;

    Locale(@NotNull String... defMessage) {
        this.content = String.join("\n", defMessage);
    }

    public static void init(@NotNull Plugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        validateFile(file);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean changed = false;
        for (Locale entry : values()) {
            if (config.isSet(entry.name())) continue;

            List<String> message = List.of(entry.content.split("\n"));
            config.set(entry.name(), message);
            changed = true;
        }

        if (changed) saveFile(file, config);

        for (Locale entry : values()) {
            entry.content = String.join("\n", config.getStringList(entry.name()));
        }
    }

    private static void saveFile(@NotNull File file, @NotNull FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            Logs.SEVERE.print("Could not save messages.yml file!", true);
        }
    }

    private static void validateFile(@NotNull File file) {
        if (!file.exists()) {
            boolean dirSuccess = file.getParentFile().mkdirs();
            if (dirSuccess) Logs.INFO.print("Created new plugin directory file!");

            try {
                boolean success = file.createNewFile();
                if (!success) return;

                Logs.INFO.print("Created messages.yml file!");
            } catch (IOException e) {
                Logs.SEVERE.print("Could not create messages.yml file!", true);
            }
        }
    }


    public void send(@NotNull CommandSender sender, @Nullable String... placeholders) {
        MiniMessage mm = MiniMessage.miniMessage();

        Map<String, String> placeholderMap = Maps.newHashMap();

        Component component = mm.deserialize(sender instanceof Player ? PlaceholderAPI.setPlaceholders((Player) sender, content) : PlaceholderAPI.setPlaceholders(null, content));
        if (placeholders != null) {
            Preconditions.checkArgument(placeholders.length % 2 == 0, "Placeholders must be in key-value pairs.");

            for (int i = 0; i < placeholders.length; i += 2) {
                placeholderMap.put(placeholders[i], placeholders[i + 1]);
            }
        }

        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            component = component.replaceText(TextReplacementConfig.builder().matchLiteral(entry.getKey()).replacement(entry.getValue()).build());
        }

        @SuppressWarnings("all")
        Audience audience = Plugin.instance().audience().sender(sender);

        audience.sendMessage(component);
    }

    public <T extends Iterable<? extends CommandSender>> void send(T iterable, @Nullable String... placeholders) {
        for (CommandSender sender : iterable) {
            send(sender, placeholders);
        }
    }

    public void broadcast(@Nullable String... placeholders) {
        send(Bukkit.getOnlinePlayers(), placeholders);
    }

}