package dc.marcin0816.chatInfoCommand;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DynamicCommand extends Command {

    private final Main plugin;
    private final String commandName;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public DynamicCommand(Main plugin, String commandName) {
        super(commandName);
        this.plugin = plugin;
        this.commandName = commandName;

        setDescription(plugin.getConfigManager().getCommandDescription(commandName));
        setUsage("/" + commandName);
        setAliases(plugin.getConfigManager().getCommandAliases(commandName));

        String permission = plugin.getConfigManager().getCommandPermission(commandName);
        if (permission != null && !permission.isEmpty()) {
            setPermission(permission);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        String permission = plugin.getConfigManager().getCommandPermission(commandName);
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            sender.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            return true;
        }

        if (sender instanceof Player player) {
            int cooldown = plugin.getConfigManager().getCommandCooldown(commandName);
            if (cooldown > 0) {
                long now = System.currentTimeMillis();
                UUID uid = player.getUniqueId();
                Long lastUse = cooldowns.get(uid);
                if (lastUse != null) {
                    long remaining = (lastUse + cooldown * 1000L - now) / 1000;
                    if (remaining > 0) {
                        sender.sendMessage(plugin.getConfigManager().getCooldownMessage((int) remaining));
                        return true;
                    }
                }
                cooldowns.put(uid, now);
            }
        }

        boolean papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (papiEnabled && sender instanceof Player player) {
            for (String raw : plugin.getConfigManager().getRawCommandMessages(commandName)) {
                sender.sendMessage(Utils.colorize(PAPIHook.setPlaceholders(player, raw)));
            }
        } else {
            List<Component> messages = plugin.getConfigManager().getCommandMessages(commandName);
            for (Component message : messages) {
                sender.sendMessage(message);
            }
        }

        return true;
    }
}
