package dc.marcin0816.chatInfoCommand;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MessageEditorGUI implements Listener {

    private static final int MSGS_PER_PAGE = 27; // 3 rows × 9
    private static final int INV_SIZE = 36;       // 4 rows

    private final Main plugin;
    private final Map<UUID, GUISession> sessions = new HashMap<>();
    private final Map<UUID, ChatSession> chatPending = new HashMap<>();

    public MessageEditorGUI(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openEditor(Player player, String commandName, List<String> initial, boolean isNew) {
        List<String> messages = initial != null ? new ArrayList<>(initial) : new ArrayList<>();
        GUISession session = new GUISession(commandName, isNew, messages, 0);
        sessions.put(player.getUniqueId(), session);
        renderGUI(player, session);
        player.sendMessage(Utils.colorize("<green>Opened editor for <yellow>/" + commandName + "<green>."));
    }

    private void renderGUI(Player player, GUISession session) {
        int totalPages = totalPages(session);
        var holder = new EditorHolder();
        Inventory inv = Bukkit.createInventory(holder, INV_SIZE,
                Utils.colorize("<gold>/" + session.commandName
                        + " <dark_gray>| <gray>" + session.messages.size()
                        + " msg <dark_gray>| <gray>pg " + (session.page + 1) + "/" + totalPages));
        holder.setInventory(inv);

        ItemStack filler = item(Material.GRAY_STAINED_GLASS_PANE, " ");

        // Row 0 controls
        inv.setItem(0, session.page > 0
                ? item(Material.ARROW, "§e◀ Previous page", "§7Page " + session.page + "/" + totalPages)
                : filler);
        inv.setItem(1, item(Material.LIME_DYE, "§a✚ Add message", "§7Type in chat to add a new line"));
        inv.setItem(2, filler);
        inv.setItem(3, filler);
        inv.setItem(4, item(Material.BOOKSHELF,
                "§6/" + session.commandName,
                "§7Messages: §e" + session.messages.size(),
                "§7Page: §e" + (session.page + 1) + " §8/ §e" + totalPages));
        inv.setItem(5, filler);
        inv.setItem(6, filler);
        inv.setItem(7, item(Material.LIME_STAINED_GLASS_PANE, "§a✔ Save & Close", "§7Saves all changes"));
        inv.setItem(8, hasNextPage(session)
                ? item(Material.ARROW, "§eNext page ▶", "§7Page " + (session.page + 2) + "/" + totalPages)
                : item(Material.RED_STAINED_GLASS_PANE, "§c✗ Cancel", "§7Discards all changes"));

        // Rows 1-3: messages
        int start = session.page * MSGS_PER_PAGE;
        int end = Math.min(start + MSGS_PER_PAGE, session.messages.size());
        for (int i = start; i < end; i++) {
            inv.setItem(9 + (i - start), messageItem(i, session.messages.get(i)));
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof EditorHolder)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null
                || !(event.getClickedInventory().getHolder() instanceof EditorHolder)) return;

        GUISession session = sessions.get(player.getUniqueId());
        if (session == null) return;

        int slot = event.getSlot();
        boolean shift = event.isShiftClick();
        boolean right = event.isRightClick();

        switch (slot) {
            case 0 -> { if (session.page > 0) { session.page--; renderGUI(player, session); } }
            case 1 -> startChatInput(player, session, -1);
            case 7 -> { sessions.remove(player.getUniqueId()); player.closeInventory(); saveChanges(player, session); }
            case 8 -> {
                if (hasNextPage(session)) { session.page++; renderGUI(player, session); }
                else { sessions.remove(player.getUniqueId()); player.closeInventory();
                    player.sendMessage(Utils.colorize("<red>Editing cancelled.")); }
            }
            default -> {
                if (slot < 9) return;
                int idx = session.page * MSGS_PER_PAGE + (slot - 9);
                if (idx >= session.messages.size()) return;
                if (shift && !right && idx > 0) {
                    Collections.swap(session.messages, idx, idx - 1);
                    renderGUI(player, session);
                } else if (shift && right && idx < session.messages.size() - 1) {
                    Collections.swap(session.messages, idx, idx + 1);
                    renderGUI(player, session);
                } else if (right) {
                    session.messages.remove(idx);
                    if (session.page >= totalPages(session)) session.page = Math.max(0, totalPages(session) - 1);
                    renderGUI(player, session);
                } else {
                    startChatInput(player, session, idx);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof EditorHolder)) return;
        // Session already removed by Save/Cancel/startChatInput — no action needed
        sessions.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ChatSession chat = chatPending.remove(player.getUniqueId());
        if (chat == null) return;
        event.setCancelled(true);

        String text = PlainTextComponentSerializer.plainText().serialize(event.message());

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (text.equalsIgnoreCase("cancel")) {
                player.sendMessage(Utils.colorize("<gray>Input cancelled."));
            } else {
                if (chat.editingIndex >= 0) chat.messages.set(chat.editingIndex, text);
                else chat.messages.add(text);
                player.sendMessage(Utils.colorize("<green>✔ Message set."));
            }
            int targetPage = chat.editingIndex >= 0
                    ? chat.editingIndex / MSGS_PER_PAGE
                    : Math.max(0, (chat.messages.size() - 1) / MSGS_PER_PAGE);
            GUISession session = new GUISession(chat.commandName, chat.isNew, chat.messages, targetPage);
            sessions.put(player.getUniqueId(), session);
            renderGUI(player, session);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        sessions.remove(uid);
        chatPending.remove(uid);
    }

    private void startChatInput(Player player, GUISession session, int editingIndex) {
        sessions.remove(player.getUniqueId());
        chatPending.put(player.getUniqueId(),
                new ChatSession(session.commandName, session.isNew, session.messages, editingIndex));
        player.closeInventory();

        player.sendMessage(Utils.colorize("<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        if (editingIndex >= 0) {
            player.sendMessage(Utils.colorize("<yellow>Editing message #" + (editingIndex + 1) + ":"));
            player.sendMessage(Utils.colorize("<white>" + session.messages.get(editingIndex)));
        } else {
            player.sendMessage(Utils.colorize("<green>Type the new message:"));
        }
        player.sendMessage(Utils.colorize("<gray>Supports <white>&codes</white> and <white><MiniMessage></white> tags."));
        player.sendMessage(Utils.colorize("<gray>Type <red>cancel</red> to abort."));
        player.sendMessage(Utils.colorize("<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
    }

    private void saveChanges(Player player, GUISession session) {
        if (session.messages.isEmpty()) {
            player.sendMessage(Utils.colorize("<red>✘ A command needs at least one message. Changes discarded."));
            return;
        }
        String cmd = session.commandName;
        plugin.getConfig().set("commands." + cmd + ".messages", session.messages);
        if (session.isNew) {
            plugin.getConfig().set("commands." + cmd + ".description", "Informational command");
            plugin.getConfig().set("commands." + cmd + ".aliases", List.of());
            plugin.getConfig().set("commands." + cmd + ".cooldown", 0);
            plugin.getConfig().set("commands." + cmd + ".permission", "");
        }
        plugin.saveConfig();
        if (session.isNew) {
            plugin.registerSingleCommand(cmd);
            player.sendMessage(Utils.colorize("<green>✔ Created command <yellow>/" + cmd + "<green>!"));
        } else {
            plugin.unregisterSingleCommand(cmd);
            plugin.registerSingleCommand(cmd);
            player.sendMessage(Utils.colorize("<green>✔ Updated command <yellow>/" + cmd + "<green>!"));
        }
    }

    private int totalPages(GUISession s) {
        return Math.max(1, (int) Math.ceil(s.messages.size() / (double) MSGS_PER_PAGE));
    }

    private boolean hasNextPage(GUISession s) {
        return (s.page + 1) * MSGS_PER_PAGE < s.messages.size();
    }

    private ItemStack item(Material mat, String name, String... lore) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.displayName(Utils.colorize(name));
        if (lore.length > 0)
            meta.lore(Arrays.stream(lore).map(Utils::colorize).toList());
        is.setItemMeta(meta);
        return is;
    }

    private ItemStack messageItem(int idx, String raw) {
        String preview = raw.length() > 42 ? raw.substring(0, 39) + "§8..." : raw;
        ItemStack is = new ItemStack(Material.PAPER, Math.min(idx + 1, 64));
        ItemMeta meta = is.getItemMeta();
        meta.displayName(Utils.colorize("§f#" + (idx + 1) + " " + preview));
        meta.lore(List.of(
                Utils.colorize("§8" + raw),
                Utils.colorize(""),
                Utils.colorize("§7Left-click §fto edit"),
                Utils.colorize("§7Right-click §cto delete"),
                Utils.colorize("§7Shift+Left §fmove up  §7Shift+Right §fmove down")
        ));
        is.setItemMeta(meta);
        return is;
    }

    static class GUISession {
        final String commandName;
        final boolean isNew;
        final List<String> messages;
        int page;
        GUISession(String c, boolean n, List<String> m, int p) { commandName=c; isNew=n; messages=m; page=p; }
    }

    record ChatSession(String commandName, boolean isNew, List<String> messages, int editingIndex) {}

    static class EditorHolder implements InventoryHolder {
        private Inventory inventory;
        void setInventory(Inventory inv) { this.inventory = inv; }
        @Override public @org.jetbrains.annotations.NotNull Inventory getInventory() { return inventory; }
    }
}
