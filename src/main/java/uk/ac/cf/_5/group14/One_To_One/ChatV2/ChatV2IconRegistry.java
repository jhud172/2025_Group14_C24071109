package uk.ac.cf._5.group14.One_To_One.ChatV2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChatV2IconRegistry {

    private static final Map<String, String> ICONS = new LinkedHashMap<>();

    static {
        ICONS.put("sparkles", "âœ¨");
        ICONS.put("bolt", "âš¡");
        ICONS.put("leaf", "ðŸŒ¿");
        ICONS.put("calendar", "ðŸ“…");
        ICONS.put("note", "ðŸ“");
        ICONS.put("target", "ðŸŽ¯");
        ICONS.put("chat", "ðŸ’¬");
        ICONS.put("heart", "â¤ï¸");
    }

    private ChatV2IconRegistry() {
    }

    public static List<String> keys() {
        return List.copyOf(ICONS.keySet());
    }

    public static Map<String, String> iconMap() {
        return Map.copyOf(ICONS);
    }

    public static String iconFor(String key) {
        return ICONS.getOrDefault(key, "ðŸ’¬");
    }
}
