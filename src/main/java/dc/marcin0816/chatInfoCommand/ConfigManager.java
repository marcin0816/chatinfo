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

    public List<String> getRawCommandMessages(String command) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("commands." + command);
        if (section == null) return List.of();
        return section.getStringList("messages");
    }

    public List<Component> getCommandMessages(String command) {
        return getRawCommandMessages(command).stream()
                .map(Utils::colorize)
                .toList();
    }

    public String getCommandPermission(String command) {
        return plugin.getConfig().getString("commands." + command + ".permission", null);
    }

    public String getCommandDescription(String command) {
        return plugin.getConfig().getString("commands." + command + ".description", "Informational command");
    }

    public List<String> getCommandAliases(String command) {
        return plugin.getConfig().getStringList("commands." + command + ".aliases");
    }

    public int getCommandCooldown(String command) {
        return plugin.getConfig().getInt("commands." + command + ".cooldown", 0);
    }

    public List<String> getAllCommands() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("commands");
        if (section == null) return List.of();
        return new ArrayList<>(section.getKeys(false));
    }

    public Component getNoPermissionMessage() {
        return Utils.colorize(plugin.getConfig().getString(
                "no-permission-message", "<red>✘ You don't have permission to use this command!"));
    }

    public Component getCooldownMessage(int remainingSeconds) {
        String template = plugin.getConfig().getString(
                "cooldown-message", "<red>✘ Please wait <yellow>{time}s</yellow> before using this command again!");
        return Utils.colorize(template.replace("{time}", String.valueOf(remainingSeconds)));
    }
}
