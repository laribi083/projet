/**
 * course-detail.js
 * JavaScript pour la page de détail d'un cours
 */

// ========== FONCTIONS DE TÉLÉCHARGEMENT ==========

function downloadFile(courseId, fileName) {
    if (!courseId || !fileName) {
        showNotification('Erreur: informations manquantes', 'error');
        return;
    }
    
    console.log(`📥 Téléchargement du fichier: ${fileName} (cours ID: ${courseId})`);
    
    const downloadUrl = `/course/${courseId}/download`;
    window.location.href = downloadUrl;
}

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
    
    setTimeout(() => notification.remove(), 3000);
}

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
        .notification-success { border-left: 4px solid #10b981; color: #065f46; background: #ecfdf5; }
        .notification-error { border-left: 4px solid #ef4444; color: #991b1b; background: #fef2f2; }
        .notification-info { border-left: 4px solid #3b82f6; color: #1e40af; background: #eff6ff; }
        @keyframes slideInRight {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
    `;
    document.head.appendChild(style);
}

// ========== INITIALISATION ==========

function initPage() {
    console.log('📚 Page de détail du cours chargée');
    addNotificationStyles();
    
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
                margin-top: 1rem; width: 100%; padding: 0.5rem;
                background: #10b981; color: white; border: none;
                border-radius: 8px; cursor: pointer; font-size: 0.85rem; font-weight: 500;
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

document.addEventListener('DOMContentLoaded', initPage);