package dc.marcin0816.chatInfoCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    /**
     * Konwertuje tekst z kodami kolorów na komponent tekstowy
     * @param text Tekst z kodami kolorów
     * @return Komponent tekstowy
     */
    public static Component colorize(String text) {
        if (text == null) return Component.empty();
        return legacySerializer.deserialize(text);
    }
}