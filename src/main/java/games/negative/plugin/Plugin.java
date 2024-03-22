package games.negative.plugin;

import games.negative.alumina.AluminaPlugin;
import games.negative.plugin.core.Locale;
import org.jetbrains.annotations.NotNull;

public class Plugin extends AluminaPlugin {

    private static Plugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        Locale.init(this);
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

}
