package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

/**
 * Business logic for managing note folders.
 */
public interface NoteFolderService {
    List<NoteFolder> getFoldersForUser(User user);

    NoteFolder createFolder(User user, String name, String colour);

    NoteFolder renameFolder(User user, Long folderId, String newName);

    void deleteFolder(User user, Long folderId);

    NoteFolder getFolderForUser(User user, Long folderId);
}
