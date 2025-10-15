console.log("Exercises available:", window.allExercises);
console.log("Cardio activities available:", window.allCardio);

let exerciseBlockIndex = 0;
let cardioIndex = 0;
let totalSetIndex = 0;


function updateExerciseDropdowns() {
    const allSelects = document.querySelectorAll('.exercise-select');
    const selectedValues = new Set();

    allSelects.forEach(select => {
        if (select.value) {
            selectedValues.add(select.value);
        }
    });

    allSelects.forEach(select => {
        for (const option of select.options) {
            if (option.value && selectedValues.has(option.value) && option.value !== select.value) {
                option.disabled = true;
            } else {
                option.disabled = false;
            }
        }
    });
}


window.addExercise = function() {
    const container = document.getElementById('exercise-container');
    const setContainerId = `sets-container-${exerciseBlockIndex}`;
    const selectId = `exercise-select-${exerciseBlockIndex}`;

    let options = '<option value="">-- Select Exercise --</option>';
    window.allExercises.forEach(ex => {
        options += `<option value="${ex.id}">${ex.name}</option>`;
    });

    const newExerciseBlock = document.createElement('div');
    newExerciseBlock.className = 'border p-3 mb-3';
    newExerciseBlock.innerHTML = `
        <div class="d-flex justify-content-between align-items-center">
            <div class="mb-3 w-75">
                <label class="form-label">Exercise Name</label>
                <select id="${selectId}" class="form-select exercise-select" data-first-set-added="false">${options}</select>
            </div>
            <button type="button" class="btn-close" onclick="this.parentElement.parentElement.remove(); updateExerciseDropdowns();"></button>
        </div>
        <div id="${setContainerId}"></div>
        <button type="button" class="btn btn-sm btn-outline-secondary mt-2" onclick="addSet('${setContainerId}', '${selectId}')">+ Add Set</button>
    `;
    container.appendChild(newExerciseBlock);
    updateExerciseDropdowns();

    document.getElementById(selectId).addEventListener('change', function() {
        if (this.value && this.getAttribute('data-first-set-added') === 'false') {
            addSet(setContainerId, selectId);
            this.setAttribute('data-first-set-added', 'true');
        }
        updateExerciseDropdowns();
    });

    exerciseBlockIndex++;
}

window.addSet = function(containerId, selectId) {
    const setsContainer = document.getElementById(containerId);
    const exerciseSelect = document.getElementById(selectId);
    const selectedExerciseId = exerciseSelect.value;

    if (!selectedExerciseId) {
        alert("Please select an exercise first.");
        return;
    }

    const setIndexInBlock = setsContainer.children.length;
    const namePrefix = `exerciseSets[${totalSetIndex}]`;

    const newSetRow = document.createElement('div');
    newSetRow.className = 'row g-3 align-items-center mb-2';
    newSetRow.innerHTML = `
        <input type="hidden" name="${namePrefix}.exerciseListId" value="${selectedExerciseId}" />
        <input type="hidden" name="${namePrefix}.setNumber" value="${setIndexInBlock + 1}" />
        <div class="col-auto"><strong>Set ${setIndexInBlock + 1}</strong></div>
        <div class="col"><input type="number" step="0.01" name="${namePrefix}.weight" class="form-control" placeholder="Weight" required></div>
        <div class="col"><input type="number" name="${namePrefix}.reps" class="form-control" placeholder="Reps" required></div>
        <div class="col"><input type="text" name="${namePrefix}.notes" class="form-control" placeholder="Notes"></div>
        <div class="col-auto"><button type="button" class="btn-close" onclick="this.parentElement.parentElement.remove()"></button></div>
    `;
    setsContainer.appendChild(newSetRow);
    totalSetIndex++;
}

window.addCardio = function() {
    const container = document.getElementById('cardio-container');
    let options = '<option value="">-- Select Activity --</option>';
    window.allCardio.forEach(ca => {
        options += `<option value="${ca.id}">${ca.name}</option>`;
    });

    const newCardioRow = document.createElement('div');
    newCardioRow.className = 'row g-3 align-items-center mb-2 border p-3';
    newCardioRow.innerHTML = `
        <div class="col-md-3">
            <label class="form-label">Activity</label>
            <select name="cardioSessions[${cardioIndex}].cardioListId" class="form-select" required>${options}</select>
        </div>
        <div class="col-md-2"><label class="form-label">Duration (min)</label><input type="number" name="cardioSessions[${cardioIndex}].durationMinutes" class="form-control" required></div>
        <div class="col-md-2"><label class="form-label">Distance</label><input type="number" step="0.01" name="cardioSessions[${cardioIndex}].distance" class="form-control"></div>
        <div class="col-md-4"><label class="form-label">Notes</label><input type="text" name="cardioSessions[${cardioIndex}].notes" class="form-control"></div>
        <div class="col-md-1 d-flex align-items-end"><button type="button" class="btn-close" onclick="this.parentElement.parentElement.remove()"></button></div>
    `;
    container.appendChild(newCardioRow);
    cardioIndex++;
}