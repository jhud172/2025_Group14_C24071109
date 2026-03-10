/* ================================================================
   PREFERENCES WIZARD
   ================================================================ */
var TOTAL_STEPS = 4;

/* ── Step navigation ────────────────────────────────────────── */
function showStep(n) {
    for (var i = 1; i <= TOTAL_STEPS; i++) {
        var panel = document.getElementById('step-' + i);
        if (panel) panel.classList.toggle('pref-step-active', i === n);
    }
    updateStepIndicator(n);
    CURRENT_STEP = n;
    /* Respect prefers-reduced-motion */
    var prefersReduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReduced) {
        window.scrollTo(0, 0);
        /* Move focus to the step heading for screen readers */
        var heading = document.querySelector('#step-' + n + ' h2');
        if (heading) { heading.setAttribute('tabindex', '-1'); heading.focus(); }
    } else {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        var heading = document.querySelector('#step-' + n + ' h2');
        if (heading) { heading.setAttribute('tabindex', '-1'); heading.focus(); }
    }
}

function updateStepIndicator(active) {
    // Update labels
    var labels = document.querySelectorAll('.pref-step-label');
    labels.forEach(function(label, idx) {
        var stepNum = idx + 1;
        label.classList.toggle('active', stepNum === active);
    });
    
    // Update dots and lines
    for (var i = 1; i <= TOTAL_STEPS; i++) {
        var dot = document.getElementById('dot-' + i);
        if (!dot) continue;
        dot.classList.remove('active', 'done');
        dot.removeAttribute('aria-current');
        if (i < active)       { dot.classList.add('done'); }
        else if (i === active) { dot.classList.add('active'); dot.setAttribute('aria-current', 'step'); }
        if (i < TOTAL_STEPS) {
            var line = document.getElementById('line-' + i);
            if (line) {
                line.classList.toggle('done', i < active);
                // Update CSS custom property for gradient animation
                if (i < active) {
                    line.style.setProperty('--progress', '100%');
                } else if (i === active - 1) {
                    // Animate current line
                    setTimeout(function() {
                        line.style.setProperty('--progress', '100%');
                    }, 50);
                } else {
                    line.style.setProperty('--progress', '0%');
                }
            }
        }
    }
}

/* Wire up all next/prev buttons */
document.querySelectorAll('[data-pref-next]').forEach(function(btn) {
    btn.addEventListener('click', function() {
        var target = parseInt(this.dataset.prefNext, 10);
        if (validateStep(CURRENT_STEP)) showStep(target);
    });
});
document.querySelectorAll('[data-pref-prev]').forEach(function(btn) {
    btn.addEventListener('click', function() {
        showStep(parseInt(this.dataset.prefPrev, 10));
    });
});

var step1Next = document.getElementById('step1-next');
if (step1Next) step1Next.addEventListener('click', function() { showStep(2); });
var step1Skip = document.getElementById('step1-skip');
if (step1Skip) step1Skip.addEventListener('click', function() { showStep(2); });

// Ensure returning users can start directly on the edit step.
showStep(CURRENT_STEP);

