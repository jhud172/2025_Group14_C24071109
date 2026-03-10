(function () {
    "use strict";

    var ROLE = (typeof tutorialRole !== 'undefined') ? tutorialRole : 'CLIENT';

    // ── Step definitions ─────────────────────────────────────────────────────
    var CLIENT_STEPS = [
        {
            icon: "👋",
            title: "Welcome to One To One!",
            body: "We're thrilled to have you here. This quick tour will help you get the most out of your fitness journey. You can skip at any time."
        },
        {
            icon: "📅",
            title: "Your Personal Calendar",
            body: "Plan and track workouts, goals, and daily tasks in one place. Head to <strong>Calendar</strong> to add your first session."
        },
        {
            icon: "💪",
            title: "Log Workouts",
            body: "Record every exercise set, rep, and weight. Your progress is tracked automatically so you can see how far you've come."
        },
        {
            icon: "🎯",
            title: "Set Goals",
            body: "Create personal goals — weight, strength, cardio — and check in weekly to stay on track."
        },
        {
            icon: "🏆",
            title: "Earn Points & Level Up",
            body: "Complete workouts and tasks to earn XP and climb through achievement levels. Fitness just got more fun!"
        },
        {
            icon: "🚀",
            title: "You're all set!",
            body: "Your dashboard is ready. Start by logging today's workout or exploring your calendar. Good luck! 💪"
        }
    ];

    var TRAINER_STEPS = [
        {
            icon: "👋",
            title: "Welcome, Trainer!",
            body: "Your professional hub is ready. Let's take a quick look at the tools that will help you manage and grow your client base."
        },
        {
            icon: "🔑",
            title: "Your Trainer Code",
            body: "Share your unique Trainer Code with clients so they can connect to you. Find it on your <strong>Profile</strong> page."
        },
        {
            icon: "👥",
            title: "Manage Clients",
            body: "View all your linked clients, track their progress, and assign personalised workouts from the <strong>Clients</strong> section."
        },
        {
            icon: "📋",
            title: "Exercise & Programme Library",
            body: "Build reusable workout templates and programmes in your <strong>Library</strong>, then assign them to clients in seconds."
        },
        {
            icon: "📊",
            title: "Check-ins & Assessments",
            body: "Schedule weekly check-ins, review client assessments, and adjust their coaching phase as they progress."
        },
        {
            icon: "🚀",
            title: "You're ready to coach!",
            body: "Your trainer dashboard is set up and waiting. Add your first client or create a programme to get started."
        }
    ];

    var GYM_STEPS = [
        {
            icon: "👋",
            title: "Welcome to your Gym Dashboard!",
            body: "Everything you need to manage your gym is here. Let's walk through the key features together."
        },
        {
            icon: "🏋️",
            title: "Gym Profile",
            body: "Keep your gym information up to date — name, location, description, and opening hours — so members always have the latest details."
        },
        {
            icon: "👥",
            title: "Member Management",
            body: "View all gym members, check their subscription status, and manage access from the <strong>Members</strong> section."
        },
        {
            icon: "🧑‍🏫",
            title: "Trainer Management",
            body: "Approve and manage trainers associated with your gym. Keep your team organised and verified."
        },
        {
            icon: "📊",
            title: "Analytics",
            body: "Track membership trends, activity levels, and revenue to make informed decisions about your gym."
        },
        {
            icon: "🚀",
            title: "You're all set!",
            body: "Your gym dashboard is live. Start by completing your gym profile or inviting your first trainer."
        }
    ];

    var steps = ROLE === 'TRAINER' ? TRAINER_STEPS
              : ROLE === 'GYM'     ? GYM_STEPS
              :                      CLIENT_STEPS;

    var current = 0;

    // ── DOM refs ─────────────────────────────────────────────────────────────
    var icon      = document.getElementById('tutorial-icon');
    var title     = document.getElementById('tutorial-title');
    var body      = document.getElementById('tutorial-body');
    var progress  = document.getElementById('tutorial-progress-bar');
    var stepLabel = document.getElementById('tutorial-step-label');
    var nextBtn   = document.getElementById('tutorial-next-btn');
    var backBtn   = document.getElementById('tutorial-back-btn');
    var dotsEl    = document.getElementById('tutorial-dots');
    var finishForm= document.getElementById('tutorial-finish-form');

    // ── Build dots ────────────────────────────────────────────────────────────
    steps.forEach(function (_, i) {
        var dot = document.createElement('button');
        dot.type = 'button';
        dot.setAttribute('aria-label', 'Go to step ' + (i + 1));
        dot.className = 'h-2 w-2 rounded-full transition-all duration-300 ' +
                        (i === 0 ? 'w-4 bg-emerald-500 dark:bg-emerald-400' : 'bg-slate-300 dark:bg-slate-600');
        dot.addEventListener('click', function () { goTo(i); });
        dotsEl.appendChild(dot);
    });

    function render() {
        var step = steps[current];
        icon.textContent  = step.icon;
        title.textContent = step.title;
        body.innerHTML    = step.body;

        var pct = steps.length > 1 ? (current / (steps.length - 1)) * 100 : 100;
        progress.style.width = pct + '%';

        stepLabel.textContent = 'Step ' + (current + 1) + ' of ' + steps.length;

        var isLast = current === steps.length - 1;
        nextBtn.textContent = isLast ? 'Get started 🚀' : 'Next →';

        backBtn.classList.toggle('hidden', current === 0);

        // Dots
        var dots = dotsEl.querySelectorAll('button');
        dots.forEach(function (d, i) {
            if (i === current) {
                d.className = 'h-2 w-4 rounded-full bg-emerald-500 dark:bg-emerald-400 transition-all duration-300';
            } else {
                d.className = 'h-2 w-2 rounded-full bg-slate-300 dark:bg-slate-600 transition-all duration-300';
            }
        });
    }

    function goTo(index) {
        current = index;
        render();
    }

    window.tutorialNext = function () {
        if (current < steps.length - 1) {
            current++;
            render();
        } else {
            finishForm.submit();
        }
    };

    window.tutorialBack = function () {
        if (current > 0) {
            current--;
            render();
        }
    };

    // Keyboard navigation
    document.addEventListener('keydown', function (e) {
        if (e.key === 'ArrowRight' || e.key === 'Enter') { window.tutorialNext(); }
        else if (e.key === 'ArrowLeft') { window.tutorialBack(); }
        else if (e.key === 'Escape') { finishForm.submit(); }
    });

    render();
}());
