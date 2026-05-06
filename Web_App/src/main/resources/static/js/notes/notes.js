document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("notes-app");
    if (!root) {
        return;
    }

    const csrfHeader = root.dataset.csrfHeader || "X-CSRF-TOKEN";
    const csrfToken = root.dataset.csrfToken || "";

    const addFolderBtn = document.getElementById("addFolderBtn");
    const newFolderRow = document.getElementById("newFolderRow");
    const newFolderName = document.getElementById("newFolderName");
    const createFolderBtn = document.getElementById("createFolderBtn");
    const cancelFolderBtn = document.getElementById("cancelFolderBtn");
    const folderList = document.getElementById("folderList");
    const folderSearch = document.getElementById("folderSearch");
    const noteList = document.getElementById("noteList");
    const noteSearch = document.getElementById("noteSearch");
    const addNoteBtn = document.getElementById("addNoteBtn");
    const noteTitle = document.getElementById("noteTitle");
    const noteMeta = document.getElementById("noteMeta");
    const saveStatus = document.getElementById("saveStatus");
    const deleteNoteBtn = document.getElementById("deleteNoteBtn");
    const exportHtmlBtn = document.getElementById("exportHtmlBtn");

    if (!folderList || !noteList || !noteTitle || !saveStatus || !window.Quill) {
        return;
    }

    let activeFolderId = root.dataset.activeFolder || "";
    let activeNoteId = root.dataset.activeNote || "";
    let autosaveTimer = null;
    let isSaving = false;

    const quill = new Quill("#editor", {
        theme: "snow",
        modules: {
            toolbar: [
                [{ header: [1, 2, 3, false] }],
                ["bold", "italic", "underline"],
                [{ list: "ordered" }, { list: "bullet" }],
                ["link", "blockquote", "code-block"],
                ["clean"]
            ]
        }
    });

    const activeContent = document.getElementById("notes-active-content");
    if (activeContent && activeContent.value) {
        quill.clipboard.dangerouslyPasteHTML(activeContent.value);
    }

    const setStatus = (text, tone) => {
        saveStatus.textContent = text;
        saveStatus.classList.remove("text-emerald-600", "text-rose-600", "text-slate-600");
        saveStatus.classList.add(tone || "text-slate-600");
    };

    const request = async (url, options = {}) => {
        const headers = Object.assign({ "Content-Type": "application/json" }, options.headers || {});
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        const response = await fetch(url, { ...options, headers });
        if (!response.ok) {
            throw new Error("Request failed");
        }
        if (response.status === 204) {
            return null;
        }
        return response.json();
    };

    const renderFolders = (folders) => {
        folderList.innerHTML = "";
        folders.forEach((folder) => {
            const btn = document.createElement("button");
            btn.type = "button";
            btn.className = "folder-item flex w-full items-center gap-3 rounded-xl border border-transparent px-3 py-2 text-left text-sm font-semibold text-slate-700 hover:bg-slate-50 dark:text-slate-200 dark:hover:bg-slate-900/40";
            if (String(folder.id) === String(activeFolderId)) {
                btn.classList.add("bg-slate-900", "text-white", "hover:bg-slate-900", "dark:bg-slate-100", "dark:text-slate-900");
            }
            btn.dataset.folderId = folder.id;
            btn.innerHTML = `
                <span class="h-2.5 w-2.5 rounded-full ring-4 ring-slate-100 dark:ring-slate-900 bg-slate-400"></span>
                <span class="truncate">${folder.name}</span>
            `;
            btn.addEventListener("click", () => selectFolder(folder.id));
            folderList.appendChild(btn);
        });
    };

    const renderNotes = (notes) => {
        noteList.innerHTML = "";
        if (!notes.length) {
            const empty = document.createElement("div");
            empty.className = "rounded-2xl border border-dashed border-slate-200 bg-white px-4 py-6 text-center text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-950/40";
            empty.textContent = "No notes yet.";
            noteList.appendChild(empty);
            return;
        }
        notes.forEach((note) => {
            const btn = document.createElement("button");
            btn.type = "button";
            btn.className = "note-item w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-left shadow-sm transition hover:border-slate-300 hover:shadow-md dark:border-slate-800 dark:bg-slate-950/50";
            if (String(note.id) === String(activeNoteId)) {
                btn.classList.add("ring-2", "ring-slate-900/20", "dark:ring-slate-100/30");
            }
            btn.dataset.noteId = note.id;
            btn.innerHTML = `
                <div class="flex items-center justify-between gap-2">
                    <span class="truncate text-sm font-semibold text-slate-900 dark:text-slate-100">${note.title || "Untitled"}</span>
                    <span class="text-[10px] text-slate-400">${note.updatedAt ? new Date(note.updatedAt).toLocaleDateString() : ""}</span>
                </div>
                <div class="mt-1 text-xs text-slate-500 line-clamp-2">${note.preview || ""}</div>
            `;
            btn.addEventListener("click", () => loadNote(note.id));
            noteList.appendChild(btn);
        });
    };

    const loadFolders = async () => {
        const folders = await request("/notes/api/folders", { method: "GET" });
        renderFolders(folders || []);
    };

    const loadNotes = async (query) => {
        if (!activeFolderId) {
            renderNotes([]);
            return;
        }
        const params = new URLSearchParams();
        params.set("folderId", activeFolderId);
        if (query) {
            params.set("q", query);
        }
        const notes = await request(`/notes/api/notes?${params.toString()}`, { method: "GET" });
        renderNotes(notes || []);
    };

    const loadNote = async (noteId) => {
        const note = await request(`/notes/api/notes/${noteId}`, { method: "GET" });
        activeNoteId = note.id;
        noteTitle.value = note.title || "";
        noteMeta.textContent = note.updatedAt ? new Date(note.updatedAt).toLocaleString() : "";
        quill.setContents([]);
        quill.clipboard.dangerouslyPasteHTML(note.content || "");
        setStatus("Saved", "text-emerald-600");
        await loadNotes(noteSearch.value);
    };

    const selectFolder = async (folderId) => {
        activeFolderId = folderId;
        activeNoteId = "";
        noteTitle.value = "";
        noteMeta.textContent = "";
        quill.setContents([]);
        await loadNotes(noteSearch.value);
        await loadFolders();
    };

    const createNote = async () => {
        if (!activeFolderId) {
            return;
        }
        const note = await request("/notes/api/notes", {
            method: "POST",
            body: JSON.stringify({
                folderId: activeFolderId,
                title: "Untitled",
                content: "",
                colour: "slate"
            })
        });
        await loadNotes(noteSearch.value);
        await loadNote(note.id);
    };

    const saveNote = async () => {
        if (!activeNoteId) {
            return;
        }
        isSaving = true;
        setStatus("Saving...", "text-slate-600");
        const payload = {
            title: noteTitle.value || "Untitled",
            content: quill.root.innerHTML,
            folderId: activeFolderId,
            colour: "slate"
        };
        try {
            const note = await request(`/notes/api/notes/${activeNoteId}`, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            noteMeta.textContent = note.updatedAt ? new Date(note.updatedAt).toLocaleString() : "";
            setStatus("Saved", "text-emerald-600");
            await loadNotes(noteSearch.value);
        } catch (error) {
            setStatus("Error", "text-rose-600");
        } finally {
            isSaving = false;
        }
    };

    const scheduleAutosave = () => {
        if (isSaving) {
            return;
        }
        clearTimeout(autosaveTimer);
        setStatus("Saving...", "text-slate-600");
        autosaveTimer = setTimeout(saveNote, 1000);
    };

    const deleteNote = async () => {
        if (!activeNoteId) {
            return;
        }
        await request(`/notes/api/notes/${activeNoteId}`, { method: "DELETE" });
        activeNoteId = "";
        noteTitle.value = "";
        noteMeta.textContent = "";
        quill.setContents([]);
        await loadNotes(noteSearch.value);
    };

    if (addFolderBtn && newFolderRow) {
        addFolderBtn.addEventListener("click", () => {
            newFolderRow.classList.toggle("hidden");
            if (!newFolderRow.classList.contains("hidden")) {
                newFolderName.focus();
            }
        });
    }

    if (cancelFolderBtn && newFolderRow) {
        cancelFolderBtn.addEventListener("click", () => {
            newFolderRow.classList.add("hidden");
            newFolderName.value = "";
        });
    }

    if (createFolderBtn) {
        createFolderBtn.addEventListener("click", async () => {
            const name = (newFolderName.value || "").trim();
            if (!name) {
                return;
            }
            await request("/notes/api/folders", {
                method: "POST",
                body: JSON.stringify({ name, colour: "slate" })
            });
            newFolderName.value = "";
            newFolderRow.classList.add("hidden");
            await loadFolders();
        });
    }

    if (addNoteBtn) {
        addNoteBtn.addEventListener("click", createNote);
    }

    if (noteTitle) {
        noteTitle.addEventListener("input", scheduleAutosave);
    }

    quill.on("text-change", scheduleAutosave);

    if (noteSearch) {
        noteSearch.addEventListener("input", () => {
            clearTimeout(autosaveTimer);
            autosaveTimer = setTimeout(() => loadNotes(noteSearch.value), 300);
        });
    }

    if (folderSearch) {
        folderSearch.addEventListener("input", () => {
            const query = folderSearch.value.toLowerCase();
            Array.from(folderList.children).forEach((item) => {
                const label = item.textContent.toLowerCase();
                item.classList.toggle("hidden", !label.includes(query));
            });
        });
    }

    if (deleteNoteBtn) {
        deleteNoteBtn.addEventListener("click", deleteNote);
    }

    if (exportHtmlBtn) {
        exportHtmlBtn.addEventListener("click", () => {
            if (!activeNoteId) {
                return;
            }
            window.location.href = `/notes/export/${activeNoteId}?format=html`;
        });
    }

    if (activeFolderId) {
        loadFolders();
        loadNotes(noteSearch ? noteSearch.value : "");
    }
});
