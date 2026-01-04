package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;

/**
 * Represents a freeform note that belongs to a user and lives within a folder.
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "folder_id")
    private NoteFolder folder;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "colour", nullable = false)
    private String colour = "slate";

    public String getColour() {return colour;}

    public void setColour(String colour) {
        if (colour == null || colour.isBlank()) {
            this.colour = "slate";
            return;
        }

        String c = colour.toLowerCase().trim();
        switch (c) {
            case "slate", "purple", "cyan", "green", "pink", "orange", "red" -> this.colour = c;
            default -> this.colour = "slate";
        }
    }

    @Column(nullable = false)
    private Boolean pinned = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
