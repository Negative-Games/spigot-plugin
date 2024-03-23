package games.negative.plugin.core;

import games.negative.alumina.logger.Logs;
import games.negative.alumina.message.Message;
import games.negative.plugin.Plugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum Locale {

    ;

    private final String content;
    private Message message;

    Locale(@NotNull String... defMessage) {
        this.content = String.join("\n", defMessage);
        this.message = Message.of(content);
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
            entry.message = new Message(String.join("\n", config.getStringList(entry.name())));
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


    /**
     * Sends a message to a specified audience with optional placeholders.
     *
     * @param audience     the audience to send the message to
     * @param placeholders the optional placeholders to be replaced in the message
     */
    public void send(@NotNull Audience audience, @Nullable String... placeholders) {
        message.send(audience, placeholders);
    }

    /**
     * Sends a message to a collection of audiences.
     *
     * @param iterable the collection of audiences to send the message to
     * @param <T> the type of iterable must extend Iterable<? extends Audience>
     * @throws NullPointerException if the iterable is null
     */
    public <T extends Iterable<? extends Audience>> void send(T iterable) {
        message.send(iterable);
    }

    /**
     * Sends a message to a collection of audiences with optional placeholders.
     * @param iterable the collection of audiences to send the message to
     * @param placeholders the optional placeholders to be replaced in the message
     * @param <T> the type of iterable must extend Iterable<? extends Audience>
     */
    public <T extends Iterable<? extends Audience>> void send(@NotNull T iterable, @Nullable String... placeholders) {
        message.send(iterable, placeholders);
    }

    /**
     * Broadcasts a message to all players on the server.
     *
     * @param placeholders an array of optional placeholders to replace in the message (nullable)
     */
    public void broadcast(@Nullable String... placeholders) {
        message.broadcast(placeholders);
    }

    /**
     * Broadcasts a message to all players on the server with optional placeholders.
     * @param audience the audience to broadcast the message to
     * @param placeholders the optional placeholders to be replaced in the message
     */
    public void broadcast(@Nullable Audience audience, @Nullable String... placeholders) {
        message.broadcast(audience, placeholders);
    }

    /**
     * Returns the message as a component.
     * @param audience the audience to send the message to
     * @param placeholders the optional placeholders to be replaced in the message
     * @return the message as a component
     */
    @NotNull
    public Component asComponent(@Nullable Audience audience, @Nullable String... placeholders) {
        return message.asComponent(audience, placeholders);
    }
}