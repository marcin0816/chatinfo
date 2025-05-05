package dc.marcin0816.chatInfoCommand;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DynamicCommand extends Command {

    private final Main plugin;
    private final String commandName;

    public DynamicCommand(Main plugin, String commandName, String description) {
        super(commandName);
        this.plugin = plugin;
        this.commandName = commandName;

        // Ustaw opis i użycie
        setDescription(description);
        setUsage("/" + commandName);

        // Ustaw uprawnienia jeśli istnieją
        String permission = plugin.getConfigManager().getCommandPermission(commandName);
        if (permission != null && !permission.isEmpty()) {
            setPermission(permission);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        // Sprawdzanie uprawnień
        String permission = plugin.getConfigManager().getCommandPermission(commandName);
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            sender.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            return true;
        }

        // Wyświetlanie wiadomości
        List<Component> messages = plugin.getConfigManager().getCommandMessages(commandName);
        for (Component message : messages) {
            sender.sendMessage(message);
        }

        return true;
    }
}