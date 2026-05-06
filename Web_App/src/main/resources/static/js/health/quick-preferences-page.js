(function () {
    "use strict";

    const presets = {
        "weight-loss-beginner": {
            title: "Weight Loss - Beginner",
            rows: [
                ["Goal", "Weight loss"],
                ["Level", "Beginner"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Low impact home workouts"],
                ["Defaults", "3 sets, 12-20 reps"]
            ]
        },
        "weight-loss-intermediate": {
            title: "Weight Loss - Intermediate",
            rows: [
                ["Goal", "Weight loss"],
                ["Level", "Intermediate"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Gym workouts and HIIT"],
                ["Defaults", "4 sets, 10-15 reps"]
            ]
        },
        "muscle-building": {
            title: "Muscle Building",
            rows: [
                ["Goal", "Muscle gain"],
                ["Level", "Intermediate"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Gym-based hypertrophy"],
                ["Defaults", "4 sets, 8-12 reps"]
            ]
        },
        "strength": {
            title: "Strength Training",
            rows: [
                ["Goal", "Increase strength"],
                ["Level", "Intermediate"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Barbell and dumbbell strength"],
                ["Defaults", "5 sets, 3-6 reps"]
            ]
        },
        "endurance": {
            title: "Endurance / Cardio",
            rows: [
                ["Goal", "Improve endurance"],
                ["Level", "Intermediate"],
                ["Frequency", "5+ times per week"],
                ["Focus", "Outdoor cardio and recovery"],
                ["Defaults", "3 sets, 15-25 reps"]
            ]
        },
        "flexibility": {
            title: "Flexibility & Mobility",
            rows: [
                ["Goal", "Flexibility and mobility"],
                ["Level", "Beginner"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Stretching, yoga, low impact"],
                ["Defaults", "3 sets, 10-20 reps"]
            ]
        },
        "general-fitness": {
            title: "General Health & Fitness",
            rows: [
                ["Goal", "General fitness"],
                ["Level", "Beginner"],
                ["Frequency", "3-4 times per week"],
                ["Focus", "Balanced gym training"],
                ["Defaults", "3 sets, 10-15 reps"]
            ]
        },
        "home-beginner": {
            title: "Home Workout - Beginner",
            rows: [
                ["Goal", "General fitness"],
                ["Level", "Beginner"],
                ["Frequency", "1-2 times per week"],
                ["Focus", "Low impact home workouts"],
                ["Defaults", "3 sets, 10-15 reps"]
            ]
        }
    };

    const select = document.getElementById("quickPreset");
    const preview = document.getElementById("quick-preset-preview");
    const title = document.getElementById("quick-preview-title");
    const body = document.getElementById("quick-preview-body");
    if (!select || !preview || !title || !body) {
        return;
    }

    const renderPreview = () => {
        const preset = presets[select.value];
        if (!preset) {
            preview.classList.remove("visible");
            body.innerHTML = "";
            return;
        }

        title.textContent = preset.title;
        body.innerHTML = "";
        preset.rows.forEach((row) => {
            const label = document.createElement("div");
            label.className = "font-medium text-slate-500 dark:text-slate-400";
            label.textContent = row[0];

            const value = document.createElement("div");
            value.className = "text-slate-800 dark:text-slate-200";
            value.textContent = row[1];

            body.append(label, value);
        });
        preview.classList.add("visible");
    };

    select.addEventListener("change", renderPreview);
    renderPreview();
})();
