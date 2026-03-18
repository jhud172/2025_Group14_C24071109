package uk.ac.cf._5.group14.One_To_One.Notes;

import java.time.LocalDateTime;

public class NoteDetailDto {
    private Long id;
    private String title;
    private String content;
    private Long folderId;
    private String colour;
    private LocalDateTime updatedAt;

    public static NoteDetailDto from(Note note) {
        NoteDetailDto dto = new NoteDetailDto();
        dto.id = note.getId();
        dto.title = note.getTitle();
        dto.content = note.getContent();
        dto.folderId = note.getFolder() != null ? note.getFolder().getId() : null;
        dto.colour = note.getColour();
        dto.updatedAt = note.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getFolderId() {
        return folderId;
    }

    public String getColour() {
        return colour;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
