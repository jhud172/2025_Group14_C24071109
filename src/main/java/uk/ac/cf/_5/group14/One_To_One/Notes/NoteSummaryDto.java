package uk.ac.cf._5.group14.One_To_One.Notes;

import java.time.LocalDateTime;

public class NoteSummaryDto {
    private Long id;
    private String title;
    private Long folderId;
    private String preview;
    private LocalDateTime updatedAt;

    public static NoteSummaryDto from(Note note) {
        NoteSummaryDto dto = new NoteSummaryDto();
        dto.id = note.getId();
        dto.title = note.getTitle();
        dto.folderId = note.getFolder() != null ? note.getFolder().getId() : null;
        dto.preview = NoteTextPreview.build(note.getContent());
        dto.updatedAt = note.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getPreview() {
        return preview;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
