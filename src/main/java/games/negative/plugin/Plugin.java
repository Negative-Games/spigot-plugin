package games.negative.plugin;

import games.negative.alumina.AluminaPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.jetbrains.annotations.NotNull;

public class Plugin extends AluminaPlugin {

    private static Plugin instance;
    private BukkitAudiences audience;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    /**
     * Returns the instance of the plugin.
     * @return The instance of the plugin.
     */
    @NotNull
    public static Plugin instance() {
        return instance;
    }

    @NotNull
    public BukkitAudiences audience() {
        if (audience == null)
            audience = BukkitAudiences.create(this);

        return audience;
    }
}
