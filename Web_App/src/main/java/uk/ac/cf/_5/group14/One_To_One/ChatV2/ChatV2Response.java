package uk.ac.cf._5.group14.One_To_One.ChatV2;

import java.util.List;

public record ChatV2Response(String assistantText, List<ChatV2Block> blocks, List<ChatV2ActionResult> actions) {
}
