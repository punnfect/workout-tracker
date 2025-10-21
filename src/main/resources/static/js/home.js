// home.js - Handles workout search and filtering on the home page

// Debounce function to delay search execution
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Search and filter workouts
function filterWorkouts() {
    const titleSearch = document.getElementById('titleSearch').value.toLowerCase().trim();
    const dateSearch = document.getElementById('dateSearch').value;
    const workoutItems = document.querySelectorAll('.workout-item');
    const noResultsMessage = document.getElementById('noResultsMessage');
    const resultCount = document.getElementById('resultCount');

    let visibleCount = 0;

    workoutItems.forEach(item => {
        const title = item.getAttribute('data-title').toLowerCase();
        const date = item.getAttribute('data-date');

        let matchesTitle = true;
        let matchesDate = true;

        // Check title match
        if (titleSearch !== '') {
            matchesTitle = title.includes(titleSearch);
        }

        // Check date match
        if (dateSearch !== '') {
            matchesDate = date === dateSearch;
        }

        // Show or hide based on both criteria
        if (matchesTitle && matchesDate) {
            item.classList.remove('d-none');
            visibleCount++;
        } else {
            item.classList.add('d-none');
        }
    });

    // Show/hide "no results" message
    if (visibleCount === 0) {
        noResultsMessage.style.display = 'block';
    } else {
        noResultsMessage.style.display = 'none';
    }

    // Update result count
    if (titleSearch !== '' || dateSearch !== '') {
        resultCount.textContent = `Showing ${visibleCount} workout${visibleCount !== 1 ? 's' : ''}`;
    } else {
        resultCount.textContent = '';
    }
}

// Set up event listeners
document.addEventListener('DOMContentLoaded', function() {
    const titleSearch = document.getElementById('titleSearch');
    const dateSearch = document.getElementById('dateSearch');
    const clearButton = document.getElementById('clearSearch');

    if (titleSearch) {
        // Debounced title search (1 second delay)
        const debouncedFilter = debounce(filterWorkouts, 1000);
        titleSearch.addEventListener('input', debouncedFilter);
    }

    if (dateSearch) {
        // Immediate date search
        dateSearch.addEventListener('change', filterWorkouts);
    }

    if (clearButton) {
        clearButton.addEventListener('click', function() {
            if (titleSearch) titleSearch.value = '';
            if (dateSearch) dateSearch.value = '';
            filterWorkouts();
        });
    }
});