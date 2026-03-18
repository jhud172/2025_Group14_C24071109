package uk.ac.cf._5.group14.One_To_One.Notes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.List;

/**
 * Implementation of note business logic.
 */
@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteFolderRepository folderRepository;
    private final NoteSanitizer noteSanitizer;

    public NoteServiceImpl(NoteRepository noteRepository,
                           NoteFolderRepository folderRepository,
                           NoteSanitizer noteSanitizer) {
        this.noteRepository = noteRepository;
        this.folderRepository = folderRepository;
        this.noteSanitizer = noteSanitizer;
    }

    // Backwards-compatible wrapper used by older tests
    public Note createNote(User user, Long folderId, String title, String content, boolean isPublic) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title required");
        }
        Note note = create(user, folderId, title, content, null);
        note.setPublic(isPublic);
        return noteRepository.save(note);
    }

    // Backwards-compatible wrapper used by older tests
    public Note updateNote(User user, Long noteId, String title, String content, boolean isPublic) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        note.setTitle(title);
        note.setContent(content);
        note.setPublic(isPublic);
        return noteRepository.save(note);
    }

    @Override
    public Note create(User user, Long folderId, String title, String content, String noteColour) {
        NoteFolder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        Note note = new Note();
        note.setUser(user);
        note.setFolder(folder);
        note.setTitle(title);
        note.setContent(noteSanitizer.sanitize(content));
        note.setColour(noteColour);
        return noteRepository.save(note);
    }

    @Override
    public Note update(User user, Long noteId, String title, String content, Long newFolderId, String noteColour) {
        Note note = noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        note.setTitle(title);
        note.setContent(noteSanitizer.sanitize(content));
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
        return noteRepository.searchNotes(user, folder, query);
    }

    @Override
    public List<Note> search(User user, Long folderId, String query) {
        NoteFolder folder = null;
        if (folderId != null) {
            folder = folderRepository.findByIdAndUser(folderId, user)
                    .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        }
        return noteRepository.searchNotes(user, folder, query);
    }
}
