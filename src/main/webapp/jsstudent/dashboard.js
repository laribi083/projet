// Configuration de l'API
const API_BASE_URL = 'http://localhost:8082/api';
const ETUDIANT_ID = 1; // ID de Sarah (créé dans DataLoader)

// Chargement des données au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
});

// Fonction principale pour charger toutes les données
async function loadDashboardData() {
    try {
        await Promise.all([
            loadStatistiques(),
            loadActionsRapides(),
            loadCoursRecents()
        ]);
    } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
        showErrorMessage('Impossible de charger les données. Veuillez rafraîchir la page.');
    }
}

// Chargement des statistiques
async function loadStatistiques() {
    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/${ETUDIANT_ID}/statistiques`);
        if (!response.ok) throw new Error('Erreur réseau');
        
        const stats = await response.json();
        
        document.getElementById('coursActifs').textContent = stats.coursActifs || 6;
        document.getElementById('quizCompletes').textContent = stats.quizCompletes || 24;
        document.getElementById('moyenne').textContent = (stats.moyenne || 15.8).toFixed(1) + '/20';
        document.getElementById('heuresEtude').textContent = (stats.heuresEtude || 142) + 'h';
    } catch (error) {
        console.error('Erreur chargement statistiques:', error);
        // Utiliser les valeurs par défaut en cas d'erreur
        setDefaultStats();
    }
}

// Valeurs par défaut pour les statistiques
function setDefaultStats() {
    document.getElementById('coursActifs').textContent = '6';
    document.getElementById('quizCompletes').textContent = '24';
    document.getElementById('moyenne').textContent = '15.8/20';
    document.getElementById('heuresEtude').textContent = '142h';
}

// Chargement des actions rapides
async function loadActionsRapides() {
    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/${ETUDIANT_ID}/actions-rapides`);
        if (!response.ok) throw new Error('Erreur réseau');
        
        const actions = await response.json();
        
        document.getElementById('coursCompletes').textContent = actions.coursCompletes || 0;
        document.getElementById('quizDisponibles').textContent = actions.quizDisponibles || 0;
        document.getElementById('evaluationsDisponibles').textContent = actions.evaluationsDisponibles || 3;
        document.getElementById('questionsDisponibles').textContent = actions.questionsDisponibles || 5;
    } catch (error) {
        console.error('Erreur chargement actions rapides:', error);
        // Utiliser les valeurs par défaut
        setDefaultActions();
    }
}

// Valeurs par défaut pour les actions rapides
function setDefaultActions() {
    document.getElementById('coursCompletes').textContent = '2';
    document.getElementById('quizDisponibles').textContent = '5';
    document.getElementById('evaluationsDisponibles').textContent = '3';
    document.getElementById('questionsDisponibles').textContent = '5';
}

// Chargement des cours récents
async function loadCoursRecents() {
    try {
        const response = await fetch(`${API_BASE_URL}/dashboard/${ETUDIANT_ID}/cours`);
        if (!response.ok) throw new Error('Erreur réseau');
        
        const cours = await response.json();
        displayCourses(cours);
    } catch (error) {
        console.error('Erreur chargement cours:', error);
        // Utiliser des données par défaut
        displayDefaultCourses();
    }
}

// Affichage des cours
function displayCourses(cours) {
    const container = document.getElementById('coursesContainer');
    if (!container) return;
    
    if (!cours || cours.length === 0) {
        displayDefaultCourses();
        return;
    }
    
    container.innerHTML = cours.slice(0, 4).map(course => `
        <div class="course-card">
            <div class="course-info">
                <h4>${course.titre || 'Cours sans titre'}</h4>
                <p>${course.professeur || 'Professeur'}</p>
                <span class="progress">${course.completionPercentage || 0}% complété</span>
            </div>
            <span class="course-category">${course.categorie || 'Général'}</span>
        </div>
    `).join('');
}

// Données par défaut pour les cours
function displayDefaultCourses() {
    const container = document.getElementById('coursesContainer');
    if (!container) return;
    
    const defaultCourses = [
        { titre: 'Mathématiques Avancées', professeur: 'Dr. Dupont', completion: 75, categorie: 'Mathématiques' },
        { titre: 'Programmation Java', professeur: 'Dr. Martin', completion: 100, categorie: 'Informatique' },
        { titre: 'Bases de Données', professeur: 'Dr. Bernard', completion: 45, categorie: 'Informatique' },
        { titre: 'Anglais Technique', professeur: 'Ms. Johnson', completion: 60, categorie: 'Langues' }
    ];
    
    container.innerHTML = defaultCourses.map(course => `
        <div class="course-card">
            <div class="course-info">
                <h4>${course.titre}</h4>
                <p>${course.professeur}</p>
                <span class="progress">${course.completion}% complété</span>
            </div>
            <span class="course-category">${course.categorie}</span>
        </div>
    `).join('');
}

// Affichage des messages d'erreur
function showErrorMessage(message) {
    // Vous pouvez implémenter une notification toast ici
    console.error(message);
    
    // Créer une notification simple
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background-color: var(--danger-color);
        color: white;
        padding: 1rem;
        border-radius: 8px;
        box-shadow: var(--shadow-lg);
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

// Animation pour les notifications
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
`;
document.head.appendChild(style);

// Gestionnaire pour le bouton de déconnexion
document.querySelector('.logout-btn')?.addEventListener('click', function(e) {
    e.preventDefault();
    window.location.href = 'index.html';
});

// Gestionnaire pour la recherche
document.querySelector('.search-box input')?.addEventListener('input', function(e) {
    const searchTerm = e.target.value.toLowerCase();
    filterCourses(searchTerm);
});

// Filtrage des cours
function filterCourses(term) {
    const courseCards = document.querySelectorAll('.course-card');
    
    courseCards.forEach(card => {
        const title = card.querySelector('h4')?.textContent.toLowerCase() || '';
        const category = card.querySelector('.course-category')?.textContent.toLowerCase() || '';
        
        if (title.includes(term) || category.includes(term)) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}

// Rafraîchissement périodique des données (toutes les 5 minutes)
setInterval(loadDashboardData, 300000);

// Gestionnaire pour le menu mobile (si nécessaire)
function toggleMobileMenu() {
    document.querySelector('.sidebar')?.classList.toggle('active');
}

// Ajouter un bouton de menu mobile
const mobileMenuBtn = document.createElement('button');
mobileMenuBtn.className = 'mobile-menu-btn';
mobileMenuBtn.innerHTML = '<i class="fas fa-bars"></i>';
mobileMenuBtn.style.cssText = `
    position: fixed;
    bottom: 20px;
    right: 20px;
    background-color: var(--primary-color);
    color: white;
    border: none;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: none;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    cursor: pointer;
    box-shadow: var(--shadow-lg);
    z-index: 1001;
`;

mobileMenuBtn.addEventListener('click', toggleMobileMenu);
document.body.appendChild(mobileMenuBtn);

// Afficher le bouton seulement sur mobile
const mediaQuery = window.matchMedia('(max-width: 768px)');
mediaQuery.addListener(e => {
    mobileMenuBtn.style.display = e.matches ? 'flex' : 'none';
});
mobileMenuBtn.style.display = mediaQuery.matches ? 'flex' : 'none';