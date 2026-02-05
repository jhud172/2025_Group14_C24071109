document.addEventListener("DOMContentLoaded", () => {
    const bootstrap = window.__workoutPlayerBootstrap || {};
    const sessionId = bootstrap.sessionId;
    const csrfHeader = bootstrap.csrfHeader || "X-CSRF-TOKEN";
    const csrfToken = bootstrap.csrfToken || "";

    if (!sessionId) {
        return;
    }

    const totalVolume = document.getElementById("totalVolume");
    const restTimer = document.getElementById("restTimer");
    const sessionComplete = document.getElementById("sessionComplete");

    let restInterval = null;
    let remaining = 0;

    const updateRestTimer = () => {
        if (!restTimer) {
            return;
        }
        const minutes = String(Math.floor(remaining / 60)).padStart(2, "0");
        const seconds = String(remaining % 60).padStart(2, "0");
        restTimer.textContent = `${minutes}:${seconds}`;
    };

    const startRest = (seconds) => {
        if (!seconds || seconds <= 0) {
            return;
        }
        remaining = seconds;
        updateRestTimer();
        if (restInterval) {
            clearInterval(restInterval);
        }
        restInterval = setInterval(() => {
            remaining -= 1;
            updateRestTimer();
            if (remaining <= 0) {
                clearInterval(restInterval);
                restInterval = null;
            }
        }, 1000);
    };

    const request = async (url, payload) => {
        const headers = { "Content-Type": "application/json" };
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }
        const response = await fetch(url, {
            method: "POST",
            headers,
            body: JSON.stringify(payload || {})
        });
        if (!response.ok) {
            throw new Error("Failed to save set");
        }
        return response.json();
    };

    const readPayload = (row) => {
        const payload = {};
        const weight = row.querySelector("[data-field='weight']");
        const reps = row.querySelector("[data-field='reps']");
        const notes = row.querySelector("[data-field='notes']");
        const completed = row.querySelector("[data-field='completed']");

        if (weight && weight.value !== "") {
            payload.weight = parseFloat(weight.value);
        }
        if (reps && reps.value !== "") {
            payload.reps = parseInt(reps.value, 10);
        }
        if (notes) {
            payload.notes = notes.value || "";
        }
        if (completed) {
            payload.completed = completed.checked;
        }
        return payload;
    };

    const applySummary = (data) => {
        if (totalVolume && data && typeof data.totalVolume !== "undefined") {
            totalVolume.textContent = data.totalVolume ?? 0;
        }
        if (sessionComplete && data) {
            sessionComplete.classList.toggle("hidden", !data.completed);
        }
    };

    const bindVideoPanel = (row, setId) => {
        const panel = row.querySelector("[data-video-panel]");
        if (!panel) return;

        const startBtn = panel.querySelector("[data-record-start]");
        const stopBtn = panel.querySelector("[data-record-stop]");
        const uploadBtn = panel.querySelector("[data-record-upload]");
        const statusEl = panel.querySelector("[data-video-status]");
        const preview = panel.querySelector("[data-record-preview]");
        const feedbackEl = panel.querySelector("[data-feedback]");

        let mediaRecorder = null;
        let mediaStream = null;
        let chunks = [];
        let recordedBlob = null;

        const setStatus = (text) => {
            if (statusEl) statusEl.textContent = text;
        };

        const showFeedback = (payload) => {
            if (!feedbackEl) return;
            if (!payload || !payload.feedback) {
                feedbackEl.classList.add("hidden");
                return;
            }
            const feedback = payload.feedback;
            let flags = "";
            if (feedback.flags) {
                try {
                    const parsed = typeof feedback.flags === "string" ? JSON.parse(feedback.flags) : feedback.flags;
                    flags = Object.entries(parsed || {}).map(([k, v]) => `${k}: ${v}`).join(" · ");
                } catch {
                    flags = "";
                }
            }
            const lines = [
                `Reps: ${feedback.repCount ?? "-"}`,
                `Tempo: ${feedback.tempo ?? "-"}`,
                `Confidence: ${feedback.confidence != null ? Math.round(feedback.confidence * 100) + "%" : "-"}`,
                flags ? `Flags: ${flags}` : ""
            ].filter(Boolean);
            feedbackEl.textContent = lines.join("\n");
            feedbackEl.classList.remove("hidden");
        };

        const pollFeedback = async () => {
            try {
                const res = await fetch(`/workouts/session/${sessionId}/sets/${setId}/video/latest`);
                if (!res.ok) return;
                const data = await res.json();
                if (data.status) {
                    setStatus(data.status.toLowerCase());
                }
                if (data.status === "COMPLETE") {
                    showFeedback(data);
                }
            } catch {
                // ignore
            }
        };

        const stopRecording = () => {
            if (mediaRecorder && mediaRecorder.state !== "inactive") {
                mediaRecorder.stop();
            }
            if (mediaStream) {
                mediaStream.getTracks().forEach(track => track.stop());
            }
        };

        startBtn?.addEventListener("click", async () => {
            try {
                mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: false });
                const options = MediaRecorder.isTypeSupported("video/webm") ? { mimeType: "video/webm" } : undefined;
                mediaRecorder = options ? new MediaRecorder(mediaStream, options) : new MediaRecorder(mediaStream);
                chunks = [];
                recordedBlob = null;
                mediaRecorder.ondataavailable = (event) => {
                    if (event.data && event.data.size > 0) chunks.push(event.data);
                };
                mediaRecorder.onstop = () => {
                    recordedBlob = new Blob(chunks, { type: "video/webm" });
                    if (preview) {
                        preview.src = URL.createObjectURL(recordedBlob);
                        preview.classList.remove("hidden");
                    }
                    if (uploadBtn) uploadBtn.classList.remove("hidden");
                    if (stopBtn) stopBtn.classList.add("hidden");
                    if (startBtn) startBtn.classList.remove("hidden");
                    setStatus("Recorded");
                };
                mediaRecorder.start();
                if (startBtn) startBtn.classList.add("hidden");
                if (stopBtn) stopBtn.classList.remove("hidden");
                if (uploadBtn) uploadBtn.classList.add("hidden");
                setStatus("Recording...");
            } catch {
                setStatus("Camera unavailable");
            }
        });

        stopBtn?.addEventListener("click", () => {
            stopRecording();
        });

        uploadBtn?.addEventListener("click", async () => {
            if (!recordedBlob) return;
            const formData = new FormData();
            formData.append("video", recordedBlob, `set-${setId}.webm`);

            const headers = {};
            if (csrfToken) headers[csrfHeader] = csrfToken;

            setStatus("Uploading...");
            try {
                const res = await fetch(`/workouts/session/${sessionId}/sets/${setId}/video`, {
                    method: "POST",
                    headers,
                    body: formData
                });
                if (!res.ok) {
                    setStatus("Upload failed");
                    return;
                }
                setStatus("Processing");
                showFeedback(null);
                pollFeedback();
                setTimeout(pollFeedback, 5000);
                setTimeout(pollFeedback, 10000);
            } catch {
                setStatus("Upload failed");
            }
        });

        pollFeedback();
    };

    const bindRow = (row) => {
        const setId = row.dataset.setId;
        if (!setId) {
            return;
        }

        const onChange = async () => {
            try {
                const data = await request(`/workouts/session/${sessionId}/sets/${setId}`, readPayload(row));
                applySummary(data);
            } catch (err) {
                // swallow to avoid breaking the flow
            }
        };

        row.querySelectorAll("[data-field]").forEach((field) => {
            field.addEventListener("change", onChange);
        });

        const restBtn = row.querySelector("[data-rest]");
        if (restBtn) {
            restBtn.addEventListener("click", () => {
                const seconds = parseInt(row.dataset.restSeconds || "0", 10);
                startRest(seconds);
            });
        }

        bindVideoPanel(row, setId);
    };

    document.querySelectorAll("[data-set-id]").forEach(bindRow);
});
