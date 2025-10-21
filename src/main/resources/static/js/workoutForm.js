console.log("Exercises available:", window.allExercises);
console.log("Cardio activities available:", window.allCardio);

let exerciseBlockIndex = 0;
let cardioIndex = 0;
let totalSetIndex = 0;

/**
 * Updates exercise dropdown options to disable already-selected exercises
 * Prevents the same exercise from being selected multiple times
 */
function updateExerciseDropdowns() {
    try {
        const allSelects = document.querySelectorAll('.exercise-select');
        const selectedValues = new Set();

        allSelects.forEach(select => {
            if (select && select.value) {
                selectedValues.add(select.value);
            }
        });

        allSelects.forEach(select => {
            if (!select || !select.options) return;

            for (const option of select.options) {
                if (option.value && selectedValues.has(option.value) && option.value !== select.value) {
                    option.disabled = true;
                } else {
                    option.disabled = false;
                }
            }
        });
    } catch (error) {
        console.error('Error updating exercise dropdowns:', error);

    }
}


window.addExercise = function() {
    try {
        const container = document.getElementById('exercise-container');

        // Validate container exists
        if (!container) {
            console.error('Exercise container not found');
            alert('Error: Could not add exercise. Please refresh the page.');
            return;
        }

        // Validate exercise data exists
        if (!window.allExercises || !Array.isArray(window.allExercises)) {
            console.error('Exercise data not loaded');
            alert('Error: Exercise data not available. Please refresh the page.');
            return;
        }

        const setContainerId = `sets-container-${exerciseBlockIndex}`;
        const selectId = `exercise-select-${exerciseBlockIndex}`;


        let options = '<option value="">-- Select Exercise --</option>';
        window.allExercises.forEach(ex => {
            if (ex && ex.id && ex.name) {
                // Escape HTML to prevent XSS attacks
                const safeName = String(ex.name).replace(/[&<>"']/g, char => {
                    const escapeMap = {
                        '&': '&amp;',
                        '<': '&lt;',
                        '>': '&gt;',
                        '"': '&quot;',
                        "'": '&#39;'
                    };
                    return escapeMap[char];
                });
                options += `<option value="${ex.id}">${safeName}</option>`;
            }
        });

        const newExerciseBlock = document.createElement('div');
        newExerciseBlock.className = 'border p-3 mb-3';
        newExerciseBlock.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div class="mb-3 w-75">
                    <label class="form-label">Exercise Name</label>
                    <select id="${selectId}" class="form-select exercise-select" data-first-set-added="false">${options}</select>
                </div>
                <button type="button" class="btn-close" aria-label="Remove exercise" onclick="this.parentElement.parentElement.remove(); updateExerciseDropdowns();"></button>
            </div>
            <div id="${setContainerId}"></div>
            <button type="button" class="btn btn-sm btn-outline-secondary mt-2" onclick="addSet('${setContainerId}', '${selectId}')">+ Add Set</button>
        `;

        container.appendChild(newExerciseBlock);
        updateExerciseDropdowns();


        const selectElement = document.getElementById(selectId);
        if (selectElement) {
            selectElement.addEventListener('change', function() {
                try {
                    if (this.value && this.getAttribute('data-first-set-added') === 'false') {
                        addSet(setContainerId, selectId);
                        this.setAttribute('data-first-set-added', 'true');
                    }
                    updateExerciseDropdowns();
                } catch (error) {
                    console.error('Error in exercise select change handler:', error);
                }
            });
        }

        exerciseBlockIndex++;

    } catch (error) {
        console.error('Error adding exercise:', error);
        alert('An error occurred while adding the exercise. Please try again.');
    }
}


window.addSet = function(containerId, selectId) {
    try {

        if (!containerId || !selectId) {
            console.error('Missing containerId or selectId');
            return;
        }

        const setsContainer = document.getElementById(containerId);
        const exerciseSelect = document.getElementById(selectId);


        if (!setsContainer) {
            console.error(`Sets container not found: ${containerId}`);
            alert('Error: Could not add set. Please try removing and re-adding this exercise.');
            return;
        }

        if (!exerciseSelect) {
            console.error(`Exercise select not found: ${selectId}`);
            alert('Error: Could not add set. Please try removing and re-adding this exercise.');
            return;
        }

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
            <div class="col-auto"><button type="button" class="btn-close" aria-label="Remove set" onclick="this.parentElement.parentElement.remove()"></button></div>
        `;

        setsContainer.appendChild(newSetRow);
        totalSetIndex++;

    } catch (error) {
        console.error('Error adding set:', error);
        alert('An error occurred while adding the set. Please try again.');
    }
}


window.addCardio = function() {
    try {
        const container = document.getElementById('cardio-container');


        if (!container) {
            console.error('Cardio container not found');
            alert('Error: Could not add cardio activity. Please refresh the page.');
            return;
        }


        if (!window.allCardio || !Array.isArray(window.allCardio)) {
            console.error('Cardio data not loaded');
            alert('Error: Cardio activity data not available. Please refresh the page.');
            return;
        }


        let options = '<option value="">-- Select Activity --</option>';
        window.allCardio.forEach(ca => {
            if (ca && ca.id && ca.name) {
                // Escape HTML to prevent XSS attacks
                const safeName = String(ca.name).replace(/[&<>"']/g, char => {
                    const escapeMap = {
                        '&': '&amp;',
                        '<': '&lt;',
                        '>': '&gt;',
                        '"': '&quot;',
                        "'": '&#39;'
                    };
                    return escapeMap[char];
                });
                options += `<option value="${ca.id}">${safeName}</option>`;
            }
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
            <div class="col-md-1 d-flex align-items-end"><button type="button" class="btn-close" aria-label="Remove cardio" onclick="this.parentElement.parentElement.remove()"></button></div>
        `;

        container.appendChild(newCardioRow);
        cardioIndex++;

    } catch (error) {
        console.error('Error adding cardio:', error);
        alert('An error occurred while adding the cardio activity. Please try again.');
    }
}


document.addEventListener('DOMContentLoaded', function() {
    try {

        if (!window.allExercises || !window.allCardio) {
            console.warn('Exercise or cardio data not fully loaded');
        }

        console.log('Workout form initialized successfully');
    } catch (error) {
        console.error('Error initializing workout form:', error);
    }
});