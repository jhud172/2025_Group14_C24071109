package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PDF_Tests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.ExerciseLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog.PdfService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceTest {

    @Test
    void generateLogsPdf_ShouldReturnNonEmptyByteArray() {
        PdfService pdfService = new PdfService();
        ExerciseLog log1 = new ExerciseLog();
        log1.setDate(LocalDate.now());
        log1.setMoodBefore(1);
        log1.setMoodAfter(2);
        log1.setConfidence(3);
        log1.setComments("Test");
        ExerciseLog log2 = new ExerciseLog();
        log2.setDate(LocalDate.now().minusDays(1));
        log2.setMoodBefore(2);
        log2.setMoodAfter(3);
        log2.setConfidence(4);
        log2.setComments("");
        List<ExerciseLog> logs = Arrays.asList(log1, log2);
        byte[] pdfBytes = pdfService.generateLogsPdf(logs);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}