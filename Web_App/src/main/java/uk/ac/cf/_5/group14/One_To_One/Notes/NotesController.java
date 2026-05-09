package uk.ac.cf._5.group14.One_To_One.Notes;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.One_To_One.Level.LevelService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (user == null) {
            return "redirect:/login";
        }
        folderService.ensureDefaults(user);
        List<NoteFolder> folders = folderService.getFoldersForUser(user);
        model.addAttribute("folders", folders);

        Long activeFolderId = folders.isEmpty() ? null : folders.get(0).getId();
        model.addAttribute("activeFolderId", activeFolderId);
        model.addAttribute("notes", activeFolderId != null ? noteService.search(user, activeFolderId, null) : List.of());
        model.addAttribute("activeNote", null);
        return "shared-views/notes/index";
    }

    @GetMapping(params = {"folderId"})
    public String indexFolder(@RequestParam Long folderId,
                              @RequestParam(required = false) String q,
                              @RequestParam(required = false) Long noteId,
                              HttpSession session,
                              Model model) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        folderService.ensureDefaults(user);
        List<NoteFolder> folders = folderService.getFoldersForUser(user);
        NoteFolder activeFolder = folderService.getFolderForUser(user, folderId);
        List<Note> notes = noteService.search(user, folderId, q);
        Note activeNote = null;
        if (noteId != null) {
            activeNote = noteService.getNoteForUser(user, noteId);
        } else if (!notes.isEmpty()) {
            activeNote = notes.get(0);
        }

        model.addAttribute("folders", folders);
        model.addAttribute("activeFolderId", activeFolder.getId());
        model.addAttribute("notes", notes);
        model.addAttribute("activeNote", activeNote);
        model.addAttribute("q", q);
        return "shared-views/notes/index";
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
        return "shared-views/notes/folders";
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
        return "shared-views/notes/note-form";
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
        return "shared-views/notes/note-view";
    }

    @GetMapping("/{id}/edit")
    public String editNoteForm(@PathVariable Long id,
                               Model model,
                               HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.getNoteForUser(user, id);
        model.addAttribute("note", note);
        model.addAttribute("folder", note.getFolder());
        return "shared-views/notes/note-form";
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

    // -------- Notes v2 API --------

    @GetMapping("/api/folders")
    @ResponseBody
    public List<NoteFolderDto> listFolders(HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) {
            return List.of();
        }
        folderService.ensureDefaults(user);
        return folderService.getFoldersForUser(user).stream()
                .map(NoteFolderDto::from)
                .toList();
    }

    @PostMapping("/api/folders")
    @ResponseBody
    public NoteFolderDto createFolder(@RequestBody NoteFolderCreateRequest request, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        NoteFolder folder = folderService.createFolder(user, request.getName(), request.getColour());
        return NoteFolderDto.from(folder);
    }

    @PostMapping("/api/folders/{id}/rename")
    @ResponseBody
    public NoteFolderDto renameFolder(@PathVariable Long id,
                                      @RequestBody NoteFolderRenameRequest request,
                                      HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        NoteFolder folder = folderService.renameFolder(user, id, request.getName());
        return NoteFolderDto.from(folder);
    }

    @DeleteMapping("/api/folders/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteFolderApi(@PathVariable Long id, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        folderService.deleteFolder(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/notes")
    @ResponseBody
    public List<NoteSummaryDto> listNotes(@RequestParam(required = false) Long folderId,
                                          @RequestParam(required = false) String q,
                                          HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        if (user == null) {
            return List.of();
        }
        return noteService.search(user, folderId, q).stream()
                .map(NoteSummaryDto::from)
                .toList();
    }

    @GetMapping("/api/notes/{id}")
    @ResponseBody
    public NoteDetailDto getNote(@PathVariable Long id, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.getNoteForUser(user, id);
        return NoteDetailDto.from(note);
    }

    @PostMapping("/api/notes")
    @ResponseBody
    public NoteDetailDto createNote(@RequestBody NoteCreateRequest request, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.create(user, request.getFolderId(), request.getTitle(), request.getContent(), request.getColour());
        levelService.addPoints(user, 2);
        return NoteDetailDto.from(note);
    }

    @PostMapping("/api/notes/{id}")
    @ResponseBody
    public NoteDetailDto updateNote(@PathVariable Long id,
                                    @RequestBody NoteUpdateRequest request,
                                    HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.update(user, id, request.getTitle(), request.getContent(), request.getFolderId(), request.getColour());
        return NoteDetailDto.from(note);
    }

    @DeleteMapping("/api/notes/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNoteApi(@PathVariable Long id, HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        noteService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> exportNote(@PathVariable Long id,
                                             @RequestParam(defaultValue = "html") String format,
                                             HttpSession session) {
        User user = authHelper.getAuthenticatedUser(session);
        Note note = noteService.getNoteForUser(user, id);

        String safeTitle = note.getTitle() != null ? note.getTitle().trim().replaceAll("[^a-zA-Z0-9-_ ]", "") : "note";
        String filename = safeTitle.isBlank() ? "note" : safeTitle;

        if (!"html".equalsIgnoreCase(format)) {
            format = "html";
        }

        String html = """
                <!doctype html>
                <html lang=\"en\">
                <head><meta charset=\"utf-8\"><title>%s</title></head>
                <body><h1>%s</h1><article>%s</article></body>
                </html>
                """.formatted(escapeHtml(note.getTitle()), escapeHtml(note.getTitle()), note.getContent());

        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".html\"")
                .contentType(MediaType.TEXT_HTML)
                .contentLength(bytes.length)
                .body(bytes);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
