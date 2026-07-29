package uk.ac.cf._5.group14.One_To_One.StorageTests;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartUploadLimitsContractTest {

    @Test
    void defaultAndLocalProfilesAllowTheLargestAcceptedUploadAndChatBatch() throws Exception {
        assertLimits("application.properties");
        assertLimits("application-local.properties");
    }

    private void assertLimits(String resource) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Path.of("src/main/resources", resource))) {
            assertThat(input).as(resource).isNotNull();
            properties.load(input);
        }
        assertThat(properties.getProperty("spring.servlet.multipart.max-file-size")).isEqualTo("8MB");
        assertThat(properties.getProperty("spring.servlet.multipart.max-request-size")).isEqualTo("25MB");
        assertThat(properties.getProperty("server.tomcat.max-swallow-size")).isEqualTo("32MB");
    }
}
