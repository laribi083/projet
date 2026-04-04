/**
 * course-detail.js
 * JavaScript pour la page de détail d'un cours
 */

// ========== FONCTIONS DE TÉLÉCHARGEMENT ==========

/**
 * Télécharge un fichier du cours
 * @param {number} courseId - L'ID du cours
 * @param {string} fileName - Le nom du fichier à télécharger
 */
function downloadFile(courseId, fileName) {
    if (!courseId || !fileName) {
        showNotification('Erreur: informations manquantes', 'error');
        return;
    }
    
    console.log(`📥 Téléchargement du fichier: ${fileName} (cours ID: ${courseId})`);
    
    const downloadUrl = `/student/download/${courseId}/${encodeURIComponent(fileName)}`;
    window.location.href = downloadUrl;
}

/**
 * Télécharge tous les fichiers du cours
 * @param {number} courseId - L'ID du cours
 * @param {Array} fileNames - Liste des noms de fichiers
 */
function downloadAllFiles(courseId, fileNames) {
    if (!fileNames || fileNames.length === 0) {
        showNotification('Aucun fichier à télécharger', 'info');
        return;
    }
    
    showNotification(`Téléchargement de ${fileNames.length} fichier(s)...`, 'info');
    
    fileNames.forEach((fileName, index) => {
        setTimeout(() => {
            downloadFile(courseId, fileName);
        }, index * 500);
    });
}

// ========== FONCTIONS DE NOTIFICATION ==========

/**
 * Affiche une notification
 * @param {string} message - Le message à afficher
 * @param {string} type - Le type de notification (success, error, info)
 */
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';
    
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

/**
 * Ajoute les styles pour les notifications s'ils n'existent pas
 */
function addNotificationStyles() {
    if (document.querySelector('#notification-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'notification-styles';
    style.textContent = `
        .notification {
            position: fixed;
            bottom: 20px;
            right: 20px;
            padding: 1rem 1.5rem;
            border-radius: 12px;
            background: white;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            gap: 0.75rem;
            z-index: 1100;
            animation: slideInRight 0.3s ease;
            font-weight: 500;
        }
        .notification-success {
            border-left: 4px solid #10b981;
            color: #065f46;
            background: #ecfdf5;
        }
        .notification-error {
            border-left: 4px solid #ef4444;
            color: #991b1b;
            background: #fef2f2;
        }
        .notification-info {
            border-left: 4px solid #3b82f6;
            color: #1e40af;
            background: #eff6ff;
        }
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
    `;
    document.head.appendChild(style);
}

// ========== FONCTIONS DE PARTAGE ==========

/**
 * Copie le lien du cours dans le presse-papier
 * @param {number} courseId - L'ID du cours
 */
function copyCourseLink(courseId) {
    const url = `${window.location.origin}/student/course/${courseId}`;
    
    navigator.clipboard.writeText(url).then(() => {
        showNotification('Lien copié dans le presse-papier !', 'success');
    }).catch(() => {
        showNotification('Erreur lors de la copie', 'error');
    });
}

/**
 * Partage le cours (si l'API de partage est disponible)
 * @param {string} title - Le titre du cours
 * @param {string} url - L'URL du cours
 */
function shareCourse(title, url) {
    if (navigator.share) {
        navigator.share({
            title: title,
            text: 'Découvrez ce cours sur BrainLearning !',
            url: url
        }).catch(() => {
            copyCourseLink(url.split('/').pop());
        });
    } else {
        copyCourseLink(url.split('/').pop());
    }
}

// ========== INITIALISATION ==========

/**
 * Initialise la page
 */
function initPage() {
    console.log('📚 Page de détail du cours chargée');
    addNotificationStyles();
    
    // Ajouter les écouteurs d'événements pour les boutons de téléchargement
    const downloadButtons = document.querySelectorAll('.btn-download');
    downloadButtons.forEach(button => {
        const newButton = button.cloneNode(true);
        button.parentNode.replaceChild(newButton, button);
        
        newButton.addEventListener('click', function(event) {
            event.stopPropagation();
            const courseId = this.getAttribute('data-course-id');
            const fileName = this.getAttribute('data-file-name');
            if (courseId && fileName) {
                downloadFile(parseInt(courseId), fileName);
            }
        });
    });
    
    // Ajouter un bouton pour télécharger tous les fichiers (optionnel)
    const resourcesSection = document.querySelector('.course-section:has(.files-list)');
    if (resourcesSection) {
        const fileItems = document.querySelectorAll('.files-list li');
        const fileNames = Array.from(fileItems).map(item => {
            return item.querySelector('span')?.textContent;
        }).filter(name => name);
        
        if (fileNames.length > 1) {
            const downloadAllBtn = document.createElement('button');
            downloadAllBtn.className = 'btn-download-all';
            downloadAllBtn.innerHTML = '<i class="fas fa-download"></i> Télécharger tous les fichiers';
            downloadAllBtn.style.cssText = `
                margin-top: 1rem;
                width: 100%;
                padding: 0.5rem;
                background: #10b981;
                color: white;
                border: none;
                border-radius: 8px;
                cursor: pointer;
                font-size: 0.85rem;
                font-weight: 500;
            `;
            
            const courseId = document.querySelector('.btn-download')?.getAttribute('data-course-id');
            if (courseId) {
                downloadAllBtn.addEventListener('click', () => {
                    downloadAllFiles(parseInt(courseId), fileNames);
                });
                resourcesSection.appendChild(downloadAllBtn);
            }
        }
    }
}

// Initialisation au chargement du DOM
document.addEventListener('DOMContentLoaded', initPage);