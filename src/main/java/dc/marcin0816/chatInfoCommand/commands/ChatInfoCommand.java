package dc.marcin0816.chatInfoCommand.commands;

import dc.marcin0816.chatInfoCommand.Main;
import dc.marcin0816.chatInfoCommand.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatInfoCommand extends Command {

    private final Main plugin;

    public ChatInfoCommand(Main plugin) {
        super("chatinfo");
        this.plugin = plugin;

        // Ustawienie opisu, instrukcji użycia i uprawnień
        setDescription("Komenda administracyjna do zarządzania wiadomościami");
        setUsage("/" + getName() + " [reload|list]");
        setPermission("chatinformator.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        // Sprawdzanie uprawnień
        if (!sender.hasPermission("chatinformator.admin")) {
            sender.sendMessage(plugin.getConfigManager().getNoPermissionMessage());
            return true;
        }

        // Obsługa komend
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadChatInfo(sender);
                break;
            case "list":
                listCommands(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    /**
     * Wyświetla pomoc dla komendy
     * @param sender Nadawca komendy
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Utils.colorize("&8&l========== &6&lChatInformator &8&l=========="));
        sender.sendMessage(Utils.colorize("&e/chatinfo reload &7- Przeładowuje konfigurację"));
        sender.sendMessage(Utils.colorize("&e/chatinfo list &7- Wyświetla listę dostępnych komend"));
        sender.sendMessage(Utils.colorize("&8&l==================================="));
    }

    /**
     * Wyświetla listę dostępnych komend
     * @param sender Nadawca komendy
     */
    private void listCommands(CommandSender sender) {
        List<String> commands = plugin.getConfigManager().getAllCommands();

        sender.sendMessage(Utils.colorize("&8&l========== &6&lDostępne komendy &8&l=========="));

        if (commands.isEmpty()) {
            sender.sendMessage(Utils.colorize("&cBrak zdefiniowanych komend."));
        } else {
            for (String cmd : commands) {
                String permission = plugin.getConfigManager().getCommandPermission(cmd);
                if (permission == null || permission.isEmpty()) {
                    sender.sendMessage(Utils.colorize("&e/" + cmd + " &7- dostępna dla wszystkich"));
                } else {
                    sender.sendMessage(Utils.colorize("&e/" + cmd + " &7- wymaga uprawnienia: &b" + permission));
                }
            }
        }

        sender.sendMessage(Utils.colorize("&8&l======================================="));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("reload", "list"));
            completions.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return completions;
        }

        return new ArrayList<>();
    }
}