/**
 * niveux.js - JavaScript pour la page de sélection du niveau
 */

console.log("📚 Page de sélection du niveau chargée");

// ========== SIDEBAR TOGGLE ==========
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('mobile-open');
    }
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log("✅ DOM chargé - Page niveux");
    
    // Configuration du menu toggle mobile
    const mobileToggle = document.getElementById('mobileMenuToggle');
    const sidebar = document.getElementById('sidebar');
    
    if (mobileToggle) {
        mobileToggle.addEventListener('click', toggleSidebar);
    }
    
    // Fermer la sidebar en cliquant à l'extérieur (mobile)
    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 768 && sidebar && mobileToggle) {
            if (!sidebar.contains(event.target) && !mobileToggle.contains(event.target)) {
                sidebar.classList.remove('mobile-open');
            }
        }
    });
    
    // Réinitialiser la sidebar au redimensionnement
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768 && sidebar) {
            sidebar.classList.remove('mobile-open');
        }
    });
    
    // Tracking des sélections de niveau
    const levelCards = document.querySelectorAll('.level-card');
    levelCards.forEach((card, index) => {
        card.addEventListener('click', function(e) {
            const levelName = this.querySelector('h2')?.innerText || 'Unknown';
            console.log(`🎯 Level selected: ${levelName}`);
        });
    });
});

// Marquer l'élément actif dans la sidebar
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href && (currentPath === href || currentPath.includes(href))) {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        }
    });
    
    // S'assurer que "My Courses" est actif sur cette page
    if (currentPath.includes('/student/niveux')) {
        const myCoursesLink = document.querySelector('a[href*="/student/niveux"]');
        if (myCoursesLink) {
            navItems.forEach(nav => nav.classList.remove('active'));
            myCoursesLink.classList.add('active');
        }
    }
});

// Effet ripple au clic sur les cartes
document.addEventListener('DOMContentLoaded', function() {
    const cards = document.querySelectorAll('.level-card');
    
    cards.forEach(card => {
        card.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');
            this.appendChild(ripple);
            
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            
            ripple.style.left = `${x}px`;
            ripple.style.top = `${y}px`;
            
            setTimeout(() => ripple.remove(), 600);
        });
    });
});

// Style pour l'effet ripple
const rippleStyle = document.createElement('style');
rippleStyle.textContent = `
    .level-card {
        position: relative;
        overflow: hidden;
    }
    .ripple {
        position: absolute;
        border-radius: 50%;
        background: rgba(59, 130, 246, 0.4);
        transform: scale(0);
        animation: ripple-animation 0.6s linear;
        pointer-events: none;
        width: 100px;
        height: 100px;
        margin-left: -50px;
        margin-top: -50px;
    }
    @keyframes ripple-animation {
        from { transform: scale(0); opacity: 0.7; }
        to { transform: scale(4); opacity: 0; }
    }
`;
document.head.appendChild(rippleStyle);