package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseLog;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
public class PdfService {
    private String moodLabel(Integer mood) {
        if (mood == null) return "—";
        return switch (mood) {
            case 1 -> "Very Low";
            case 2 -> "Low";
            case 3 -> "Neutral";
            case 4 -> "Good";
            case 5 -> "Excellent";
            default -> "—";
        };
    }

    public byte[] generateLogsPdf(List<ExerciseLog> logs) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("Your Exercise Logs", titleFont);
            title.setSpacingAfter(20);
            document.add(title);
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addHeader(table, "Date");
            addHeader(table, "Mood Before");
            addHeader(table, "Mood After");
            addHeader(table, "Confidence");
            addHeader(table, "Notes");
            for (ExerciseLog log : logs) {
                table.addCell(log.getDate().toString());
                table.addCell(moodLabel(log.getMoodBefore()));
                table.addCell(moodLabel(log.getMoodAfter()));
                table.addCell(String.valueOf(log.getConfidence()));
                table.addCell(log.getComments() == null ? "" : log.getComments());
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void addHeader(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        table.addCell(cell);
    }
}