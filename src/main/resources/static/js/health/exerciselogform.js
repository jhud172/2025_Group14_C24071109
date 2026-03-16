const moodMap = {
    1: "\u{1F641}",
    2: "\u{1F610}",
    3: "\u{1F642}",
    4: "\u{1F604}"
};

const confidenceMap = {
    1: "\u{1F61F}",
    2: "\u{1F642}",
    3: "\u{1F60E}",
    4: "\u{1F525}"
};

function bounceEmoji(element) {
    if (!element) {
        return;
    }

    element.style.transform = "scale(1.2)";
    element.style.transition = "transform 0.15s ease";
    setTimeout(() => {
        element.style.transform = "scale(1)";
    }, 150);
}

function updateMoodSlider(type, value) {
    const emoji = moodMap[value] || "-";
    const element = document.getElementById(type === "before" ? "moodBeforeEmoji" : "moodAfterEmoji");
    if (!element) {
        return;
    }

    if (element.textContent !== emoji) {
        element.textContent = emoji;
        bounceEmoji(element);
    }
}

function updateConfidenceSlider(value) {
    const element = document.getElementById("confidenceEmoji");
    const emoji = confidenceMap[value] || "-";
    if (!element) {
        return;
    }

    if (element.textContent !== emoji) {
        element.textContent = emoji;
        bounceEmoji(element);
    }
}

function updatePreview() {
    const duration = document.querySelector("[name='durationMinutes']")?.value || "";
    const comments = document.querySelector("[name='comments']")?.value || "";
    const date = document.querySelector("[name='date']")?.value || "";
    const moodBefore = document.querySelector("[name='moodBefore']")?.value;
    const moodAfter = document.querySelector("[name='moodAfter']")?.value;
    const confidence = document.querySelector("[name='confidence']")?.value;

    const previewDate = document.getElementById("previewDate");
    const previewMoodBefore = document.getElementById("previewMoodBefore");
    const previewMoodAfter = document.getElementById("previewMoodAfter");
    const previewConfidence = document.getElementById("previewConfidence");
    const previewDuration = document.getElementById("previewDuration");
    const previewComments = document.getElementById("previewComments");

    if (previewDate) {
        previewDate.textContent = date || "-";
    }
    if (previewMoodBefore) {
        previewMoodBefore.textContent = moodMap[moodBefore] || "-";
    }
    if (previewMoodAfter) {
        previewMoodAfter.textContent = moodMap[moodAfter] || "-";
    }
    if (previewConfidence) {
        previewConfidence.textContent = confidenceMap[confidence] || "-";
    }
    if (previewDuration) {
        previewDuration.textContent = duration ? `${duration} minutes` : "-";
    }
    if (previewComments) {
        previewComments.textContent = comments.trim() === "" ? "No comments yet." : comments.trim();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("[data-exercise-log-form]");
    if (!form) {
        return;
    }

    form.querySelectorAll("input, textarea, select").forEach((element) => {
        element.addEventListener("input", () => {
            switch (element.name) {
                case "moodBefore":
                    updateMoodSlider("before", element.value);
                    break;
                case "moodAfter":
                    updateMoodSlider("after", element.value);
                    break;
                case "confidence":
                    updateConfidenceSlider(element.value);
                    break;
                default:
                    break;
            }

            updatePreview();
        });
    });

    updateMoodSlider("before", form.querySelector("[name='moodBefore']")?.value);
    updateMoodSlider("after", form.querySelector("[name='moodAfter']")?.value);
    updateConfidenceSlider(form.querySelector("[name='confidence']")?.value);
    updatePreview();
});
