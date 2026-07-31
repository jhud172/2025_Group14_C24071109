package uk.ac.cf._5.group14.One_To_One.StorageTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.dev-mode=true",
                "spring.servlet.multipart.max-file-size=8MB",
                "spring.servlet.multipart.max-request-size=25MB",
                "server.tomcat.max-swallow-size=32MB"
        })
class MultipartUploadRejectionIntegrationTest {

    private static final int MEBIBYTE = 1024 * 1024;
    private static final String LIMIT_MESSAGE =
            "Upload too large. Each file must be 8 MB or smaller and the total request must be 25 MB or smaller.";

    @LocalServerPort
    int port;

    @Test
    void acceptsAFileAtTheConfiguredEightMiBLimit() throws Exception {
        HttpResponse<String> response = postFiles(List.of(bytes("accepted.mp4", 8 * MEBIBYTE)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void rejectsAFileAboveEightMiBWithSafePayloadTooLargeResponse() throws Exception {
        HttpResponse<String> response = postFiles(List.of(bytes("too-large.mp4", (8 * MEBIBYTE) + 1)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE.value());
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE))
                .hasValueSatisfying(type -> assertThat(type).startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertThat(response.body()).contains(LIMIT_MESSAGE);
        assertThat(response.body()).doesNotContain("SizeLimitExceededException", "stackTrace");
    }

    @Test
    void rejectsARequestAboveTwentyFiveMiBWhenEveryFileIsBelowTheFileLimit() throws Exception {
        HttpResponse<String> response = postFiles(List.of(
                bytes("one.bin", 7 * MEBIBYTE),
                bytes("two.bin", 6 * MEBIBYTE),
                bytes("three.bin", 6 * MEBIBYTE),
                bytes("four.bin", 6 * MEBIBYTE)));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE.value());
        assertThat(response.headers().firstValue(HttpHeaders.CONTENT_TYPE))
                .hasValueSatisfying(type -> assertThat(type).startsWith(MediaType.APPLICATION_JSON_VALUE));
        assertThat(response.body()).contains(LIMIT_MESSAGE);
        assertThat(response.body()).doesNotContain("SizeLimitExceededException", "stackTrace");
    }

    private HttpResponse<String> postFiles(List<UploadPart> files) throws Exception {
        String boundary = "one-to-one-multipart-limit-boundary";
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (UploadPart file : files) {
            writeAscii(body, "--" + boundary + "\r\n");
            writeAscii(body, "Content-Disposition: form-data; name=\"files\"; filename=\"" + file.filename() + "\"\r\n");
            writeAscii(body, "Content-Type: application/octet-stream\r\n\r\n");
            body.write(file.bytes());
            writeAscii(body, "\r\n");
        }
        writeAscii(body, "--" + boundary + "--\r\n");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/mobile/multipart-limit-probe"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE + "; boundary=" + boundary)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void writeAscii(ByteArrayOutputStream output, String text) {
        output.writeBytes(text.getBytes(StandardCharsets.US_ASCII));
    }

    private UploadPart bytes(String filename, int size) {
        return new UploadPart(filename, new byte[size]);
    }

    @TestConfiguration
    static class ProbeConfiguration {

        @Bean
        MultipartLimitProbeController multipartLimitProbeController() {
            return new MultipartLimitProbeController();
        }
    }

    @RestController
    @RequestMapping("/api/mobile/multipart-limit-probe")
    static class MultipartLimitProbeController {

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        ResponseEntity<Void> accept(@RequestParam("files") List<MultipartFile> files) {
            return ResponseEntity.noContent().build();
        }
    }

    private record UploadPart(String filename, byte[] bytes) {
    }
}
