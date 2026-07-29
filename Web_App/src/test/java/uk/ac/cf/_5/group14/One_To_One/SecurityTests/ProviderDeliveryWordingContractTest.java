package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderDeliveryWordingContractTest {

    @Test
    void synchronousEmailFlowsDoNotClaimQueuedOrConfirmedDelivery() throws IOException {
        String membershipController = Files.readString(Path.of(
                "src/main/java/uk/ac/cf/_5/group14/One_To_One/Membership/GymAdminMembershipController.java"
        ));
        String verificationController = Files.readString(Path.of(
                "src/main/java/uk/ac/cf/_5/group14/One_To_One/Verification/SuperAdminVerificationController.java"
        ));

        assertThat(membershipController)
                .doesNotContain("Notification queued")
                .contains("Email delivery was attempted immediately; delivery is not tracked.");
        assertThat(verificationController)
                .doesNotContain("Notification queued")
                .contains("Email delivery was attempted immediately; delivery is not tracked.");
    }
}
