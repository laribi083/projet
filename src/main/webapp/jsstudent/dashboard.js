/**
 * dashboard.js - Dashboard étudiant
 * Version complète avec chargement des statistiques et des cours récents
 */

// Attendre le chargement du DOM
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Dashboard chargé');
    
    // Charger les statistiques
    loadStudentStats();
    
    // Charger les cours récents
    loadRecentCourses();
    
    // Animation des cartes
    initCardAnimations();
});

// ========== CHARGEMENT DES STATISTIQUES ==========
async function loadStudentStats() {
    console.log('📊 Chargement des statistiques...');
    
    try {
        const response = await fetch('/student/api/stats');
        
        if (!response.ok) {
            throw new Error('Erreur lors du chargement des statistiques');
        }
        
        const data = await response.json();
        console.log('Statistiques reçues:', data);
        
        if (data.success) {
            updateStatsDisplay(data);
        } else {
            console.error('Erreur:', data.message);
        }
        
    } catch (error) {
        console.error('Erreur:', error);
        setDefaultStats();
    }
}

// ========== MISE À JOUR DE L'AFFICHAGE ==========
function updateStatsDisplay(stats) {
    // Nombre de cours actifs
    const coursActifs = document.getElementById('coursActifs');
    if (coursActifs) {
        animateNumber(coursActifs, 0, stats.activeCourses || 0, 1000);
    }
    
    // Nombre de quiz complétés
    const quizCompletes = document.getElementById('quizCompletes');
    if (quizCompletes) {
        animateNumber(quizCompletes, 0, stats.quizCompletes || 0, 1000);
    }
    
    // Moyenne générale
    const moyenne = document.getElementById('moyenne');
    if (moyenne) {
        const moyenneValue = (stats.moyenne || 0).toFixed(1);
        moyenne.innerHTML = `${moyenneValue}<span style="font-size: 0.8rem;">/20</span>`;
        
        // Changer la couleur selon la note
        if (moyenneValue >= 15) {
            moyenne.style.color = '#0f9d58';
        } else if (moyenneValue >= 10) {
            moyenne.style.color = '#f4b400';
        } else {
            moyenne.style.color = '#e74c3c';
        }
    }
    
    // Heures d'étude
    const heuresEtude = document.getElementById('heuresEtude');
    if (heuresEtude) {
        animateNumber(heuresEtude, 0, stats.heuresEtude || 0, 1000);
        heuresEtude.innerHTML = (stats.heuresEtude || 0) + '<span style="font-size: 0.8rem;">h</span>';
    }
    
    // Cours complétés (dans actions rapides)
    const coursCompletes = document.getElementById('coursCompletes');
    if (coursCompletes) {
        coursCompletes.textContent = stats.coursesCompleted || 0;
    }
    
    // Quiz disponibles
    const quizDisponibles = document.getElementById('quizDisponibles');
    if (quizDisponibles) {
        quizDisponibles.textContent = stats.quizDisponibles || 0;
    }
    
    // Évaluations disponibles
    const evaluationsDisponibles = document.getElementById('evaluationsDisponibles');
    if (evaluationsDisponibles) {
        evaluationsDisponibles.textContent = stats.quizzesPassed || 0;
    }
    
    // Questions disponibles
    const questionsDisponibles = document.getElementById('questionsDisponibles');
    if (questionsDisponibles) {
        questionsDisponibles.textContent = stats.quizzesAvailable || 0;
    }
}

// ========== CHARGEMENT DES COURS RÉCENTS ==========
async function loadRecentCourses() {
    console.log('📚 Chargement des cours récents...');
    
    const container = document.getElementById('recentCoursesContainer');
    if (!container) return;
    
    try {
        const response = await fetch('/student/api/stats');
        
        if (!response.ok) {
            throw new Error('Erreur lors du chargement');
        }
        
        const data = await response.json();
        
        if (data.success && data.recentCourses && data.recentCourses.length > 0) {
            displayRecentCourses(container, data.recentCourses);
        } else {
            displayEmptyRecentCourses(container);
        }
        
    } catch (error) {
        console.error('Erreur:', error);
        displayEmptyRecentCourses(container);
    }
}

// ========== AFFICHAGE DES COURS RÉCENTS ==========
function displayRecentCourses(container, courses) {
    container.innerHTML = '';
    
    courses.forEach(course => {
        const courseCard = document.createElement('div');
        courseCard.className = 'course-card';
        courseCard.onclick = () => window.location.href = '/course/view/' + course.id;
        
        courseCard.innerHTML = `
            <div class="course-icon">
                <i class="fas fa-book-open"></i>
            </div>
            <div class="course-info">
                <h4>${escapeHtml(course.title)}</h4>
                <p>${escapeHtml(course.teacherName)}</p>
                <span class="badge ${course.niveau}">${course.niveau}</span>
            </div>
        `;
        
        container.appendChild(courseCard);
    });
}

// ========== AFFICHAGE SI AUCUN COURS ==========
function displayEmptyRecentCourses(container) {
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-book-open"></i>
            <p>No courses downloaded yet</p>
            <a href="/receive-courses" class="btn-primary">Browse Courses</a>
        </div>
    `;
}

// ========== ANIMATION DES NOMBRES ==========
function animateNumber(element, start, end, duration) {
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= end) {
            element.textContent = end;
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current);
        }
    }, 16);
}

// ========== ANIMATIONS DES CARTES ==========
function initCardAnimations() {
    const cards = document.querySelectorAll('.stat-card, .quick-action-card, .action-item');
    
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
        
        setTimeout(() => {
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

// ========== STATISTIQUES PAR DÉFAUT ==========
function setDefaultStats() {
    const elements = ['coursActifs', 'quizCompletes', 'heuresEtude', 'coursCompletes', 'quizDisponibles', 'evaluationsDisponibles', 'questionsDisponibles'];
    elements.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = '0';
    });
    
    const moyenne = document.getElementById('moyenne');
    if (moyenne) moyenne.innerHTML = '0<span style="font-size: 0.8rem;">/20</span>';
}

// ========== OUVERTURE DU CHATBOT ==========
function openChatbot() {
    // À implémenter selon votre besoin
    alert('Fonctionnalité Chatbot à venir bientôt !');
}

// ========== ÉCHAPPEMENT HTML ==========
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}