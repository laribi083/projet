// receive-courses.js - Version finale simplifiée

console.log("📚 receive-courses.js chargé");

// Variables globales
let allCourses = [];
let currentFilters = {
    niveau: 'all',
    module: 'all',
    search: ''
};

// ========== INITIALISATION ==========
$(document).ready(function() {
    console.log("✅ DOM chargé");
    loadModules();
    loadCourses();
    updateLastUpdateTime();
    
    $('#filterNiveau').change(function() {
        currentFilters.niveau = $(this).val();
        applyFilters();
    });
    
    $('#filterModule').change(function() {
        currentFilters.module = $(this).val();
        applyFilters();
    });
    
    $('#searchInput').on('input', function() {
        currentFilters.search = $(this).val();
        applyFilters();
    });
    
    $('#resetFilters').click(function() {
        $('#filterNiveau').val('all');
        $('#filterModule').val('all');
        $('#searchInput').val('');
        currentFilters = { niveau: 'all', module: 'all', search: '' };
        applyFilters();
    });
    
    $('#refreshBtn').click(function() {
        loadCourses();
        updateLastUpdateTime();
    });
});

// ========== CHARGEMENT DES DONNÉES ==========
function loadModules() {
    $.ajax({
        url: '/receive-courses/api/modules',
        type: 'GET',
        success: function(modules) {
            const $moduleSelect = $('#filterModule');
            $moduleSelect.empty();
            $moduleSelect.append('<option value="all">📂 All Modules</option>');
            modules.forEach(module => {
                $moduleSelect.append(`<option value="${module}">📁 ${module}</option>`);
            });
        },
        error: function(xhr, status, error) {
            console.error('Error loading modules:', error);
        }
    });
}

function loadCourses() {
    $('#coursesContainer').html(`
        <div class="loading-spinner">
            <i class="fas fa-spinner fa-spin fa-2x"></i>
            <p>Chargement des cours disponibles...</p>
        </div>
    `);
    
    $.ajax({
        url: '/receive-courses/api/all',
        type: 'GET',
        success: function(courses) {
            console.log("✅ Cours chargés:", courses.length);
            allCourses = courses;
            updateStats(courses);
            renderCourses(courses);
        },
        error: function(xhr, status, error) {
            console.error('Error loading courses:', error);
            $('#coursesContainer').html(`
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Erreur lors du chargement des cours. Veuillez réessayer.</p>
                </div>
            `);
        }
    });
}

function applyFilters() {
    let filtered = [...allCourses];
    
    if (currentFilters.niveau !== 'all') {
        filtered = filtered.filter(c => c.niveau === currentFilters.niveau);
    }
    
    if (currentFilters.module !== 'all') {
        filtered = filtered.filter(c => c.module === currentFilters.module);
    }
    
    if (currentFilters.search !== '') {
        const searchLower = currentFilters.search.toLowerCase();
        filtered = filtered.filter(c => 
            (c.title && c.title.toLowerCase().includes(searchLower)) ||
            (c.description && c.description.toLowerCase().includes(searchLower)) ||
            (c.teacherName && c.teacherName.toLowerCase().includes(searchLower))
        );
    }
    
    renderCourses(filtered);
}

function renderCourses(courses) {
    const container = $('#coursesContainer');
    
    if (courses.length === 0) {
        container.html(`
            <div class="empty-state">
                <i class="fas fa-book-open"></i>
                <h3>Aucun cours trouvé</h3>
                <p>Aucun cours ne correspond à vos critères de recherche.</p>
            </div>
        `);
        return;
    }
    
    let html = '';
    courses.forEach(course => {
        const fileIcon = getFileIcon(course);
        const niveauBadge = getNiveauBadge(course.niveau);
        
        html += `
            <div class="course-card" data-course-id="${course.id}">
                <div class="course-icon">${fileIcon}</div>
                <div class="course-info">
                    <div class="course-header">
                        <h3 class="course-title">${escapeHtml(course.title || 'Sans titre')}</h3>
                        ${niveauBadge}
                    </div>
                    <p class="course-description">${escapeHtml(course.description || 'Aucune description')}</p>
                    <div class="course-meta">
                        <span><i class="fas fa-chalkboard-teacher"></i> ${escapeHtml(course.teacherName || 'Professeur')}</span>
                        <span><i class="fas fa-folder"></i> ${escapeHtml(course.module || 'Module')}</span>
                    </div>
                    <div class="course-actions">
                        <a href="/course/${course.id}/download" class="btn-download">
                            <i class="fas fa-download"></i> Download
                        </a>
                        <a href="/course/${course.id}/download" class="btn-view">
                            <i class="fas fa-eye"></i> View Online
                        </a>
                    </div>
                </div>
            </div>
        `;
    });
    
    container.html(html);
}

function updateStats(courses) {
    const total = courses.length;
    const firstYear = courses.filter(c => c.niveau === '1year').length;
    const secondYear = courses.filter(c => c.niveau === '2year').length;
    const thirdYear = courses.filter(c => c.niveau === '3year').length;
    
    $('#totalCourses').text(total);
    $('#total1stYear').text(firstYear);
    $('#total2ndYear').text(secondYear);
    $('#total3rdYear').text(thirdYear);
}

function updateLastUpdateTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('fr-FR');
    $('#lastUpdate').text(timeString);
}

function getFileIcon(course) {
    const fileName = course.fileNames && course.fileNames[0] ? course.fileNames[0] : '';
    if (fileName.endsWith('.pdf')) return '📄';
    return '📘';
}

function getNiveauBadge(niveau) {
    switch(niveau) {
        case '1year': return '<span class="badge badge-1year">1st Year</span>';
        case '2year': return '<span class="badge badge-2year">2nd Year</span>';
        case '3year': return '<span class="badge badge-3year">3rd Year</span>';
        default: return '';
    }
}

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}