package dc.marcin0816.chatInfoCommand;

import dc.marcin0816.chatInfoCommand.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Map;

public class Main extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Inicjalizacja managera konfiguracji
        configManager = new ConfigManager(this);

        // Zapisanie domyślnej konfiguracji, jeśli nie istnieje
        saveDefaultConfig();

        // Rejestracja komendy administracyjnej
        Command chatInfoCommand = new ChatInfoCommand(this);
        Bukkit.getServer().getCommandMap().register("chatinfo", getName().toLowerCase(), chatInfoCommand);
        getLogger().info("Zarejestrowano komendę administracyjną: chatinfo");

        // Rejestracja komend z konfiguracji
        registerCommandsFromConfig();

        getLogger().info("ChatInformator został włączony!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatInformator został wyłączony!");
    }

    /**
     * Rejestruje wszystkie komendy zdefiniowane w pliku konfiguracyjnym
     */
    public void registerCommandsFromConfig() {
        // Pobierz CommandMap
        CommandMap commandMap = Bukkit.getServer().getCommandMap();

        // Rejestracja komend z konfiguracji
        if (getConfig().contains("commands")) {
            ConfigurationSection commandsSection = getConfig().getConfigurationSection("commands");
            if (commandsSection != null) {
                for (String cmdName : commandsSection.getKeys(false)) {
                    // Uzyskaj opis komendy, lub użyj domyślnego
                    String description = "Komenda informacyjna";

                    // Tworzenie i rejestracja dynamicznej komendy
                    DynamicCommand command = new DynamicCommand(this, cmdName, description);
                    commandMap.register(cmdName, getName().toLowerCase(), command);
                    getLogger().info("Zarejestrowano komendę: " + cmdName);
                }
            } else {
                getLogger().warning("Sekcja 'commands' nie jest poprawną sekcją konfiguracji!");
            }
        }
    }

    private void unregisterCommands() {
        try {
            // Usuń wszystkie komendy spersonalizowane
            CommandMap commandMap = Bukkit.getServer().getCommandMap();
            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            // Pobierz komendy z konfiguracji do usunięcia
            if (getConfig().contains("commands")) {
                ConfigurationSection commandsSection = getConfig().getConfigurationSection("commands");
                if (commandsSection != null) {
                    for (String cmdName : commandsSection.getKeys(false)) {
                        // Usuń komendę z mapy
                        knownCommands.remove(cmdName);
                        knownCommands.remove(getName().toLowerCase() + ":" + cmdName);
                        getLogger().info("Wyrejestrowano komendę: " + cmdName);
                    }
                }
            }
        } catch (Exception e) {
            getLogger().warning("Nie udało się usunąć komend: " + e.getMessage());
        }
    }

    /**
     * Przeładowuje konfigurację pluginu
     * @param sender Nadawca komendy przeładowania
     */
    public void reloadChatInfo(CommandSender sender) {
        reloadConfig();

        // Usuń stare komendy i zarejestruj nowe
        unregisterCommands();
        registerCommandsFromConfig();

        String reloadMessage = getConfig().getString("reload-message", "&aKonfiguracja została przeładowana!");
        sender.sendMessage(Utils.colorize(reloadMessage));
    }

    /**
     * Zwraca manager konfiguracji
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
}