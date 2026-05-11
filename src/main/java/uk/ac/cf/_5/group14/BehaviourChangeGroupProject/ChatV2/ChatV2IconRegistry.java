package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ChatV2IconRegistry {

    private static final Map<String, String> ICONS = new LinkedHashMap<>();

    static {
        ICONS.put("sparkles", "✨");
        ICONS.put("bolt", "⚡");
        ICONS.put("leaf", "🌿");
        ICONS.put("calendar", "📅");
        ICONS.put("note", "📝");
        ICONS.put("target", "🎯");
        ICONS.put("chat", "💬");
        ICONS.put("heart", "❤️");
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
        return ICONS.getOrDefault(key, "💬");
    }
}
