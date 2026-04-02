/**
 * receive-courses.js
 * Toute la logique JavaScript pour la page Receive Courses
 */

// ========== VARIABLES GLOBALES ==========
let allCourses = [];
let receivedCourses = [];
let autoRefreshInterval = null;

// ========== FONCTIONS UTILITAIRES ==========

/**
 * Formate la date et l'heure actuelles
 */
function formatDateTime() {
    const now = new Date();
    return now.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
}

/**
 * Met à jour l'affichage de la dernière mise à jour
 */
function updateLastUpdateTime() {
    const lastUpdateSpan = document.getElementById('lastUpdate');
    if (lastUpdateSpan) {
        lastUpdateSpan.textContent = formatDateTime();
    }
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
 * Formate une date pour l'affichage
 */
function formatDate(dateString) {
    if (!dateString) return 'Récemment';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch (error) {
        return 'Récemment';
    }
}

/**
 * Retourne le nom du niveau pour l'affichage
 */
function getLevelName(niveau) {
    const levels = {
        '1year': '🎓 1ère Année',
        '2year': '📖 2ème Année',
        '3year': '🔬 3ème Année'
    };
    return levels[niveau] || niveau;
}

/**
 * Retourne l'icône FontAwesome pour un module
 */
function getCourseIcon(module) {
    const icons = {
        'Algebra 01': 'fa-calculator',
        'Algebra 02': 'fa-calculator',
        'Analysis 01': 'fa-chart-line',
        'Analysis 02': 'fa-chart-line',
        'DataBases and SQL': 'fa-database',
        'web development 01': 'fa-laptop-code',
        'web development 02': 'fa-laptop-code',
        'Git and GitHub': 'fa-code-branch'
    };
    return icons[module] || 'fa-graduation-cap';
}

/**
 * Affiche une notification
 */
function showNotification(message, type) {
    // Supprimer les notifications existantes
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notif => notif.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// ========== GESTION DES COURS ==========

/**
 * Charge les cours depuis l'API
 */
async function loadCourses() {
    try {
        const response = await fetch('/student/api/courses');
        if (!response.ok) throw new Error('Erreur chargement');
        
        const courses = await response.json();
        allCourses = courses.filter(c => c.status === 'ACTIVE');
        
        console.log('📚 Cours chargés:', allCourses.length);
        
        updateStats();
        updateModulesFilter();
        filterAndDisplayCourses();
        updateLastUpdateTime();
        
        return true;
    } catch (error) {
        console.error('Error loading courses:', error);
        showNotification('Erreur lors du chargement des cours', 'error');
        return false;
    }
}

/**
 * Met à jour les statistiques d'affichage
 */
function updateStats() {
    const total = allCourses.length;
    const total1st = allCourses.filter(c => c.niveau === '1year').length;
    const total2nd = allCourses.filter(c => c.niveau === '2year').length;
    const total3rd = allCourses.filter(c => c.niveau === '3year').length;
    
    const totalCoursesSpan = document.getElementById('totalCourses');
    const total1stSpan = document.getElementById('total1stYear');
    const total2ndSpan = document.getElementById('total2ndYear');
    const total3rdSpan = document.getElementById('total3rdYear');
    
    if (totalCoursesSpan) totalCoursesSpan.textContent = total;
    if (total1stSpan) total1stSpan.textContent = total1st;
    if (total2ndSpan) total2ndSpan.textContent = total2nd;
    if (total3rdSpan) total3rdSpan.textContent = total3rd;
}

/**
 * Met à jour le filtre des modules
 */
function updateModulesFilter() {
    const modules = [...new Set(allCourses.map(c => c.module).filter(m => m))];
    const moduleSelect = document.getElementById('filterModule');
    
    if (!moduleSelect) return;
    
    const currentValue = moduleSelect.value;
    
    // Garder l'option "All Modules"
    moduleSelect.innerHTML = '<option value="all">📂 All Modules</option>';
    
    modules.forEach(module => {
        const option = document.createElement('option');
        option.value = module;
        option.textContent = module;
        moduleSelect.appendChild(option);
    });
    
    // Restaurer la valeur précédente si possible
    if (currentValue && modules.includes(currentValue)) {
        moduleSelect.value = currentValue;
    }
}

/**
 * Filtre et affiche les cours selon les critères
 */
function filterAndDisplayCourses() {
    const searchInput = document.getElementById('searchInput');
    const filterNiveauSelect = document.getElementById('filterNiveau');
    const filterModuleSelect = document.getElementById('filterModule');
    
    const searchTerm = searchInput?.value.toLowerCase() || '';
    const filterNiveau = filterNiveauSelect?.value || 'all';
    const filterModule = filterModuleSelect?.value || 'all';
    
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
                <p><strong>Cours reçus (0)</strong></p>
                <p>Aucun cours reçu pour le moment</p>
                <p class="empty-hint">Les cours créés par les enseignants apparaîtront ici automatiquement</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = courses.map(course => {
        const isReceived = receivedCourses.includes(course.id);
        return `
            <div class="course-card">
                <div class="course-image">
                    <i class="fas ${getCourseIcon(course.module)}"></i>
                    <span class="level-badge">${getLevelName(course.niveau)}</span>
                    <span class="course-badge">${course.status === 'ACTIVE' ? 'Disponible' : 'Brouillon'}</span>
                </div>
                <div class="course-content">
                    <h3 class="course-title">${escapeHtml(course.title)}</h3>
                    <p class="course-description">${escapeHtml(course.description ? course.description.substring(0, 120) : '')}${course.description && course.description.length > 120 ? '...' : ''}</p>
                    <div class="course-meta">
                        <div class="meta-item">
                            <i class="fas fa-clock"></i> ${course.totalHours || 8} heures
                        </div>
                        <div class="meta-item">
                            <i class="fas fa-file-alt"></i> ${course.fileNames ? course.fileNames.length : 0} ressources
                        </div>
                    </div>
                    <div>
                        <span class="module-tag"><i class="fas fa-tag"></i> ${escapeHtml(course.module || 'Général')}</span>
                    </div>
                    <div class="teacher-info">
                        <i class="fas fa-chalkboard-user"></i> ${escapeHtml(course.teacherName || 'Professeur')}
                        <span>
                            <i class="fas fa-calendar-alt"></i> ${formatDate(course.createdAt)}
                        </span>
                    </div>
                    <button class="btn-receive ${isReceived ? 'received' : ''}" 
                            onclick="receiveCourse(${course.id}, this)">
                        <i class="fas ${isReceived ? 'fa-check-circle' : 'fa-download'}"></i>
                        ${isReceived ? 'Cours reçu' : 'Recevoir le cours'}
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

// ========== GESTION DES COURS REÇUS ==========

/**
 * Charge les cours reçus depuis localStorage
 */
function loadReceivedCourses() {
    const saved = localStorage.getItem('received_courses');
    if (saved) {
        receivedCourses = JSON.parse(saved);
    }
}

/**
 * Sauvegarde les cours reçus dans localStorage
 */
function saveReceivedCourses() {
    localStorage.setItem('received_courses', JSON.stringify(receivedCourses));
}

/**
 * Marque un cours comme reçu
 */
function receiveCourse(courseId, button) {
    if (receivedCourses.includes(courseId)) {
        showNotification('Vous avez déjà reçu ce cours !', 'info');
        return;
    }
    
    receivedCourses.push(courseId);
    saveReceivedCourses();
    
    button.classList.add('received');
    button.innerHTML = '<i class="fas fa-check-circle"></i> Cours reçu';
    
    const course = allCourses.find(c => c.id === courseId);
    showNotification(`✅ "${course?.title}" a été ajouté à vos cours !`, 'success');
}

// ========== RAFRAÎCHISSEMENT ==========

/**
 * Rafraîchit manuellement la liste des cours
 */
async function refreshCourses() {
    const refreshBtn = document.getElementById('refreshBtn');
    if (refreshBtn) {
        refreshBtn.disabled = true;
        refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Chargement...';
    }
    
    await loadCourses();
    
    if (refreshBtn) {
        refreshBtn.innerHTML = '<i class="fas fa-sync-alt"></i> Actualiser';
        refreshBtn.disabled = false;
    }
    showNotification('Cours actualisés !', 'success');
}

/**
 * Démarre l'auto-refresh toutes les 30 secondes
 */
function startAutoRefresh() {
    if (autoRefreshInterval) clearInterval(autoRefreshInterval);
    autoRefreshInterval = setInterval(() => {
        loadCourses();
    }, 30000);
}

// ========== INITIALISATION ==========

/**
 * Initialise tous les événements
 */
function initEventListeners() {
    const searchInput = document.getElementById('searchInput');
    const filterNiveau = document.getElementById('filterNiveau');
    const filterModule = document.getElementById('filterModule');
    const resetBtn = document.getElementById('resetFilters');
    const refreshBtn = document.getElementById('refreshBtn');
    
    if (searchInput) searchInput.addEventListener('input', filterAndDisplayCourses);
    if (filterNiveau) filterNiveau.addEventListener('change', filterAndDisplayCourses);
    if (filterModule) filterModule.addEventListener('change', filterAndDisplayCourses);
    
    if (resetBtn) {
        resetBtn.addEventListener('click', function() {
            if (filterNiveau) filterNiveau.value = 'all';
            if (filterModule) filterModule.value = 'all';
            if (searchInput) searchInput.value = '';
            filterAndDisplayCourses();
        });
    }
    
    if (refreshBtn) {
        refreshBtn.addEventListener('click', refreshCourses);
    }
}

/**
 * Initialisation au chargement de la page
 */
document.addEventListener('DOMContentLoaded', async function() {
    console.log('📚 Page Receive Courses initialisée');
    
    loadReceivedCourses();
    initEventListeners();
    await loadCourses();
    startAutoRefresh();
});