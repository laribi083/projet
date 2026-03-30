/**
 * Receive Courses Module
 * Gère l'affichage et la réception des cours par les étudiants
 */

// Variables globales
let allCourses = [];
let receivedCourses = [];
let studentNiveau = '1year';

// Constantes pour les icônes par module
const MODULE_ICONS = {
    'Mathematics': 'fa-calculator',
    'Computer Science': 'fa-laptop-code',
    'Physics': 'fa-atom',
    'Chemistry': 'fa-flask',
    'Biology': 'fa-dna',
    'Economics': 'fa-chart-line',
    'Languages': 'fa-language',
    'Engineering': 'fa-cogs'
};

// Niveaux académiques
const LEVEL_NAMES = {
    '1year': '🎓 1st Year',
    '2year': '📖 2nd Year',
    '3year': '🔬 3rd Year'
};

/**
 * Initialisation au chargement de la page
 */
document.addEventListener('DOMContentLoaded', function() {
    // Récupérer le niveau de l'étudiant depuis la session
    const niveauElement = document.querySelector('.user-role span');
    if (niveauElement) {
        studentNiveau = niveauElement.textContent.trim();
        // Normaliser la valeur
        if (studentNiveau === '1st Year') studentNiveau = '1year';
        if (studentNiveau === '2nd Year') studentNiveau = '2year';
        if (studentNiveau === '3rd Year') studentNiveau = '3year';
    }
    
    loadReceivedCourses();
    loadCourses();
    initializeEventListeners();
});

/**
 * Initialise les écouteurs d'événements
 */
function initializeEventListeners() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', filterAndDisplayCourses);
    }
    
    const filterNiveau = document.getElementById('filterNiveau');
    const filterModule = document.getElementById('filterModule');
    
    if (filterNiveau) {
        filterNiveau.addEventListener('change', filterAndDisplayCourses);
    }
    
    if (filterModule) {
        filterModule.addEventListener('change', filterAndDisplayCourses);
    }
}

/**
 * Charge les cours reçus depuis localStorage
 */
function loadReceivedCourses() {
    try {
        const saved = localStorage.getItem(`received_courses_${studentNiveau}`);
        if (saved) {
            receivedCourses = JSON.parse(saved);
        }
    } catch (error) {
        console.error('Error loading received courses:', error);
        receivedCourses = [];
    }
}

/**
 * Sauvegarde les cours reçus dans localStorage
 */
function saveReceivedCourses() {
    try {
        localStorage.setItem(`received_courses_${studentNiveau}`, JSON.stringify(receivedCourses));
    } catch (error) {
        console.error('Error saving received courses:', error);
    }
}

/**
 * Charge tous les cours depuis l'API
 */
function loadCourses() {
    fetch('/receive-courses/api/all')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(courses => {
            allCourses = courses.filter(c => c.status === 'ACTIVE');
            filterAndDisplayCourses();
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            const container = document.getElementById('coursesContainer');
            if (container) {
                container.innerHTML = `
                    <div class="empty-state">
                        <i class="fas fa-exclamation-circle"></i>
                        <p>Error loading courses. Please refresh the page.</p>
                        <button onclick="location.reload()" style="margin-top: 1rem; padding: 0.5rem 1rem; background: #667eea; color: white; border: none; border-radius: 8px; cursor: pointer;">
                            <i class="fas fa-sync-alt"></i> Retry
                        </button>
                    </div>
                `;
            }
        });
}

/**
 * Filtre et affiche les cours selon les critères de recherche
 */
function filterAndDisplayCourses() {
    const searchTerm = document.getElementById('searchInput')?.value.toLowerCase() || '';
    const filterNiveau = document.getElementById('filterNiveau')?.value || 'all';
    const filterModule = document.getElementById('filterModule')?.value || 'all';
    
    let filtered = allCourses.filter(course => {
        const matchesSearch = course.title.toLowerCase().includes(searchTerm) || 
                              course.description.toLowerCase().includes(searchTerm) ||
                              (course.teacherName && course.teacherName.toLowerCase().includes(searchTerm));
        
        const matchesNiveau = filterNiveau === 'all' || course.niveau === filterNiveau;
        const matchesModule = filterModule === 'all' || course.module === filterModule;
        
        return matchesSearch && matchesNiveau && matchesModule;
    });
    
    displayCourses(filtered);
}

