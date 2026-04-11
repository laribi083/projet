
/**
 * Retourne au dashboard teacher
 */
function goBack() {
    window.location.href = '/teacher/dashboard';
}

// ========== GESTION DES FICHIERS PDF ==========

/**
 * Prépare le chemin du fichier pour l'affichage
 * @param {string} filePath - Chemin complet du fichier
 * @returns {string} Chemin encodé
 */
function encodeFilePath(filePath) {
    // Remplacer les antislashs par des slashes pour l'URL
    let normalizedPath = filePath.replace(/\\/g, '/');
    return encodeURIComponent(normalizedPath);
}

/**
 * Prépare le chemin du fichier pour l'affichage dans l'iframe
 * @param {string} filePath - Chemin du fichier
 * @returns {string} URL pour l'iframe
 */
function getPdfPreviewUrl(filePath) {
    const encodedPath = encodeFilePath(filePath);
    return `/teacher/preview-pdf?path=${encodedPath}`;
}

/**
 * Ouvre le modal de prévisualisation PDF
 * @param {string} filePath - Chemin du fichier PDF
 */
function previewPDF(filePath) {
    console.log('📄 Previewing PDF:', filePath);
    
    const pdfModal = document.getElementById('pdfModal');
    const pdfFrame = document.getElementById('pdfFrame');
    
    if (!pdfModal || !pdfFrame) {
        console.error('Modal elements not found');
        return;
    }
    
    // Construire l'URL de prévisualisation
    const previewUrl = getPdfPreviewUrl(filePath);
    pdfFrame.src = previewUrl;
    
    // Afficher le modal
    pdfModal.style.display = 'block';
    document.body.style.overflow = 'hidden';
    
    // Ajouter un écouteur pour les erreurs de chargement
    pdfFrame.onerror = function() {
        console.error('Failed to load PDF');
        showNotification('Erreur lors du chargement du PDF', 'error');
        closePdfModal();
    };
}

/**
 * Ferme le modal de prévisualisation PDF
 */
function closePdfModal() {
    const pdfModal = document.getElementById('pdfModal');
    const pdfFrame = document.getElementById('pdfFrame');
    
    if (pdfModal) {
        pdfModal.style.display = 'none';
    }
    
    if (pdfFrame) {
        pdfFrame.src = '';
    }
    
    document.body.style.overflow = 'auto';
}

/**
 * Télécharge un fichier
 * @param {string} filePath - Chemin du fichier
 * @param {string} fileName - Nom du fichier à télécharger
 */
function downloadFile(filePath, fileName) {
    console.log('📥 Downloading file:', fileName);
    
    const encodedPath = encodeFilePath(filePath);
    const encodedName = encodeURIComponent(fileName);
    
    // Créer un lien temporaire pour le téléchargement
    const downloadUrl = `/teacher/download-file?path=${encodedPath}&name=${encodedName}`;
    
    // Ouvrir dans un nouvel onglet ou télécharger directement
    window.location.href = downloadUrl;
    
    // Notification
    showNotification(`Téléchargement de "${fileName}" démarré...`, 'success');
}

// ========== GESTION DES QUIZ ==========

/**
 * Affiche la page d'un quiz
 * @param {number} quizId - ID du quiz
 */
function viewQuiz(quizId) {
    console.log('📝 Viewing quiz:', quizId);
    window.location.href = `/teacher/quiz/${quizId}/view`;
}

/**
 * Crée un quiz pour un cours
 * @param {number} courseId - ID du cours
 */
function createQuizForCourse(courseId) {
    console.log('📝 Creating quiz for course:', courseId);
    
    // Vous pouvez soit rediriger vers la page de création, soit ouvrir le modal
    // Option 1: Redirection
    window.location.href = `/teacher/dashboard?createQuiz=true&courseId=${courseId}`;
    
    // Option 2: Si vous avez un modal global, vous pouvez l'ouvrir
    // if (typeof openCreateQuizModal === 'function') {
    //     openCreateQuizModal(courseId, '', '');
    // }
}

// ========== NOTIFICATIONS ==========

/**
 * Affiche une notification temporaire
 * @param {string} message - Message à afficher
 * @param {string} type - Type de notification (success, error, info)
 */
function showNotification(message, type) {
    // Supprimer les notifications existantes
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    // Créer la notification
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    let icon = '';
    switch (type) {
        case 'success':
            icon = 'fa-check-circle';
            break;
        case 'error':
            icon = 'fa-exclamation-circle';
            break;
        default:
            icon = 'fa-info-circle';
    }
    
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    // Auto-suppression après 3 secondes
    setTimeout(() => {
        if (notification.remove) {
            notification.remove();
        }
    }, 3000);
}

// ========== GESTION DES ÉVÉNEMENTS ==========

/**
 * Initialise les écouteurs d'événements
 */
function initEventListeners() {
    // Fermer le modal PDF avec la touche Echap
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closePdfModal();
        }
    });
    
    // Fermer le modal en cliquant sur l'arrière-plan
    const pdfModal = document.getElementById('pdfModal');
    if (pdfModal) {
        pdfModal.addEventListener('click', function(e) {
            if (e.target === pdfModal) {
                closePdfModal();
            }
        });
    }
}

// ========== CHARGEMENT DE LA PAGE ==========

/**
 * Initialisation au chargement de la page
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('Course view page loaded');
    initEventListeners();
    
    // Afficher une notification de bienvenue (optionnelle)
    // showNotification('Bienvenue dans la page du cours', 'info');
});

// ========== UTILITAIRES ==========

/**
 * Formate une date en format local
 * @param {string} dateString - Date au format ISO
 * @returns {string} Date formatée
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

/**
 * Tronque un texte si trop long
 * @param {string} text - Texte à tronquer
 * @param {number} maxLength - Longueur maximale
 * @returns {string} Texte tronqué
 */
function truncateText(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

/**
 * Échappe les caractères HTML
 * @param {string} text - Texte à échapper
 * @returns {string} Texte échappé
 */
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}