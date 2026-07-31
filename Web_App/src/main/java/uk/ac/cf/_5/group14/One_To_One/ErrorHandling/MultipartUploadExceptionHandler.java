package uk.ac.cf._5.group14.One_To_One.ErrorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
public class MultipartUploadExceptionHandler {

    private static final String LIMIT_MESSAGE =
            "Upload too large. Each file must be 8 MB or smaller and the total request must be 25 MB or smaller.";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceeded() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of(
                        "error", LIMIT_MESSAGE,
                        "message", LIMIT_MESSAGE));
    }
}
