package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

public interface NoteService {

    Note create(User user, Long folderId, String title, String content, String noteColour);

    Note update(User user, Long noteId, String title, String content, Long newFolderId, String noteColour);

    void delete(User user, Long noteId);

    Note getNoteForUser(User user, Long noteId);

    List<Note> getNotesForFolder(User user, Long folderId, String query);
}
