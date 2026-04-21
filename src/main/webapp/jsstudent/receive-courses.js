/**
 * receive-courses.js - Gestion de la page des cours à recevoir
 * Version complète avec téléchargement et animations
 */

// ========== FONCTION DE TÉLÉCHARGEMENT ==========
async function downloadCourse(btn) {
    const courseId = btn.getAttribute('data-id');
    const courseTitle = btn.getAttribute('data-title');
    
    if (!confirm('Voulez-vous télécharger le cours "' + courseTitle + '" ?')) {
        return;
    }
    
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Téléchargement...';
    
    try {
        const response = await fetch('/course/download/' + courseId);
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Erreur lors du téléchargement');
        }
        
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = courseTitle + '.pdf';
        if (contentDisposition) {
            const match = contentDisposition.match(/filename\*=UTF-8''(.+)/);
            if (match && match[1]) {
                filename = decodeURIComponent(match[1]);
            }
        }
        
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        
        showNotification('✅ Cours "' + courseTitle + '" téléchargé avec succès !', 'success');
        
        setTimeout(() => {
            location.reload();
        }, 1500);
        
    } catch (error) {
        console.error('Erreur:', error);
        showNotification('❌ Erreur: ' + error.message, 'error');
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

// ========== FONCTION DE NOTIFICATION ==========
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;
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
        border-left: 4px solid ${type === 'success' ? '#10b981' : '#ef4444'};
        color: ${type === 'success' ? '#065f46' : '#991b1b'};
    `;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 4000);
}

// ========== INITIALISATION DES FILTRES ==========
function initFilters() {
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    const moduleSelect = document.getElementById('moduleSelect');
    const resetBtn = document.getElementById('resetFilters');
    
    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            applyFilters();
        });
    }
    
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                applyFilters();
            }
        });
    }
    
    if (resetBtn) {
        resetBtn.addEventListener('click', function() {
            if (searchInput) searchInput.value = '';
            if (moduleSelect) moduleSelect.value = 'all';
            applyFilters();
        });
    }
}

// ========== APPLICATION DES FILTRES ==========
function applyFilters() {
    const searchInput = document.getElementById('searchInput');
    const moduleSelect = document.getElementById('moduleSelect');
    
    const searchTerm = searchInput ? searchInput.value : '';
    const module = moduleSelect ? moduleSelect.value : 'all';
    
    let url = '/receive-courses?';
    const params = [];
    
    if (module && module !== 'all') {
        params.push('module=' + encodeURIComponent(module));
    }
    if (searchTerm) {
        params.push('search=' + encodeURIComponent(searchTerm));
    }
    
    if (params.length > 0) {
        window.location.href = url + params.join('&');
    } else {
        window.location.href = '/receive-courses';
    }
}

// ========== ANIMATIONS DES CARTES ==========
function initCardAnimations() {
    const cards = document.querySelectorAll('.course-card');
    
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
        
        setTimeout(() => {
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
        
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ DOM chargé - Page Receive Courses');
    initFilters();
    initCardAnimations();
    addAnimationStyles();
});

// ========== AJOUT DES STYLES D'ANIMATION ==========
function addAnimationStyles() {
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
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .course-card {
            animation: fadeInUp 0.4s ease forwards;
        }
    `;
    document.head.appendChild(style);
}