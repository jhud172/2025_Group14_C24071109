package uk.ac.cf._5.group14.One_To_One.ChatV2;

import java.util.Map;

public record ChatV2ActionResult(String type, Map<String, Object> payload, String status, String message) {
}
