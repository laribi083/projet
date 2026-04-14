/**
 * dashboard.js - Admin Dashboard JavaScript
 * Version finale avec chargement des vraies statistiques
 */

// ========== TOGGLE SIDEBAR ==========
const toggleBtn = document.getElementById('toggleBtn');
const sidebar = document.getElementById('sidebar');

if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
    });
}

// ========== ANIMATION DES NOMBRES ==========
function animateNumber(elementId, targetNumber) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    let current = 0;
    const increment = Math.ceil(targetNumber / 50);
    const interval = setInterval(() => {
        current += increment;
        if (current >= targetNumber) {
            current = targetNumber;
            clearInterval(interval);
        }
        element.innerText = current;
    }, 20);
}

// ========== CHARGER LES STATISTIQUES DEPUIS L'API ==========
async function loadStats() {
    try {
        const response = await fetch('/admin/api/stats');
        const data = await response.json();
        
        if (data.success) {
            // Total Users = Étudiants + Enseignants (sans admins)
            animateNumber('totalUsers', data.totalUsers || 0);
            animateNumber('totalCourses', data.totalCourses || 0);
            animateNumber('pendingCourses', data.pendingCourses || 0);
            animateNumber('validatedCourses', data.validatedCourses || 0);
            
            console.log(`📊 Stats chargées: ${data.totalUsers} utilisateurs (${data.studentsCount} étudiants, ${data.teachersCount} enseignants)`);
            console.log(`📚 Cours: ${data.totalCourses} (${data.pendingCourses} en attente, ${data.validatedCourses} validés)`);
            console.log(`📝 Quiz: ${data.totalQuizzes}`);
        } else {
            console.error('Erreur API:', data.error);
            fallbackAnimation();
        }
    } catch (error) {
        console.error('Erreur chargement stats:', error);
        fallbackAnimation();
    }
}

// Fallback si l'API ne répond pas
function fallbackAnimation() {
    animateNumber('totalUsers', 0);
    animateNumber('totalCourses', 0);
    animateNumber('pendingCourses', 0);
    animateNumber('validatedCourses', 0);
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    
    // Animation au survol des cartes
    const cards = document.querySelectorAll('.stat-card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'translateY(-3px)';
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0)';
        });
    });
    
    // Rafraîchir les stats toutes les 30 secondes
    setInterval(loadStats, 30000);
    
    console.log('Admin Dashboard initialisé');
});