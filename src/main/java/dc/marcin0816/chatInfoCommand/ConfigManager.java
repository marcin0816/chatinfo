package dc.marcin0816.chatInfoCommand;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Pobiera listę wiadomości dla danej komendy
     * @param command Nazwa komendy
     * @return Lista wiadomości lub pusta lista jeśli komenda nie istnieje
     */
    public List<Component> getCommandMessages(String command) {
        List<Component> messages = new ArrayList<>();

        ConfigurationSection commandSection = plugin.getConfig().getConfigurationSection("commands." + command);
        if (commandSection != null && commandSection.contains("messages")) {
            List<String> configMessages = commandSection.getStringList("messages");

            // Kolorowanie wiadomości
            for (String message : configMessages) {
                messages.add(Utils.colorize(message));
            }
        }

        return messages;
    }

    /**
     * Sprawdza czy komenda wymaga uprawnień
     * @param command Nazwa komendy
     * @return Nazwa uprawnienia lub null jeśli nie wymaga uprawnień
     */
    public String getCommandPermission(String command) {
        return plugin.getConfig().getString("commands." + command + ".permission", null);
    }

    /**
     * Pobiera wszystkie zdefiniowane komendy
     * @return Lista nazw komend
     */
    public List<String> getAllCommands() {
        List<String> commands = new ArrayList<>();

        ConfigurationSection commandsSection = plugin.getConfig().getConfigurationSection("commands");
        if (commandsSection != null) {
            commands.addAll(commandsSection.getKeys(false));
        }

        return commands;
    }

    /**
     * Pobiera wiadomość o braku uprawnień
     * @return Wiadomość o braku uprawnień
     */
    public Component getNoPermissionMessage() {
        return Utils.colorize(plugin.getConfig().getString("no-permission-message", "&cNie masz uprawnień do używania tej komendy!"));
    }
}