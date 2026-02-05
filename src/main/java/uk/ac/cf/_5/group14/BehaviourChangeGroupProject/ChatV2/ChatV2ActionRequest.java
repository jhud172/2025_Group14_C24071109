package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import java.util.Map;

public record ChatV2ActionRequest(String type, Map<String, Object> payload) {
}