/* ── Per-step validation ────────────────────────────────────── */
function showStepError(stepId, msg) {
    var errEl = document.getElementById('step' + stepId + '-error');
    if (errEl) {
        if (msg) errEl.textContent = msg;
        errEl.classList.remove('hidden');
        errEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}
function hideStepError(stepId) {
    var errEl = document.getElementById('step' + stepId + '-error');
    if (errEl) errEl.classList.add('hidden');
}

function validateStep(step) {
    if (step === 2) {
        var ok = true;
        ['language', 'theme'].forEach(function(id) {
            var el = document.getElementById(id);
            var wrap = document.getElementById('field-' + id);
            if (el && !el.value) {
                if (wrap) wrap.classList.add('pref-field-error');
                ok = false;
            } else if (wrap) {
                wrap.classList.remove('pref-field-error');
            }
        });
        if (!ok) { showStepError(2); return false; }
        hideStepError(2);
    }
    if (step === 4) {
        var ok = true;
        var sets = document.getElementById('defaultSets');
        if (sets && sets.value !== '' && (parseInt(sets.value) < 1 || parseInt(sets.value) > 20)) {
            document.getElementById('field-defaultSets').classList.add('pref-field-error'); ok = false;
        } else if (sets) { document.getElementById('field-defaultSets').classList.remove('pref-field-error'); }

        var repMin = document.getElementById('defaultRepMin');
        var repMax = document.getElementById('defaultRepMax');
        if (repMin && repMax && repMin.value !== '' && repMax.value !== '' &&
            parseInt(repMin.value) >= parseInt(repMax.value)) {
            document.getElementById('field-defaultRepMax').classList.add('pref-field-error');
            showStepError(4, 'Rep range max must be greater than min.');
            ok = false;
        } else {
            if (document.getElementById('field-defaultRepMin')) document.getElementById('field-defaultRepMin').classList.remove('pref-field-error');
            if (document.getElementById('field-defaultRepMax')) document.getElementById('field-defaultRepMax').classList.remove('pref-field-error');
        }
        if (!ok) { showStepError(4); return false; }
        hideStepError(4);
    }
    return true;
}

/* If server returned validation errors, jump to the smart defaults step */
(function() {
    var errBox = document.getElementById('pref-server-errors');
    if (errBox) showStep(4);
})();

/* ── SlimSelect for physical conditions ─────────────────────────── */
try { 
    new SlimSelect({ 
        select: '#selectElement',
        settings: {
            searchText: 'No conditions found',
            searchPlaceholder: 'Search conditions...',
            searchHighlight: true,
            allowDeselect: true
        }
    }); 
} catch(e) {}

/* ── Pill checkbox visual state ─────────────────────────────────── */
function updatePillStyle(checkbox) {
    var pill = checkbox.closest('.pref-pill');
    if (!pill) return;
    var icon = pill.querySelector('.pref-pill-check');
    if (checkbox.checked) {
        pill.classList.add('pref-pill--active');
        if (icon) icon.classList.remove('hidden');
    } else {
        pill.classList.remove('pref-pill--active');
        if (icon) icon.classList.add('hidden');
    }
}

document.querySelectorAll('.pref-pill-checkbox').forEach(function(cb) {
    updatePillStyle(cb);
    cb.addEventListener('change', function() { updatePillStyle(this); });
});

/* ── Weekly summary metric cap (max 6) ───────────────────────── */
(function() {
    var metricBoxes = Array.prototype.slice.call(document.querySelectorAll('.weekly-metric-checkbox'));
    var limitMsg = document.getElementById('weekly-metric-limit-msg');
    if (!metricBoxes.length) return;

    metricBoxes.forEach(function(cb) {
        cb.addEventListener('change', function() {
            var selected = metricBoxes.filter(function(item){ return item.checked; });
            if (selected.length > 6) {
                cb.checked = false;
                if (typeof updatePillStyle === 'function') updatePillStyle(cb);
                if (limitMsg) {
                    limitMsg.classList.remove('hidden');
                    setTimeout(function(){ limitMsg.classList.add('hidden'); }, 2200);
                }
            }
        });
    });
})();

/* ── Single-select dropdown <-> hidden checkboxes sync ──────────── */
document.querySelectorAll('.pref-single-select').forEach(function(sel) {
    var cat = sel.dataset.category;
    var hc  = document.querySelector('.pref-hidden-checks[data-category="' + cat + '"]');
    if (!hc) return;
    sel.addEventListener('change', function() {
        hc.querySelectorAll('.pref-hidden-checkbox').forEach(function(cb) {
            cb.checked = (cb.value === sel.value);
        });
    });
});

/* ── "Other" pill ───────────────────────────────────────────────── */
document.querySelectorAll('.pref-other-trigger').forEach(function(cb) {
    var wrap = document.getElementById(cb.dataset.target);
    var pill = cb.closest('.pref-other-pill');
    cb.addEventListener('change', function() {
        if (wrap) wrap.classList.toggle('visible', cb.checked);
        if (pill) {
            if (cb.checked) {
                pill.classList.add('pref-pill--active');
                var inp = wrap && wrap.querySelector('input[type=text]');
                if (inp) setTimeout(function(){ inp.focus(); }, 50);
            } else {
                pill.classList.remove('pref-pill--active');
                var inp2 = wrap && wrap.querySelector('input[type=text]');
                if (inp2) inp2.value = '';
            }
        }
    });
});

/* ── Quick-setup presets ────────────────────────────────────────── */
var PRESETS = {
    'weight-loss-beginner': {
        label: 'Weight Loss \u2013 Beginner',
        preferences: ['Weight Loss', 'Beginner (New to exercise)', '3\u20134 times per week',
                      'Home Workouts', 'Low Impact', 'Calorie Deficit (Cutting)', 'High Protein', 'Prioritise Recovery Days'],
        sets: 3, repMin: 12, repMax: 20, equipment: ['bodyweight'],
        calories: 1800, protein: 140, carbs: 160, fat: 60
    },
    'weight-loss-intermediate': {
        label: 'Weight Loss \u2013 Intermediate',
        preferences: ['Weight Loss', 'Intermediate (Exercising regularly)', '3\u20134 times per week',
                      'Gym Workouts', 'HIIT / High Intensity', 'Calorie Deficit (Cutting)', 'High Protein'],
        sets: 4, repMin: 10, repMax: 15, equipment: ['dumbbell', 'machine'],
        calories: 2000, protein: 160, carbs: 180, fat: 65
    },
    'muscle-building': {
        label: 'Muscle Building',
        preferences: ['Muscle Gain / Hypertrophy', 'Intermediate (Exercising regularly)',
                      '3\u20134 times per week', 'Gym Workouts', 'Solo Training', 'Calorie Surplus (Bulking)', 'High Protein'],
        sets: 4, repMin: 8, repMax: 12, equipment: ['barbell', 'dumbbell', 'machine'],
        calories: 3000, protein: 180, carbs: 300, fat: 90
    },
    'strength': {
        label: 'Strength Training',
        preferences: ['Increase Strength', 'Intermediate (Exercising regularly)',
                      '3\u20134 times per week', 'Gym Workouts', 'Solo Training', 'High Protein'],
        sets: 5, repMin: 3, repMax: 6, equipment: ['barbell', 'dumbbell'],
        calories: 2800, protein: 175, carbs: 280, fat: 85
    },
    'endurance': {
        label: 'Endurance / Cardio',
        preferences: ['Improve Endurance', 'Intermediate (Exercising regularly)',
                      '5+ times per week', 'Outdoor Activities', 'Solo Training', 'Active Recovery (light movement)'],
        sets: 3, repMin: 15, repMax: 25, equipment: ['bodyweight'],
        calories: 2600, protein: 130, carbs: 320, fat: 70
    },
    'flexibility': {
        label: 'Flexibility & Mobility',
        preferences: ['Improve Flexibility & Mobility', 'Beginner (New to exercise)',
                      '3\u20134 times per week', 'Home Workouts', 'Low Impact', 'Regular Stretching / Yoga', 'Active Recovery (light movement)'],
        sets: 3, repMin: 10, repMax: 20, equipment: ['bodyweight'],
        calories: null, protein: null, carbs: null, fat: null
    },
    'general-fitness': {
        label: 'General Health & Fitness',
        preferences: ['General Health & Fitness', 'Beginner (New to exercise)',
                      '3\u20134 times per week', 'Gym Workouts', 'Solo Training', 'Prioritise Recovery Days'],
        sets: 3, repMin: 10, repMax: 15, equipment: ['bodyweight', 'dumbbell'],
        calories: 2200, protein: 130, carbs: 230, fat: 70
    },
    'home-beginner': {
        label: 'Home Workout \u2013 Beginner',
        preferences: ['General Health & Fitness', 'Beginner (New to exercise)',
                      '1\u20132 times per week', 'Home Workouts', 'Low Impact', 'Prioritise Recovery Days'],
        sets: 3, repMin: 10, repMax: 15, equipment: ['bodyweight'],
        calories: null, protein: null, carbs: null, fat: null
    }
};

var EQUIP_MAP = {
    bodyweight: 'preferredEquipmentBodyweight', dumbbell: 'preferredEquipmentDumbbell',
    barbell: 'preferredEquipmentBarbell',       machine:  'preferredEquipmentMachine',
    bands:   'preferredEquipmentBands',         kettlebell:'preferredEquipmentKettlebell',
    cable: 'preferredEquipmentCable',           pullupBar: 'preferredEquipmentPullupBar',
    jumpRope: 'preferredEquipmentJumpRope',     medicineBall: 'preferredEquipmentMedicineBall',
    foamRoller: 'preferredEquipmentFoamRoller', trx: 'preferredEquipmentTrx'
};

var PRESET_PENDING = null;

function showPresetPreview(key) {
    var p = PRESETS[key];
    if (!p) { hidePresetPreview(); return; }
    PRESET_PENDING = key;
    document.getElementById('preview-title').textContent = p.label;
    var body = document.getElementById('preview-body');
    body.innerHTML = '';
    var rows = [
        ['Goal',      p.preferences[0] || '\u2014'],
        ['Level',     p.preferences[1] || '\u2014'],
        ['Frequency', p.preferences[2] || '\u2014'],
        ['Sets',      p.sets],
        ['Reps',      p.repMin + '\u2013' + p.repMax],
        ['Calories',  p.calories != null ? p.calories + ' kcal' : '\u2014'],
        ['Equipment', p.equipment.map(function(e){ return e.charAt(0).toUpperCase()+e.slice(1); }).join(', ')]
    ];
    rows.forEach(function(row) {
        var lbl = document.createElement('div');
        lbl.className = 'font-medium text-slate-500 dark:text-slate-400';
        lbl.textContent = row[0];
        var val = document.createElement('div');
        val.className = 'text-slate-800 dark:text-slate-200';
        val.textContent = row[1];
        body.appendChild(lbl);
        body.appendChild(val);
    });
    document.getElementById('preset-applied-msg').classList.add('hidden');
    var applyBtn = document.getElementById('preset-apply-btn');
    if (applyBtn) { applyBtn.textContent = 'Apply preset & continue'; applyBtn.disabled = false; applyBtn.className = 'rounded-lg bg-emerald-600 px-4 py-1.5 text-xs font-semibold text-white transition-colors hover:bg-emerald-700'; }
    document.getElementById('preset-preview').classList.add('visible');
}

function hidePresetPreview() {
    PRESET_PENDING = null;
    document.getElementById('preset-preview').classList.remove('visible');
}

function applyPresetData(key) {
    var p = PRESETS[key]; if (!p) return;
    /* Uncheck all */
    document.querySelectorAll('input.pref-hidden-checkbox, input.pref-pill-checkbox').forEach(function(cb) {
        cb.checked = false; updatePillStyle(cb);
    });
    /* Apply matching preferences */
    p.preferences.forEach(function(desc) {
        document.querySelectorAll('.pref-pill-checkbox[data-description="' + desc + '"]').forEach(function(cb) {
            cb.checked = true; updatePillStyle(cb);
        });
        document.querySelectorAll('.pref-hidden-checkbox[data-description="' + desc + '"]').forEach(function(cb) {
            cb.checked = true;
        });
    });
    /* Sync single-select dropdowns */
    document.querySelectorAll('.pref-single-select').forEach(function(sel) {
        var cat = sel.dataset.category;
        var hc = document.querySelector('.pref-hidden-checks[data-category="' + cat + '"]');
        if (!hc) return;
        var checked = hc.querySelector('.pref-hidden-checkbox:checked');
        sel.value = checked ? checked.value : '';
    });
    /* Smart defaults */
    function sf(id, val) { var el=document.getElementById(id); if(el) el.value = (val!==null && val!==undefined) ? val : ''; }
    sf('defaultSets', p.sets); sf('defaultRepMin', p.repMin); sf('defaultRepMax', p.repMax);
    sf('macroTargetCalories', p.calories); sf('macroTargetProtein', p.protein);
    sf('macroTargetCarbs', p.carbs); sf('macroTargetFat', p.fat);
    /* Equipment */
    Object.keys(EQUIP_MAP).forEach(function(key) {
        var cb = document.querySelector('input[name="' + EQUIP_MAP[key] + '"]');
        if (!cb) return;
        cb.checked = p.equipment.indexOf(key) !== -1;
        updatePillStyle(cb);
    });
}

var quickPreset = document.getElementById('quickPreset');
if (quickPreset) quickPreset.addEventListener('change', function() {
    if (this.value) showPresetPreview(this.value);
    else hidePresetPreview();
});

var applyBtn = document.getElementById('preset-apply-btn');
if (applyBtn) applyBtn.addEventListener('click', function() {
    if (!PRESET_PENDING) return;
    applyPresetData(PRESET_PENDING);
    document.getElementById('preset-applied-msg').classList.remove('hidden');
    applyBtn.textContent = '\u2713 Applied';
    applyBtn.disabled = true;
    applyBtn.className = 'rounded-lg bg-slate-400 px-4 py-1.5 text-xs font-semibold text-white cursor-default';
    setTimeout(function() { showStep(2); }, 700);
});

var cancelBtn = document.getElementById('preset-cancel-btn');
if (cancelBtn) cancelBtn.addEventListener('click', function() {
    if (quickPreset) quickPreset.value = '';
    hidePresetPreview();
});

var editBtn = document.getElementById('preset-edit-btn');
if (editBtn) editBtn.addEventListener('click', function() {
    if (quickPreset) quickPreset.value = '';
    hidePresetPreview();
});

/* ── Equipment "Other" conditional input ──────────────────────── */
var equipOtherCheckbox = document.getElementById('equipment-other-checkbox');
var equipOtherContainer = document.getElementById('equipment-other-container');

function toggleEquipmentOtherInput() {
    if (equipOtherCheckbox && equipOtherContainer) {
        if (equipOtherCheckbox.checked) {
            equipOtherContainer.classList.remove('hidden');
        } else {
            equipOtherContainer.classList.add('hidden');
        }
    }
}

// Initialize on page load
toggleEquipmentOtherInput();

// Listen for changes
if (equipOtherCheckbox) {
    equipOtherCheckbox.addEventListener('change', toggleEquipmentOtherInput);
}
