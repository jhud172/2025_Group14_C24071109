package uk.ac.cf._5.group14.BehaviourChangeGroupProject.NotesTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolder;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolderRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolderServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteFolderServiceImplTest {

    @Mock
    private NoteFolderRepository folderRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteFolderServiceImpl service;

    @Test
    void ensureDefaults_createsMissingFolders() {
        User user = new User();
        user.setId(1L);

        when(folderRepository.findByUserAndNameIgnoreCase(any(), anyString())).thenReturn(Optional.empty());
        when(folderRepository.findByUserOrderBySortOrderAscNameAsc(any())).thenReturn(List.of());

        service.ensureDefaults(user);

        verify(folderRepository, atLeast(3)).save(any(NoteFolder.class));
    }

    @Test
    void renameFolder_blocksUnsortedRename() {
        User user = new User();
        user.setId(1L);

        NoteFolder unsorted = new NoteFolder();
        unsorted.setId(10L);
        unsorted.setUser(user);
        unsorted.setName("Unsorted");

        when(folderRepository.findByIdAndUser(eq(10L), eq(user))).thenReturn(Optional.of(unsorted));

        assertThrows(IllegalArgumentException.class, () -> service.renameFolder(user, 10L, "Whatever"));
    }
}
