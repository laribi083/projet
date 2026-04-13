/**
 * quizzes.js - JavaScript pour la page des quiz étudiant
 * Gère l'affichage dynamique, les animations et les interactions
 */

// ========== ATTENDRE LE CHARGEMENT DU DOM ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('📚 Page des quiz initialisée');
    
    // Cacher le spinner après chargement
    hideLoadingSpinner();
    
    // Initialiser les animations des cartes
    initCardAnimations();
    
    // Initialiser la navigation active
    initActiveNavigation();
});

// ========== GESTION DU LOADING ==========
function hideLoadingSpinner() {
    const spinner = document.getElementById('loadingSpinner');
    const quizzesGrid = document.getElementById('quizzesGrid');
    const emptyState = document.getElementById('emptyState');
    
    if (!spinner) return;
    
    // Vérifier si des quiz sont présents
    const quizCards = document.querySelectorAll('.quiz-card');
    
    setTimeout(() => {
        spinner.style.display = 'none';
        
        if (quizCards.length > 0 && quizzesGrid) {
            quizzesGrid.style.display = 'grid';
            // Animation d'apparition des cartes
            animateCardsAppearance();
        } else if (emptyState) {
            emptyState.style.display = 'block';
        }
    }, 500);
}

// ========== ANIMATION D'APPARITION DES CARTES ==========
function animateCardsAppearance() {
    const cards = document.querySelectorAll('.quiz-card');
    
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

// ========== INITIALISER LES ANIMATIONS DES CARTES ==========
function initCardAnimations() {
    const cards = document.querySelectorAll('.quiz-card');
    
    cards.forEach(card => {
        // Animation au survol
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.transition = 'transform 0.3s ease';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
        
        // Animation du bouton Start
        const startBtn = card.querySelector('.btn-start');
        if (startBtn) {
            startBtn.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
            });
            
            startBtn.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        }
    });
}

// ========== INITIALISER LA NAVIGATION ACTIVE ==========
function initActiveNavigation() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
    
    navItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.includes(href)) {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        }
    });
}

// ========== MESSAGES DE NOTIFICATION ==========
function showNotification(message, type = 'info') {
    // Supprimer les notifications existantes
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());
    
    // Créer la notification
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Styles de la notification
    notification.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: white;
        border-radius: 12px;
        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        display: flex;
        align-items: center;
        gap: 0.75rem;
        z-index: 1000;
        animation: slideInRight 0.3s ease;
        border-left: 4px solid ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#667eea'};
        color: ${type === 'success' ? '#065f46' : type === 'error' ? '#991b1b' : '#1f2937'};
    `;
    
    document.body.appendChild(notification);
    
    // Supprimer après 3 secondes
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// ========== STYLES POUR LES ANIMATIONS ==========
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
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