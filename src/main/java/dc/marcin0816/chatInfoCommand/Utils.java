package dc.marcin0816.chatInfoCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class Utils {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    // Detects MiniMessage tags like <red>, <gold>, <gradient:...>, <click:...>, <#FF5500>
    private static final Pattern MINI_MESSAGE_PATTERN = Pattern.compile("<[a-zA-Z#/!][^>]*>");

    public static Component colorize(String text) {
        if (text == null) return Component.empty();
        if (MINI_MESSAGE_PATTERN.matcher(text).find()) {
            return MINI_MESSAGE.deserialize(text);
        }
        return LEGACY.deserialize(text);
    }
}
