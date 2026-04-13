/**
 * niveux.js - JavaScript pour la page de sélection du niveau
 */

console.log("📚 Page de sélection du niveau chargée");

// ========== SIDEBAR TOGGLE ==========
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('active');
    }
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log("✅ DOM chargé - Page niveux");
    
    // Configuration du menu toggle
    const menuToggle = document.getElementById('menuToggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', toggleSidebar);
    }
    
    // Récupérer le nom de l'utilisateur depuis la session
    const userName = document.querySelector('.user-profile h3')?.textContent;
    const welcomeSpan = document.querySelector('.welcome-message h1 span');
    
    if (userName && userName !== 'Student Name') {
        const prenom = userName.split(' ')[0];
        if (welcomeSpan && welcomeSpan.textContent !== prenom) {
            welcomeSpan.textContent = prenom;
        }
    }
    
    // Animation supplémentaire pour les cartes
    const cards = document.querySelectorAll('.level-card');
    cards.forEach((card, index) => {
        card.style.animationDelay = `${0.1 + index * 0.2}s`;
    });
    
    // Fermer la sidebar si on clique en dehors sur mobile
    document.addEventListener('click', function(event) {
        const sidebar = document.querySelector('.sidebar');
        const menuToggle = document.getElementById('menuToggle');
        
        if (window.innerWidth <= 768 && sidebar && menuToggle) {
            if (!sidebar.contains(event.target) && !menuToggle.contains(event.target)) {
                sidebar.classList.remove('active');
            }
        }
    });
    
    // Gestion du redimensionnement de la fenêtre
    window.addEventListener('resize', function() {
        const sidebar = document.querySelector('.sidebar');
        if (window.innerWidth > 768 && sidebar) {
            sidebar.classList.remove('active');
        }
    });
});

// ========== GESTION DE LA NAVIGATION ==========

// Marquer l'élément actif dans la sidebar
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.includes(href)) {
            item.classList.add('active');
        }
    });
});

// ========== EFFETS AU SURVOL DES CARTES ==========
document.addEventListener('DOMContentLoaded', function() {
    const cards = document.querySelectorAll('.level-card');
    
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-15px) scale(1.02)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
        
        card.addEventListener('click', function(e) {
            // Effet ripple
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');
            this.appendChild(ripple);
            
            const x = e.clientX - e.target.getBoundingClientRect().left;
            const y = e.clientY - e.target.getBoundingClientRect().top;
            
            ripple.style.left = `${x}px`;
            ripple.style.top = `${y}px`;
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
});

// Ajout du style pour l'effet ripple
const rippleStyle = document.createElement('style');
rippleStyle.textContent = `
    .level-card {
        position: relative;
        overflow: hidden;
    }
    
    .ripple {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.7);
        transform: scale(0);
        animation: ripple-animation 0.6s linear;
        pointer-events: none;
        width: 100px;
        height: 100px;
        margin-left: -50px;
        margin-top: -50px;
    }
    
    @keyframes ripple-animation {
        from {
            transform: scale(0);
            opacity: 0.7;
        }
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(rippleStyle);