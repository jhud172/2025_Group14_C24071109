package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Level.LevelService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

/**
 * Web controller for managing notes and folders.
 */
@Controller
@RequestMapping("/notes")
public class NotesController {

    private final NoteFolderService folderService;
    private final NoteService noteService;
    private final AuthHelper authHelper;
    private final LevelService levelService;

    public NotesController(NoteFolderService folderService,
                           NoteService noteService,
                           AuthHelper authHelper,
                           LevelService levelService) {
        this.folderService = folderService;
        this.noteService = noteService;
        this.authHelper = authHelper;
        this.levelService = levelService;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        User user = authHelper.getAuthenticatedUser(session);
        model.addAttribute("folders", folderService.getFoldersForUser(user));
        return "notes/folders";
    }

    @GetMapping("/folders/{id}")
    public String folderView(@PathVariable Long id,
                             @RequestParam(required = false) String q,
                             HttpSession session,
                             Model model) {
        User user = authHelper.getAuthenticatedUser(session);
        model.addAttribute("folders", folderService.getFoldersForUser(user));
        NoteFolder activeFolder = folderService.getFolderForUser(user, id);
        model.addAttribute("activeFolder", activeFolder);
        model.addAttribute("notes", noteService.getNotesForFolder(user, id, q));
        model.addAttribute("q", q);
        return "notes/folders";
    }

    @PostMapping("/folders/new")
    public String createFolder(@RequestParam String name,
                               @RequestParam(required = false) String colour,
                               HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        folderService.createFolder(user, name, colour);
        return "redirect:/notes";
    }

    @PostMapping("/folders/{id}/rename")
    public String renameFolder(@PathVariable Long id,
                               @RequestParam String name,
                               HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        folderService.renameFolder(user, id, name);
        return "redirect:/notes/folders/" + id;
    }

    @PostMapping("/folders/{id}/delete")
    public String deleteFolder(@PathVariable Long id, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        folderService.deleteFolder(user, id);
        return "redirect:/notes";
    }

    @GetMapping("/folders/{folderId}/new")
    public String newNote(@PathVariable Long folderId,
                          Model model,
                          HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        model.addAttribute("folders", folderService.getFoldersForUser(user));
        NoteFolder folder = folderService.getFolderForUser(user, folderId);
        model.addAttribute("folder", folder);
        model.addAttribute("note", new Note());
        return "notes/note-form";
    }

    @PostMapping("/folders/{folderId}/new")
    public String createNote(@PathVariable Long folderId,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(value="noteColour", required=false) String noteColour,
                             HttpSession session) {

        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        Note note = noteService.create(user, folderId, title, content, noteColour);
        levelService.addPoints(user, 5);

        return "redirect:/notes/" + note.getId();
    }

    @GetMapping("/{id}")
    public String viewNote(@PathVariable Long id,
                           Model model,
                           HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.getNoteForUser(user, id);
        model.addAttribute("note", note);
        return "notes/note-view";
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id,
                               Model model,
                               HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.getNoteForUser(user, id);
        model.addAttribute("note", note);
        model.addAttribute("folder", note.getFolder());
        return "notes/note-form";
    }

    @PostMapping("/{id}/edit")
    public String updateNote(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(value="noteColour", required=false) String noteColour,
                             @RequestParam(required = false) Long folderId,
                             HttpSession session) {

        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) return "redirect:/login";

        noteService.update(user, id, title, content, folderId, noteColour);
        levelService.addPoints(user, 2);

        return "redirect:/notes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable Long id,
                             HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        noteService.delete(user, id);
        return "redirect:/notes";
    }
}
