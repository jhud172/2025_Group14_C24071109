document.addEventListener("DOMContentLoaded", function () {

    const suggestedList = document.getElementById("suggested-list");
    const container = document.getElementById("workout-input-container");
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || "";
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || "X-CSRF-TOKEN";
    const premiumRoot = document.querySelector("[data-premium]");
    const isPremium = premiumRoot?.getAttribute("data-premium") === "true";

    if (!suggestedList || !container) {
        return;
    }

    new Sortable(suggestedList, {
        group: {
            name: "exercises",
            put: false,
            pull: "clone"
        },
        animation: 150,
        sort: false,
        onSort: toggleEmptyState
    });
    toggleEmptyState();

    const workoutSearchBar = document.getElementById('workout-search-input');
    const workoutList = document.getElementById('existing-workouts');
    if (workoutSearchBar && workoutList) {
        workoutSearchBar.addEventListener('input', function (searchInput) {
            const searchTerm = searchInput.target.value.toLowerCase();
            search(searchTerm, workoutList);
        });
    }

    const exerciseSearchBar = document.getElementById('exercise-search-input');
    if (exerciseSearchBar) {
        exerciseSearchBar.addEventListener('input', function (searchInput) {
            const searchTerm = searchInput.target.value.toLowerCase();
            search(searchTerm, suggestedList);
        });
    }

    const customSearchBar = document.getElementById('custom-exercise-search');
    const customList = document.getElementById('custom-exercise-list');
    if (customSearchBar && customList) {
        customSearchBar.addEventListener('input', function (searchInput) {
            const searchTerm = searchInput.target.value.toLowerCase();
            search(searchTerm, customList);
        });
    }

    if (workoutList) {
        workoutList.addEventListener('click', function (e) {
            const deleteBtn = e.target.closest('.btn-delete');
            if (deleteBtn) {
                e.stopPropagation();
                const row = deleteBtn.closest('.workout-item');
                const workoutId = row.getAttribute('data-id');
                if (confirm("Are you sure you want to delete this workout?")) {
                    handleWorkoutDelete(workoutId);
                }
                return;
            }

            const row = e.target.closest('.workout-item');
            if (row) {
                const url = row.getAttribute('data-url');
                document.querySelectorAll('.workout-item').forEach(el => el.classList.remove('ring-2', 'ring-slate-900/10'));
                row.classList.add('ring-2', 'ring-slate-900/10');
                loadFragment(url);
            }
        });
    }

    const scheduleButton = document.getElementById('schedule-button');
    if (scheduleButton) {
        scheduleButton.addEventListener('click', function () {
            window.location.replace("schedules/builder");
        });
    }

    const newBtn = document.getElementById('btn-new-workout');
    if (newBtn) {
        newBtn.addEventListener('click', function () {
            const url = this.getAttribute('data-url');
            loadFragment(url);
        });
    }

    initFragmentLogic();
    initCustomExerciseLogic();
    initAiSuggestions();
    initUpgradeModal();

    function initFragmentLogic() {
        const workoutList = document.getElementById("workout-list");
        const saveWorkoutBtn = document.getElementById("save-workout-btn");

        if (!workoutList) return;

        new Sortable(workoutList, {
            group: "exercises",
            animation: 150,
            onAdd: function (evt) {
                const exercise = evt.item;
                if (!exercise.classList.contains("workout-item")) {
                    exercise.classList.add("workout-item");
                }
                if (!exercise.getAttribute('data-source')) {
                    const type = exercise.getAttribute('data-type') || 'exercise';
                    exercise.setAttribute('data-source', type === 'custom' ? 'custom' : 'suggested');
                }
                addRemoveButton(exercise);
                attachItemSelection(exercise);
                toggleEmptyState();
            }
        });

        Array.from(workoutList.children).forEach(function (exercise) {
            addRemoveButton(exercise);
            attachItemSelection(exercise);
        });

        if (saveWorkoutBtn) {
            saveWorkoutBtn.addEventListener('click', handleSave);
        }

        const selectedRemove = document.getElementById("selected-exercise-remove");
        if (selectedRemove) {
            selectedRemove.addEventListener('click', () => {
                const selected = workoutList.querySelector('.workout-item.selected');
                if (selected) {
                    selected.remove();
                    clearSelection();
                }
            });
        }
    }

    function initCustomExerciseLogic() {
        const form = document.getElementById('custom-exercise-form');
        const list = document.getElementById('custom-exercise-list');

        if (!form || !list) return;

        form.addEventListener('submit', function (e) {
            e.preventDefault();
            handleCustomSave();
        });

        const resetBtn = document.getElementById('custom-exercise-reset');
        if (resetBtn) {
            resetBtn.addEventListener('click', resetCustomForm);
        }

        list.addEventListener('click', function (e) {
            const addBtn = e.target.closest('.custom-add');
            const editBtn = e.target.closest('.custom-edit');
            const deleteBtn = e.target.closest('.custom-delete');
            const item = e.target.closest('.custom-ex-item');

            if (!item) return;

            if (addBtn) {
                e.preventDefault();
                addCustomToWorkout(item);
                return;
            }

            if (editBtn) {
                e.preventDefault();
                populateCustomForm(item);
                return;
            }

            if (deleteBtn) {
                e.preventDefault();
                if (confirm("Delete this custom exercise?")) {
                    deleteCustomExercise(item.getAttribute('data-id'));
                }
            }
        });
    }

    function initAiSuggestions() {
        const btn = document.getElementById('ai-suggestions-btn');
        const list = document.getElementById('ai-suggestions-list');
        const wrapper = document.getElementById('ai-suggestions');
        if (!btn || !list || !wrapper) return;

        btn.addEventListener('click', async () => {
            if (!isPremium) {
                showUpgradeModal();
                return;
            }

            list.innerHTML = "";
            wrapper.classList.remove("hidden");

            try {
                const res = await fetch('/workout/ai-suggestions', {
                    method: 'POST',
                    headers: headers(),
                    body: JSON.stringify({ prompt: "" })
                });

                if (res.status === 403) {
                    showUpgradeModal();
                    return;
                }

                const data = await res.json();
                const suggestions = data.suggestions || [];
                suggestions.forEach(text => {
                    const li = document.createElement('li');
                    li.className = "flex items-center justify-between gap-2 rounded-xl border border-blue-200 bg-white/80 px-3 py-2 text-sm shadow-sm";
                    li.innerHTML = `
                        <span class="font-semibold text-slate-800">${escapeHtml(text)}</span>
                        <button class="ai-add rounded-lg bg-blue-600 px-2 py-1 text-xs font-semibold text-white">Add</button>
                    `;
                    li.querySelector('.ai-add').addEventListener('click', () => {
                        addSuggestionToWorkout(text);
                    });
                    list.appendChild(li);
                });
            } catch (err) {
                console.error(err);
            }
        });
    }

    function initUpgradeModal() {
        const modal = document.getElementById('upgrade-modal');
        const closeBtn = document.getElementById('upgrade-modal-close');
        const dismissBtn = document.getElementById('upgrade-modal-dismiss');

        if (!modal) return;

        const close = () => {
            modal.classList.add('hidden');
            modal.classList.remove('flex');
        };

        if (closeBtn) closeBtn.addEventListener('click', close);
        if (dismissBtn) dismissBtn.addEventListener('click', close);
        modal.addEventListener('click', (e) => {
            if (e.target === modal) close();
        });
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') close();
        });
    }

    function showUpgradeModal() {
        const modal = document.getElementById('upgrade-modal');
        if (!modal) return;
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }

    function loadFragment(url) {
        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                container.innerHTML = html;
                clearSelection();
                initFragmentLogic();
            })
            .catch(error => console.error('Error loading fragment:', error));
    }

    function addRemoveButton(element) {
        if (element.querySelector('.remove-btn')) return;

        if (!element.getAttribute('data-source')) {
            const type = element.getAttribute('data-type') || 'exercise';
            element.setAttribute('data-source', type === 'custom' ? 'custom' : 'suggested');
        }

        const btn = document.createElement("button");
        btn.setAttribute("class", "remove-btn mt-2 inline-flex items-center justify-center rounded-lg border border-red-300 bg-white px-3 py-1.5 text-xs font-semibold text-red-700 shadow-sm transition hover:bg-red-50");
        btn.innerText = "remove";

        btn.onclick = function () {
            btn.remove();
            const source = element.getAttribute('data-source');
            if (source === 'suggested') {
                suggestedList.appendChild(element);
            } else {
                element.remove();
            }
            toggleEmptyState();
            clearSelection();
        };

        element.appendChild(btn);
    }

    function getWorkoutPayload() {
        const workout = document.getElementById("workout-list");
        const exercises = workout ? Array.from(workout.children) : [];
        const exerciseIds = [];
        const customExerciseIds = [];

        exercises.forEach(exercise => {
            const id = exercise.getAttribute("data-id");
            const type = exercise.getAttribute("data-type");
            if (!id) return;
            if (type === 'custom') {
                customExerciseIds.push(id);
            } else {
                exerciseIds.push(id);
            }
        });

        return { exerciseIds, customExerciseIds };
    }

    function handleSave() {
        const nameInput = document.getElementById("workout-name").value;
        const notesInput = document.getElementById("workout-notes").value;
        const idInput = document.getElementById("workout-id");
        const payloadIds = getWorkoutPayload();

        if (!nameInput || nameInput.trim() === "") {
            alert("Please give your workout a name!");
            return;
        }

        if (payloadIds.exerciseIds.length + payloadIds.customExerciseIds.length === 0) {
            alert("Please add at least one exercise!");
            return;
        }

        const payload = {
            id: (idInput && idInput.value) ? idInput.value : null,
            name: nameInput,
            workoutNotes: notesInput,
            exerciseIds: payloadIds.exerciseIds,
            customExerciseIds: payloadIds.customExerciseIds
        };

        fetch("/save-workout", {
            method: "POST",
            headers: headers(),
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Save failed');
            })
            .then(data => {
                showSuccess(data.message || "Saved successfully");
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(err => console.error(err));
    }

    function handleWorkoutDelete(id) {
        if (!id) return;

        fetch("/delete-workout", {
            method: "POST",
            headers: headers(),
            body: JSON.stringify({ id })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Delete failed');
            })
            .then(data => {
                showSuccess(data.message || "Deleted successfully");
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(err => {
                console.error(err);
                alert("Error deleting workout.");
            });
    }

    function handleCustomSave() {
        const idInput = document.getElementById('custom-exercise-id');
        const nameInput = document.getElementById('custom-exercise-name');
        const descriptionInput = document.getElementById('custom-exercise-description');
        const howToInput = document.getElementById('custom-exercise-howto');
        const videoInput = document.getElementById('custom-exercise-video');
        const colorInput = document.getElementById('custom-exercise-color');

        const payload = {
            name: nameInput.value,
            description: descriptionInput.value,
            howTo: howToInput.value,
            videoUrl: videoInput.value,
            colorTag: colorInput ? colorInput.value : ""
        };

        const id = idInput.value;
        const url = id ? `/workout/custom-exercises/${id}` : '/workout/custom-exercises';

        fetch(url, {
            method: 'POST',
            headers: headers(),
            body: JSON.stringify(payload)
        })
            .then(async res => {
                const data = await res.json();
                if (!res.ok) {
                    throw new Error(data.message || "Save failed");
                }
                return data;
            })
            .then(data => {
                upsertCustomExercise(data);
                resetCustomForm();
            })
            .catch(err => alert(err.message));
    }

    function deleteCustomExercise(id) {
        if (!id) return;
        fetch(`/workout/custom-exercises/${id}/delete`, {
            method: 'POST',
            headers: headers()
        })
            .then(res => res.json())
            .then(() => {
                const item = document.querySelector(`.custom-ex-item[data-id="${id}"]`);
                if (item) item.remove();
            })
            .catch(err => console.error(err));
    }

    function populateCustomForm(item) {
        document.getElementById('custom-exercise-id').value = item.getAttribute('data-id') || "";
        document.getElementById('custom-exercise-name').value = item.getAttribute('data-name') || "";
        document.getElementById('custom-exercise-description').value = item.getAttribute('data-description') || "";
        document.getElementById('custom-exercise-howto').value = item.getAttribute('data-howto') || "";
        document.getElementById('custom-exercise-video').value = item.getAttribute('data-video') || "";
        const color = item.getAttribute('data-color') || "";
        const colorInput = document.getElementById('custom-exercise-color');
        if (colorInput) {
            colorInput.value = color;
        }
    }

    function resetCustomForm() {
        document.getElementById('custom-exercise-id').value = "";
        document.getElementById('custom-exercise-name').value = "";
        document.getElementById('custom-exercise-description').value = "";
        document.getElementById('custom-exercise-howto').value = "";
        document.getElementById('custom-exercise-video').value = "";
        const colorInput = document.getElementById('custom-exercise-color');
        if (colorInput) {
            colorInput.value = "";
        }
    }

    function upsertCustomExercise(data) {
        const list = document.getElementById('custom-exercise-list');
        if (!list || !data) return;

        let item = list.querySelector(`.custom-ex-item[data-id="${data.id}"]`);
        if (!item) {
            item = document.createElement('li');
            item.className = "custom-ex-item flex items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white/80 px-3 py-2 text-sm shadow-sm dark:border-slate-800 dark:bg-slate-950/30";
            list.prepend(item);
        }

        item.setAttribute('data-id', data.id);
        item.setAttribute('data-name', data.name || "");
        item.setAttribute('data-description', data.description || "");
        item.setAttribute('data-howto', data.howTo || "");
        item.setAttribute('data-video', data.videoUrl || "");
        item.setAttribute('data-embed', data.embedUrl || "");
        item.setAttribute('data-color', data.colorTag || "");
        item.setAttribute('data-type', 'custom');

        item.innerHTML = `
            <div class="flex min-w-0 items-center gap-2">
                ${data.colorTag ? `<span class="h-2 w-2 rounded-full" style="background:${data.colorTag}"></span>` : ''}
                <span class="truncate font-semibold text-slate-900">${escapeHtml(data.name)}</span>
            </div>
            <div class="flex items-center gap-2">
                <button class="custom-add inline-flex items-center rounded-lg bg-blue-600 px-2 py-1 text-xs font-semibold text-white">Add</button>
                <button class="custom-edit text-xs text-slate-500 hover:text-slate-900">Edit</button>
                <button class="custom-delete text-xs text-rose-600">Delete</button>
            </div>
        `;
    }

    function addCustomToWorkout(item) {
        const workoutList = document.getElementById("workout-list");
        if (!workoutList) return;

        const newItem = buildWorkoutItem({
            id: item.getAttribute('data-id'),
            name: item.getAttribute('data-name'),
            description: item.getAttribute('data-description'),
            howTo: item.getAttribute('data-howto'),
            videoUrl: item.getAttribute('data-embed') || item.getAttribute('data-video'),
            colorTag: item.getAttribute('data-color'),
            type: 'custom'
        });
        workoutList.appendChild(newItem);
        attachItemSelection(newItem);
        addRemoveButton(newItem);
        toggleEmptyState();
    }

    function addSuggestionToWorkout(text) {
        const workoutList = document.getElementById("workout-list");
        if (!workoutList) return;

        fetch('/workout/custom-exercises', {
            method: 'POST',
            headers: headers(),
            body: JSON.stringify({ name: text, description: "AI suggestion" })
        })
            .then(async res => {
                const data = await res.json();
                if (!res.ok) {
                    throw new Error(data.message || "Failed to add suggestion");
                }
                return data;
            })
            .then(data => {
                upsertCustomExercise(data);
                const newItem = buildWorkoutItem({
                    id: data.id,
                    name: data.name,
                    description: data.description,
                    howTo: data.howTo,
                    videoUrl: data.embedUrl || data.videoUrl,
                    colorTag: data.colorTag,
                    type: 'custom'
                });
                workoutList.appendChild(newItem);
                attachItemSelection(newItem);
                addRemoveButton(newItem);
                toggleEmptyState();
            })
            .catch(err => alert(err.message));
    }

    function buildWorkoutItem({ id, name, description, howTo, videoUrl, colorTag, type }) {
        const li = document.createElement('li');
        li.className = "workout-item group cursor-pointer rounded-xl border border-slate-200 bg-white p-3 shadow-sm transition hover:border-slate-300 hover:shadow-md";
        if (type === 'custom') {
            li.classList.add('border-indigo-200', 'bg-indigo-50/70');
        }
        li.setAttribute('data-id', id || "");
        li.setAttribute('data-name', name || "");
        li.setAttribute('data-description', description || "");
        li.setAttribute('data-howto', howTo || "");
        li.setAttribute('data-video', videoUrl || "");
        li.setAttribute('data-embed', videoUrl || "");
        li.setAttribute('data-color', colorTag || "");
        li.setAttribute('data-type', type || 'exercise');
        li.setAttribute('data-source', type === 'exercise' ? 'suggested' : 'custom');

        li.innerHTML = `
            <div class="flex justify-between items-center mb-1">
                <span class="font-bold text-slate-800">${escapeHtml(name)}</span>
                <span class="text-xs font-semibold uppercase ${type === 'custom' ? 'text-indigo-500' : 'text-slate-400'}">${type === 'custom' ? 'Custom' : 'Exercise'}</span>
            </div>
            <p class="line-clamp-2 text-sm text-slate-600">${escapeHtml(description || '')}</p>
        `;
        return li;
    }

    function attachItemSelection(item) {
        item.addEventListener('click', () => {
            document.querySelectorAll('.workout-item.selected').forEach(el => el.classList.remove('selected', 'ring-2', 'ring-blue-400'));
            item.classList.add('selected', 'ring-2', 'ring-blue-400');
            updateSelectedPanel(item);
        });
    }

    function updateSelectedPanel(item) {
        const emptyState = document.getElementById('selected-exercise-empty');
        const panel = document.getElementById('selected-exercise-panel');
        if (!emptyState || !panel) return;

        emptyState.classList.add('hidden');
        panel.classList.remove('hidden');

        document.getElementById('selected-exercise-name').textContent = item.getAttribute('data-name') || "";
        document.getElementById('selected-exercise-type').textContent = (item.getAttribute('data-type') || 'exercise').toUpperCase();
        document.getElementById('selected-exercise-description').textContent = item.getAttribute('data-description') || "";
        const howTo = item.getAttribute('data-howto') || "";
        const howToEl = document.getElementById('selected-exercise-howto');
        howToEl.textContent = howTo;
        howToEl.classList.toggle('hidden', howTo.length === 0);

        const videoContainer = document.getElementById('selected-exercise-video');
        const videoUrl = item.getAttribute('data-embed') || item.getAttribute('data-video') || "";
        if (videoUrl) {
            videoContainer.innerHTML = `<iframe class="h-48 w-full" src="${escapeHtml(videoUrl)}" title="Exercise video" loading="lazy" allowfullscreen></iframe>`;
            videoContainer.classList.remove('hidden');
        } else {
            videoContainer.innerHTML = '';
            videoContainer.classList.add('hidden');
        }
    }

    function clearSelection() {
        const emptyState = document.getElementById('selected-exercise-empty');
        const panel = document.getElementById('selected-exercise-panel');
        if (emptyState && panel) {
            emptyState.classList.remove('hidden');
            panel.classList.add('hidden');
        }
        document.querySelectorAll('.workout-item.selected').forEach(el => el.classList.remove('selected', 'ring-2', 'ring-blue-400'));
    }

    function showSuccess(message) {
        const responseContainer = document.getElementById("response-container");
        if (responseContainer) {
            responseContainer.innerText = message;
            responseContainer.style.display = "flex";

            setTimeout(() => {
                responseContainer.style.display = "none";
            }, 3000);
        } else {
            alert(message);
        }
    }

    function search(searchInput, list) {
        Array.from(list.children).forEach(function (item) {
            const name = item.getAttribute('data-name') || item.textContent || "";
            const text = name.toLowerCase();
            item.style.display = text.includes(searchInput) ? '' : 'none';
        });
    }

    function toggleEmptyState() {
        const msg = document.getElementById("no-exercises-msg");
        if (!msg) return;
        if (suggestedList.children.length > 0) {
            msg.classList.add("hidden");
        } else {
            msg.classList.remove("hidden");
        }
    }

    function headers() {
        const out = { 'Content-Type': 'application/json' };
        if (csrfToken) out[csrfHeader] = csrfToken;
        return out;
    }

    function escapeHtml(str) {
        return String(str || '')
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});