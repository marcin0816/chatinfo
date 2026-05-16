package dc.marcin0816.chatInfoCommand.commands;

import dc.marcin0816.chatInfoCommand.Main;
import dc.marcin0816.chatInfoCommand.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatInfoCommand extends Command {

    private final Main plugin;

    public ChatInfoCommand(Main plugin) {
        super("chatinfo");
        this.plugin = plugin;
        setDescription("Admin command for managing informational commands");
        setUsage("/" + getName() + " [reload|list|create|edit|delete|setperm]");
        setPermission("chatinformator.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("chatinformator.admin")) {
            sender.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> plugin.reloadChatInfo(sender);
            case "list" -> listCommands(sender);
            case "create" -> {
                if (args.length < 2) { sender.sendMessage(Utils.colorize("<red>Usage: /chatinfo create <name>")); return true; }
                createCommand(sender, args[1].toLowerCase());
            }
            case "edit" -> {
                if (args.length < 2) { sender.sendMessage(Utils.colorize("<red>Usage: /chatinfo edit <name>")); return true; }
                editCommand(sender, args[1].toLowerCase());
            }
            case "delete" -> {
                if (args.length < 2) { sender.sendMessage(Utils.colorize("<red>Usage: /chatinfo delete <name>")); return true; }
                deleteCommand(sender, args[1].toLowerCase());
            }
            case "setperm" -> {
                if (args.length < 2) { sender.sendMessage(Utils.colorize("<red>Usage: /chatinfo setperm <name> [permission]")); return true; }
                setCommandPermission(sender, args[1].toLowerCase(), args.length >= 3 ? args[2] : "");
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Utils.colorize("<dark_gray>━━━━━━━━ <gold><bold>ChatInfo</bold></gold> ━━━━━━━━"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo reload <dark_gray>— <gray>Reload configuration"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo list <dark_gray>— <gray>List all commands"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo create <name> <dark_gray>— <gray>Create a new command"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo edit <name> <dark_gray>— <gray>Edit command messages"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo delete <name> <dark_gray>— <gray>Delete a command"));
        sender.sendMessage(Utils.colorize("<yellow>/chatinfo setperm <name> [perm] <dark_gray>— <gray>Set/clear permission"));
        sender.sendMessage(Utils.colorize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }

    private void listCommands(CommandSender sender) {
        List<String> commands = plugin.getConfigManager().getAllCommands();
        sender.sendMessage(Utils.colorize("<dark_gray>━━━━━━ <gold><bold>Commands (" + commands.size() + ")</bold></gold> ━━━━━━"));
        if (commands.isEmpty()) {
            sender.sendMessage(Utils.colorize("<gray>No commands defined yet."));
        } else {
            for (String cmd : commands) {
                String desc = plugin.getConfigManager().getCommandDescription(cmd);
                String perm = plugin.getConfigManager().getCommandPermission(cmd);
                List<String> aliases = plugin.getConfigManager().getCommandAliases(cmd);
                int cooldown = plugin.getConfigManager().getCommandCooldown(cmd);

                String permText = (perm == null || perm.isEmpty()) ? "<green>public" : "<aqua>" + perm;
                String aliasText = aliases.isEmpty() ? "" : " <dark_gray>(" + String.join(", ", aliases) + ")";
                String cooldownText = cooldown > 0 ? " <dark_gray>[" + cooldown + "s cooldown]" : "";

                sender.sendMessage(Utils.colorize("<yellow>/" + cmd + aliasText + " <dark_gray>— <white>" + desc
                        + " <dark_gray>| " + permText + cooldownText));
            }
        }
        sender.sendMessage(Utils.colorize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }

    private void createCommand(CommandSender sender, String commandName) {
        if (plugin.getConfigManager().getAllCommands().contains(commandName)) {
            sender.sendMessage(Utils.colorize("<red>✘ Command <yellow>/" + commandName + " <red>already exists!"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.colorize("<red>✘ This subcommand requires a player (uses book editor)."));
            return;
        }
        plugin.getEditorManager().openEditor(player, commandName, null, true);
    }

    private void editCommand(CommandSender sender, String commandName) {
        if (!plugin.getConfigManager().getAllCommands().contains(commandName)) {
            sender.sendMessage(Utils.colorize("<red>✘ Command <yellow>/" + commandName + " <red>does not exist!"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.colorize("<red>✘ This subcommand requires a player (uses book editor)."));
            return;
        }
        List<String> current = plugin.getConfig().getStringList("commands." + commandName + ".messages");
        plugin.getEditorManager().openEditor(player, commandName, current, false);
    }

    private void deleteCommand(CommandSender sender, String commandName) {
        if (!plugin.getConfigManager().getAllCommands().contains(commandName)) {
            sender.sendMessage(Utils.colorize("<red>✘ Command <yellow>/" + commandName + " <red>does not exist!"));
            return;
        }
        plugin.unregisterSingleCommand(commandName);
        plugin.getConfig().set("commands." + commandName, null);
        plugin.saveConfig();
        sender.sendMessage(Utils.colorize("<green>✔ Deleted command <yellow>/" + commandName + "<green>."));
    }

    private void setCommandPermission(CommandSender sender, String commandName, String permission) {
        if (!plugin.getConfigManager().getAllCommands().contains(commandName)) {
            sender.sendMessage(Utils.colorize("<red>✘ Command <yellow>/" + commandName + " <red>does not exist!"));
            return;
        }
        plugin.getConfig().set("commands." + commandName + ".permission", permission);
        plugin.saveConfig();
        plugin.unregisterSingleCommand(commandName);
        plugin.registerSingleCommand(commandName);
        if (permission.isEmpty()) {
            sender.sendMessage(Utils.colorize("<green>✔ Cleared permission for <yellow>/" + commandName + "<green> (now public)."));
        } else {
            sender.sendMessage(Utils.colorize("<green>✔ Set permission for <yellow>/" + commandName + " <green>to <aqua>" + permission + "<green>."));
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("chatinformator.admin")) return List.of();

        if (args.length == 1) {
            return Arrays.asList("reload", "list", "create", "edit", "delete", "setperm")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("edit") || sub.equals("delete") || sub.equals("setperm")) {
                return plugin.getConfigManager().getAllCommands().stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .toList();
            }
        }

        return List.of();
    }
}
