package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

/**
 * Implementation of note business logic.
 */
@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteFolderRepository folderRepository;

    public NoteServiceImpl(NoteRepository noteRepository, NoteFolderRepository folderRepository) {
        this.noteRepository = noteRepository;
        this.folderRepository = folderRepository;
    }

    @Override
    public Note create(User user, Long folderId, String title, String content, String noteColour) {
        NoteFolder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        Note note = new Note();
        note.setUser(user);
        note.setFolder(folder);
        note.setTitle(title);
        note.setContent(content);
        note.setColour(noteColour);
        return noteRepository.save(note);
    }

    @Override
    public Note update(User user, Long noteId, String title, String content, Long newFolderId, String noteColour) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        note.setTitle(title);
        note.setContent(content);
        note.setColour(noteColour);
        if (newFolderId != null && !note.getFolder().getId().equals(newFolderId)) {
            NoteFolder newFolder = folderRepository.findByIdAndUser(newFolderId, user)
                    .orElseThrow(() -> new IllegalArgumentException("Target folder not found"));
            note.setFolder(newFolder);
        }
        return noteRepository.save(note);
    }

    @Override
    public void delete(User user, Long noteId) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        noteRepository.delete(note);
    }

    @Override
    public Note getNoteForUser(User user, Long noteId) {
        return noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
    }

    @Override
    public List<Note> getNotesForFolder(User user, Long folderId, String query) {
        NoteFolder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        List<Note> notes = noteRepository.findByFolderOrderByUpdatedAtDesc(folder);
        if (query == null || query.isBlank()) {
            return notes;
        }
        String qLower = query.toLowerCase();
        return notes.stream()
                .filter(n -> (n.getTitle() != null && n.getTitle().toLowerCase().contains(qLower))
                        || (n.getContent() != null && n.getContent().toLowerCase().contains(qLower)))
                .toList();
    }
}
