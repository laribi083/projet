// teacher-dashboard.js - Scripts pour le dashboard enseignant

let currentCourseToDelete = null;
let currentStats = {
    totalCourses: 0,
    totalQuizzes: 0,
    totalStudents: 0
};



/**
 * Créer un nouveau cours
 */
function createCourse() {
    window.location.href = '/teacher/create-course';
}

/**
 * Modifier un cours existant
 * @param {number} courseId - L'identifiant du cours
 */
function editCourse(courseId) {
    window.location.href = `/teacher/edit-course/${courseId}`;
}

/**
 * Voir le quiz d'un cours
 * @param {number} courseId - L'identifiant du cours
 */
function viewQuiz(courseId) {
    window.location.href = `/teacher/quiz/${courseId}`;
}

/**
 * Déconnexion de l'utilisateur
 */
function logout() {
    window.location.href = '/logout';
}

// ==================== GESTION DU MODAL DE SUPPRESSION ====================

/**
 * Afficher le modal de confirmation de suppression
 * @param {number} courseId - L'identifiant du cours
 * @param {string} courseName - Le nom du cours
 */
function showDeleteModal(courseId, courseName) {
    currentCourseToDelete = { id: courseId, name: courseName };
    const modal = document.getElementById('deleteModal');
    const courseNameSpan = document.getElementById('courseNameToDelete');
    
    if (modal && courseNameSpan) {
        courseNameSpan.textContent = courseName;
        modal.style.display = 'flex';
    }
}

/**
 * Fermer le modal de suppression
 */
function closeModal() {
    const modal = document.getElementById('deleteModal');
    if (modal) {
        modal.style.display = 'none';
        currentCourseToDelete = null;
    }
}

/**
 * Confirmer et exécuter la suppression du cours
 */
async function confirmDelete() {
    if (!currentCourseToDelete) return;
    
    try {
        const response = await fetch(`/api/courses/${currentCourseToDelete.id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            showNotification(`✅ Cours "${currentCourseToDelete.name}" supprimé avec succès`, 'success');
            // Recharger la liste des cours
            loadCourses();
            loadStats();
        } else {
            const error = await response.text();
            showNotification(`❌ Erreur: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Erreur lors de la suppression:', error);
        showNotification('❌ Erreur de connexion au serveur', 'error');
    }
    closeModal();
}

// ==================== CHARGEMENT DES DONNÉES ====================

/**
 * Charger les statistiques du tableau de bord
 */
async function loadStats() {
    try {
        const response = await fetch('/api/teacher/stats');
        if (response.ok) {
            const data = await response.json();
            currentStats = {
                totalCourses: data.totalCourses || 0,
                totalQuizzes: data.totalQuizzes || 0,
                totalStudents: data.totalStudents || 0
            };
            
            // Mettre à jour l'affichage
            document.getElementById('totalCourses').textContent = currentStats.totalCourses;
            document.getElementById('totalQuizzes').textContent = currentStats.totalQuizzes;
            document.getElementById('totalStudents').textContent = currentStats.totalStudents;
        }
    } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
    }
}

/**
 * Charger la liste des cours
 */
async function loadCourses() {
    try {
        const response = await fetch('/api/teacher/courses');
        const container = document.getElementById('coursesGrid');
        
        if (!container) return;
        
        if (response.ok) {
            const courses = await response.json();
            
            if (courses && courses.length > 0) {
                container.innerHTML = courses.map(course => createCourseCard(course)).join('');
            } else {
                container.innerHTML = `
                    <div class="empty-state">
                        📭 Aucun cours pour le moment<br>
                        Cliquez sur "Nouveau cours" pour commencer
                    </div>
                `;
            }
        } else {
            container.innerHTML = `
                <div class="empty-state">
                    ⚠️ Erreur lors du chargement des cours<br>
                    Veuillez réessayer plus tard
                </div>
            `;
        }
    } catch (error) {
        console.error('Erreur lors du chargement des cours:', error);
        const container = document.getElementById('coursesGrid');
        if (container) {
            container.innerHTML = `
                <div class="empty-state">
                    ⚠️ Erreur de connexion au serveur<br>
                    Veuillez vérifier votre connexion
                </div>
            `;
        }
    }
}

/**
 * Créer une carte de cours HTML
 * @param {Object} course - Les données du cours
 * @returns {string} Le HTML de la carte
 */
function createCourseCard(course) {
    const icon = course.icon || getRandomIcon();
    const status = course.status || getCourseStatus(course);
    const lessonsCount = course.lessonsCount || 0;
    const studentsCount = course.studentsCount || 0;
    const description = course.description || 'Aucune description';
    
    return `
        <div class="course-card" data-course-id="${course.id}">
            <div class="course-image">
                ${icon}
                <span class="course-badge">${status}</span>
            </div>
            <div class="course-content">
                <h3 class="course-title">${escapeHtml(course.title)}</h3>
                <p class="course-description">${escapeHtml(description)}</p>
                <div class="course-meta">
                    <div class="course-stats">
                        <span>📖 ${lessonsCount} leçons</span>
                        <span>👥 ${studentsCount} étudiants</span>
                    </div>
                    <div class="course-actions">
                        <button class="btn-view" onclick="viewQuiz(${course.id})">📘 Quiz</button>
                        <button class="btn-edit" onclick="editCourse(${course.id})">✏️</button>
                        <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')">🗑</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * Obtenir un statut aléatoire pour le cours
 * @param {Object} course - Les données du cours
 * @returns {string} Le statut du cours
 */
function getCourseStatus(course) {
    const statuses = ['En cours', 'Brouillon', 'Publié', 'Archivé'];
    return course.status || statuses[Math.floor(Math.random() * statuses.length)];
}

/**
 * Obtenir une icône aléatoire pour le cours
 * @returns {string} L'icône du cours
 */
function getRandomIcon() {
    const icons = ['📘', '📙', '📗', '📕', '📓', '📔', '📒'];
    return icons[Math.floor(Math.random() * icons.length)];
}

// ==================== FONCTIONS UTILITAIRES ====================

/**
 * Échapper les caractères HTML pour éviter les injections XSS
 * @param {string} text - Le texte à échapper
 * @returns {string} Le texte échappé
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Afficher une notification
 * @param {string} message - Le message à afficher
 * @param {string} type - Le type de notification (success, error, info)
 */
function showNotification(message, type = 'info') {
    // Créer un élément de notification
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 10px;
        background: ${type === 'success' ? '#4caf50' : type === 'error' ? '#f44336' : '#2196f3'};
        color: white;
        z-index: 3000;
        animation: slideIn 0.3s ease;
        box-shadow: 0 5px 15px rgba(0,0,0,0.2);
    `;
    
    document.body.appendChild(notification);
    
    // Supprimer la notification après 3 secondes
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// ==================== GESTION DES ÉVÉNEMENTS ====================

/**
 * Fermer le modal en cliquant en dehors
 */
window.onclick = function(event) {
    const modal = document.getElementById('deleteModal');
    if (event.target === modal) {
        closeModal();
    }
};

/**
 * Initialisation au chargement de la page
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard Teacher chargé');
    loadStats();
    loadCourses();
});

// Ajouter les animations CSS pour les notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);