package dc.marcin0816.chatInfoCommand;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

// Isolated in a separate class so the PAPI classes are only loaded
// when this class is actually used (guarded by isPluginEnabled check).
public class PAPIHook {

    public static String setPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
