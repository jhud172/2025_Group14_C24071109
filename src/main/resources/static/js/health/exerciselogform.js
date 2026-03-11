const moodMap = {1: "🙁", 2: "😐", 3: "🙂", 4: "😄"};
const confidenceMap = {1: "😟", 2: "🙂", 3: "😎", 4: "🔥"};

function updateMoodSlider(type, value) {
    const emoji = moodMap[value];
    const el = document.getElementById(
        type === "before" ? "moodBeforeEmoji" : "moodAfterEmoji"
    );
    if (!el) return;
    el.textContent = emoji;
    el.style.transform = "scale(1.2)";
    el.style.transition = "transform 0.15s ease";
    setTimeout(() => {
        el.style.transform = "scale(1)";
    }, 150);
    updatePreview();
}

function updateConfidenceSlider(value) {
    const emojiEl = document.getElementById("confidenceEmoji");
    if (!emojiEl) return;
    emojiEl.textContent = confidenceMap[value];
    emojiEl.style.transform = "scale(1.2)";
    setTimeout(() => {
        emojiEl.style.transform = "scale(1)";
    }, 150);
    updatePreview();
}

function updatePreview() {
    const duration = document.querySelector("[name='durationMinutes']")?.value || "";
    const comments = document.querySelector("[name='comments']")?.value || "";
    const date = document.querySelector("[name='date']")?.value || "";
    const moodBefore = document.querySelector("[name='moodBefore']")?.value;
    const moodAfter = document.querySelector("[name='moodAfter']")?.value;
    const confidence = document.querySelector("[name='confidence']")?.value;
    document.getElementById("previewDate").textContent =
        date === "" ? "—" : date;
    document.getElementById("previewMoodBefore").textContent =
        moodMap[moodBefore] || "—";
    document.getElementById("previewMoodAfter").textContent =
        moodMap[moodAfter] || "—";
    document.getElementById("previewConfidence").textContent =
        confidenceMap[confidence] || "—";
    document.getElementById("previewDuration").textContent =
        duration === "" ? "—" : `${duration} minutes`;
    document.getElementById("previewComments").textContent =
        comments.trim() === "" ? "No comments yet…" : comments.trim();
}

document.addEventListener("DOMContentLoaded", () => {
    const inputs = document.querySelectorAll(
        "[name='durationMinutes'], [name='comments'], [name='date'], [name='moodBefore'], [name='moodAfter'], [name='confidence']"
    );
    inputs.forEach(el => {
        el.addEventListener("input", updatePreview);
    });
    updatePreview();
});