/**
 * Affiche les cours dans le conteneur
 */
function displayCourses(courses) {
    const container = document.getElementById('coursesContainer');
    
    if (!container) return;
    
    if (!courses || courses.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-book-open"></i>
                <p>No courses available at the moment.</p>
                <p class="empty-hint">New courses will appear here when professors add them!</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = courses.map(course => {
        const isReceived = receivedCourses.includes(course.id);
        return `
            <div class="course-card" onclick="viewCourse(${course.id})">
                <div class="course-image">
                    <i class="fas ${getCourseIcon(course.module)}"></i>
                    <span class="level-badge">${getLevelName(course.niveau)}</span>
                    <span class="course-badge">${course.status === 'ACTIVE' ? 'Available' : 'Draft'}</span>
                </div>
                <div class="course-content">
                    <h3 class="course-title">${escapeHtml(course.title)}</h3>
                    <p class="course-description">${escapeHtml(course.description.substring(0, 120))}${course.description.length > 120 ? '...' : ''}</p>
                    <div class="course-meta">
                        <div class="meta-item">
                            <i class="fas fa-clock"></i> ${course.totalHours || 8} hours
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-video"></i> ${course.totalVideos || 0} videos
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-file-alt"></i> ${course.fileNames ? course.fileNames.length : 0} resources
                        </div>
                    </div>
                    <div>
                        <span class="module-tag"><i class="fas fa-tag"></i> ${escapeHtml(course.module || 'General')}</span>
                    </div>
                    <div class="teacher-info">
                        <i class="fas fa-chalkboard-user"></i> ${escapeHtml(course.teacherName || 'Professor')}
                        <span style="margin-left: auto;">
                            <i class="fas fa-calendar-alt"></i> ${formatDate(course.createdAt)}
                        </span>
                    </div>
                    <button class="btn-receive ${isReceived ? 'received' : ''}" 
                            onclick="event.stopPropagation(); receiveCourse(${course.id}, this)">
                        <i class="fas ${isReceived ? 'fa-check-circle' : 'fa-download'}"></i>
                        ${isReceived ? 'Course Received' : 'Receive Course'}
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

/**
 * Récupère l'icône correspondant au module
 */
function getCourseIcon(module) {
    return MODULE_ICONS[module] || 'fa-graduation-cap';
}

/**
 * Reçoit un cours et l'ajoute aux cours reçus
 */
function receiveCourse(courseId, button) {
    // Vérifier si déjà reçu
    if (receivedCourses.includes(courseId)) {
        showNotification('You have already received this course!', 'info');
        return;
    }
    
    // Ajouter aux cours reçus
    receivedCourses.push(courseId);
    saveReceivedCourses();
    
    // Mettre à jour le bouton
    button.classList.add('received');
    button.innerHTML = '<i class="fas fa-check-circle"></i> Course Received';
    
    // Afficher notification
    const course = allCourses.find(c => c.id === courseId);
    showNotification(`✅ "${course?.title}" has been added to your courses!`, 'success');
}

/**
 * Affiche les détails d'un cours
 */
function viewCourse(courseId) {
    window.location.href = `/student/course/${courseId}`;
}

/**
 * Retourne le nom complet du niveau
 */
function getLevelName(niveau) {
    return LEVEL_NAMES[niveau] || niveau;
}

/**
 * Formate une date pour l'affichage
 */
function formatDate(dateString) {
    if (!dateString) return 'Recently';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', { 
            day: 'numeric', 
            month: 'short', 
            year: 'numeric' 
        });
    } catch (error) {
        return 'Recently';
    }
}

/**
 * Affiche une notification temporaire
 */
function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';
    
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

/**
 * Échappe les caractères HTML pour éviter les injections XSS
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Rafraîchit la liste des cours
 */
function refreshCourses() {
    loadCourses();
    showNotification('Refreshing courses...', 'info');
}

/**
 * Exporte la liste des cours reçus (utile pour le débogage)
 */
function exportReceivedCourses() {
    console.log('Received courses:', receivedCourses);
    alert(`You have received ${receivedCourses.length} courses. Check console for details.`);
}

// Exposer les fonctions nécessaires globalement
window.receiveCourse = receiveCourse;
window.viewCourse = viewCourse;
window.refreshCourses = refreshCourses;