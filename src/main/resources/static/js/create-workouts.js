document.addEventListener("DOMContentLoaded", function () {

    const suggestedList = document.getElementById("suggested-list");
    const container = document.getElementById("workout-input-container");
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // static init (run on first page render)

    new Sortable(suggestedList, {
        group: {
            name: "exercises",
            put: true
        },
        animation: 150,
        sort: false,
        onSort: function() {
            toggleEmptyState()
        }
    });
    toggleEmptyState();

    // Setup searchbars
    const workoutSearchBar = document.getElementById('workout-search-input');
    const workoutList = document.getElementById('existing-workouts');
    workoutSearchBar.addEventListener('input', function(searchInput) {

        const searchTerm = searchInput.target.value.toLowerCase();
        search(searchTerm, workoutList);

    });

    const exerciseSearchBar = document.getElementById('exercise-search-input');
    const exerciseList = document.getElementById('suggested-list');
    exerciseSearchBar.addEventListener('input', function(searchInput) {
        const searchTerm = searchInput.target.value.toLowerCase();
        search(searchTerm, exerciseList);
    })

    // Select edit workout
    workoutList.addEventListener('click', function(e) {

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

            document.querySelectorAll('.workout-item').forEach(el => el.classList.remove('ring-2', 'ring-blue-500'));
            row.classList.add('ring-2', 'ring-blue-500');

            loadFragment(url);
        }

    });


    const scheduleButton = document.getElementById('schedule-button');
    scheduleButton.addEventListener('click', function () {
        window.location.replace("schedules/builder");
    });

    // Select create new workout
    const newBtn = document.getElementById('btn-new-workout');
    if (newBtn) {
        newBtn.addEventListener('click', function() {
            const url = this.getAttribute('data-url');
            loadFragment(url);
        });
    }


    initFragmentLogic();

    /**
     * Re-initializes SortableJS and Event Listeners for the editable area.
     * Must be called after fetch replaces the HTML.
     */
    function initFragmentLogic() {
        const workoutList = document.getElementById("workout-list");
        const saveWorkoutBtn = document.getElementById("save-workout-btn");


        if (workoutList) {
            // Make it so exercises can be dropped into workoutList again (it gets reset when reloading the fragment)
            new Sortable(workoutList, {
                group: "exercises",
                animation: 150,
                onAdd: function (evt) {
                    const exercise = evt.item;

                    const oldBtn = exercise.querySelector("button");
                    if(oldBtn) oldBtn.remove();

                    addRemoveButton(exercise, suggestedList);
                    toggleEmptyState();
                }
            });

            Array.from(workoutList.children).forEach(function(exercise) {
                addRemoveButton(exercise, suggestedList)
            });
        }

        if (saveWorkoutBtn) {
            saveWorkoutBtn.addEventListener('click', handleSave);
        }
    }


    // -- Helpers --

    function loadFragment(url) {
        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                // Change the html within the container to the new fragment
                container.innerHTML = html;
                initFragmentLogic();
            })
            .catch(error => console.error('Error loading fragment:', error));
    }

    function addRemoveButton(element, exerciseList) {

        if (element.querySelector('.remove-btn')) return;

        const btn = document.createElement("button");
        btn.setAttribute("class", "remove-btn bg-transparent hover:bg-red-500 text-red-700 font-semibold hover:text-white mt-2 py-1 px-4 border border-red-500 rounded transition-all");
        btn.innerText = "remove";

        btn.onclick = function () {
            btn.remove();
            const hiddenId = element.querySelector('input[name="exerciseIds"]');
            if (hiddenId) {
                hiddenId.remove();
            }

            exerciseList.appendChild(element);
            toggleEmptyState();
        };

        element.appendChild(btn);
    }

    function getExerciseIds() {
        const workout = document.getElementById("workout-list");

        const exercises = workout.children;
        let exerciseIds = [];
        for (let i = 0; i < exercises.length; i++) {
            const exercise = exercises[i];
            const id = exercise.getAttribute("data-id");
            if(id) exerciseIds.push(id);
        }
        return exerciseIds;
    }

    function handleSave() {
        const nameInput = document.getElementById("workout-name").value;
        const notesInput = document.getElementById("workout-notes").value;
        const idInput = document.getElementById("workout-id");
        const selectedExerciseIds = getExerciseIds();

        // Validation
        if (!nameInput || nameInput.trim() === "") {
            alert("Please give your workout a name!");
            return;
        }

        if (selectedExerciseIds.length === 0) {
            alert("Please add at least one exercise!");
            return;
        }

        const payload = {
            id: (idInput && idInput.value) ? idInput.value : null,
            name: nameInput,
            workoutNotes: notesInput,
            exerciseIds: selectedExerciseIds
        };

        fetch("/save-workout", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
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
                // Reload page to update all lists and inputs
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(err => console.error(err));
    }

    function handleWorkoutDelete(id) {
        if (!id) return;

        const payload = {
            id: id
        };

        fetch("/delete-workout", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Delete failed');
            })
            .then(data => {
                showSuccess(data.message || "Deleted successfully");

                // Reload to remove the item from the html list
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            })
            .catch(err => {
                console.error(err);
                alert("Error deleting workout.");
            });
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
        Array.from(list.children).forEach(function(item) {

            const nameSpan = item.querySelector('span.font-bold');
            const text = nameSpan ? nameSpan.textContent.toLowerCase() : "";

            item.style.display = text.includes(searchInput) ? '' : 'none';
        });
    }

    function toggleEmptyState() {
        const msg = document.getElementById("no-exercises-msg");
        if (suggestedList.children.length > 0) {
            msg.classList.add("hidden");
        } else {
            msg.classList.remove("hidden");
        }
    }
});