package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;

/**
 * Implementation of note folder business logic.
 */
@Service
@Transactional
public class NoteFolderServiceImpl implements NoteFolderService {

    private final NoteFolderRepository folderRepository;

    public NoteFolderServiceImpl(NoteFolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public List<NoteFolder> getFoldersForUser(User user) {
        return folderRepository.findByUserOrderByNameAsc(user);
    }

    @Override
    public NoteFolder createFolder(User user, String name, String colour) {
        NoteFolder folder = new NoteFolder();
        folder.setUser(user);
        folder.setName(name);
        folder.setColour(colour != null ? colour : "slate");
        return folderRepository.save(folder);
    }

    @Override
    public NoteFolder renameFolder(User user, Long folderId, String newName) {
        NoteFolder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        folder.setName(newName);
        return folderRepository.save(folder);
    }

    @Override
    public void deleteFolder(User user, Long folderId) {
        NoteFolder folder = folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        folderRepository.delete(folder);
    }

    @Override
    public NoteFolder getFolderForUser(User user, Long folderId) {
        return folderRepository.findByIdAndUser(folderId, user)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
    }
}
