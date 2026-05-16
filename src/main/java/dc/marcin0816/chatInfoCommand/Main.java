package dc.marcin0816.chatInfoCommand;

import dc.marcin0816.chatInfoCommand.commands.ChatInfoCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ConfigManager configManager;
    private MessageEditorGUI editorManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        editorManager = new MessageEditorGUI(this);

        CommandMap commandMap = Bukkit.getServer().getCommandMap();
        commandMap.register("chatinfo", getName().toLowerCase(), new ChatInfoCommand(this));

        registerCommandsFromConfig();

        getLogger().info("ChatInfo enabled.");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("PlaceholderAPI found — placeholder support active.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatInfo disabled.");
    }

    public void registerCommandsFromConfig() {
        ConfigurationSection section = getConfig().getConfigurationSection("commands");
        if (section == null) return;
        CommandMap commandMap = Bukkit.getServer().getCommandMap();
        for (String cmdName : section.getKeys(false)) {
            commandMap.register(cmdName, getName().toLowerCase(), new DynamicCommand(this, cmdName));
        }
    }

    public void registerSingleCommand(String commandName) {
        Bukkit.getServer().getCommandMap().register(commandName, getName().toLowerCase(), new DynamicCommand(this, commandName));
        syncCommands();
    }

    private void unregisterCommands() {
        ConfigurationSection section = getConfig().getConfigurationSection("commands");
        if (section == null) return;
        for (String cmdName : section.getKeys(false)) {
            unregisterSingleCommand(cmdName);
        }
    }

    public void unregisterSingleCommand(String commandName) {
        SimpleCommandMap simpleMap = (SimpleCommandMap) Bukkit.getServer().getCommandMap();
        Command existing = simpleMap.getCommand(commandName);
        if (existing != null) {
            simpleMap.getKnownCommands().remove(commandName);
            simpleMap.getKnownCommands().remove(getName().toLowerCase() + ":" + commandName);
            // Also remove aliases
            existing.getAliases().forEach(alias -> {
                simpleMap.getKnownCommands().remove(alias);
                simpleMap.getKnownCommands().remove(getName().toLowerCase() + ":" + alias);
            });
        }
    }

    // Called by /chatinfo reload — reloads and notifies the sender
    public void reloadChatInfo(CommandSender sender) {
        reloadCommands();
        sender.sendMessage(Utils.colorize(getConfig().getString(
                "reload-message", "<green><bold>✔</bold> Configuration reloaded successfully!")));
    }

    // Internal reload used by the editor — no message sent
    public void reloadCommands() {
        reloadConfig();
        unregisterCommands();
        registerCommandsFromConfig();
        syncCommands();
    }

    private void syncCommands() {
        try {
            Bukkit.getServer().getClass().getMethod("syncCommands").invoke(Bukkit.getServer());
        } catch (Exception e) {
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageEditorGUI getEditorManager() {
        return editorManager;
    }
}
