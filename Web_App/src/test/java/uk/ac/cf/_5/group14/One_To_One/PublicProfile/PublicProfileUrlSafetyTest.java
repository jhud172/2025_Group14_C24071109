package uk.ac.cf._5.group14.One_To_One.PublicProfile;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PublicProfileUrlSafetyTest {

    @Test
    void allowsOnlyAbsoluteHttpLinksWithoutEmbeddedCredentials() {
        assertThat(PublicProfileController.safeExternalUrl("https://example.com/coaching?ref=profile"))
                .isEqualTo("https://example.com/coaching?ref=profile");
        assertThat(PublicProfileController.safeExternalUrl("http://example.com"))
                .isEqualTo("http://example.com");

        assertThat(PublicProfileController.safeExternalUrl("javascript:alert(1)"))
                .isNull();
        assertThat(PublicProfileController.safeExternalUrl("data:text/html,test"))
                .isNull();
        assertThat(PublicProfileController.safeExternalUrl("/relative-profile"))
                .isNull();
        assertThat(PublicProfileController.safeExternalUrl("https://user:secret@example.com"))
                .isNull();
        assertThat(PublicProfileController.safeExternalUrl("not a url"))
                .isNull();
    }
}
