/**
 * editcourse.js
 * JavaScript pour la page de modification de cours
 */

// ========== VARIABLES GLOBALES ==========
let newSelectedFiles = [];

// ========== FONCTIONS UTILITAIRES ==========

/**
 * Formate la taille d'un fichier
 * @param {number} bytes - Taille en bytes
 * @returns {string} Taille formatée
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * Retourne l'icône FontAwesome pour un type de fichier
 * @param {string} fileName - Nom du fichier
 * @returns {string} Classe CSS de l'icône
 */
function getFileIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    const icons = {
        'jpg': 'fa-file-image', 'jpeg': 'fa-file-image', 'png': 'fa-file-image', 'gif': 'fa-file-image', 'svg': 'fa-file-image',
        'pdf': 'fa-file-pdf',
        'doc': 'fa-file-word', 'docx': 'fa-file-word',
        'xls': 'fa-file-excel', 'xlsx': 'fa-file-excel',
        'ppt': 'fa-file-powerpoint', 'pptx': 'fa-file-powerpoint',
        'mp4': 'fa-file-video', 'avi': 'fa-file-video', 'mov': 'fa-file-video',
        'mp3': 'fa-file-audio', 'wav': 'fa-file-audio',
        'zip': 'fa-file-archive', 'rar': 'fa-file-archive',
        'txt': 'fa-file-alt', 'md': 'fa-file-alt', 'html': 'fa-file-code', 'css': 'fa-file-code', 'js': 'fa-file-code'
    };
    return icons[extension] || 'fa-file';
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

/**
 * Affiche une notification
 * @param {string} message - Message à afficher
 * @param {string} type - Type de notification (success, error, info)
 */
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// ========== GESTION DES FICHIERS ==========

/**
 * Configure l'upload de nouveaux fichiers
 */
function setupNewFileUpload() {
    const fileInput = document.getElementById('newFiles');
    
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const files = Array.from(e.target.files);
            newSelectedFiles = [...newSelectedFiles, ...files];
            updateNewFilesPreview();
        });
    }
}

/**
 * Met à jour l'aperçu des nouveaux fichiers
 */
function updateNewFilesPreview() {
    const previewContainer = document.getElementById('newFilesPreview');
    if (!previewContainer) return;
    
    if (newSelectedFiles.length === 0) {
        previewContainer.innerHTML = '';
        return;
    }
    
    previewContainer.innerHTML = `
        <div class="current-files">
            <h4><i class="fas fa-plus-circle"></i> Nouveaux fichiers à ajouter</h4>
            <ul class="file-list">
                ${newSelectedFiles.map((file, index) => `
                    <li class="new-file-item">
                        <div>
                            <i class="fas ${getFileIcon(file.name)}"></i>
                            <span>${escapeHtml(file.name)}</span>
                            <small class="file-size">(${formatFileSize(file.size)})</small>
                        </div>
                        <button type="button" class="remove-new-file" onclick="removeNewFile(${index})">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </li>
                `).join('')}
            </ul>
        </div>
    `;
}

/**
 * Supprime un nouveau fichier de la liste
 * @param {number} index - Index du fichier à supprimer
 */
function removeNewFile(index) {
    newSelectedFiles.splice(index, 1);
    updateNewFilesPreview();
    
    // Mettre à jour l'input file
    const fileInput = document.getElementById('newFiles');
    if (fileInput) {
        const dataTransfer = new DataTransfer();
        newSelectedFiles.forEach(file => dataTransfer.items.add(file));
        fileInput.files = dataTransfer.files;
    }
}

// ========== VALIDATION DU FORMULAIRE ==========

/**
 * Valide le formulaire avant soumission
 * @returns {boolean} true si valide
 */
function validateForm() {
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
    const module = document.getElementById('module').value.trim();
    const niveau = document.getElementById('niveau').value;
    
    if (!title) {
        showNotification('Le titre du cours est obligatoire', 'error');
        return false;
    }
    
    if (!description) {
        showNotification('La description du cours est obligatoire', 'error');
        return false;
    }
    
    if (!module) {
        showNotification('Le module est obligatoire', 'error');
        return false;
    }
    
    if (!niveau) {
        showNotification('Le niveau est obligatoire', 'error');
        return false;
    }
    
    return true;
}

// ========== SOUMISSION DU FORMULAIRE ==========

/**
 * Prépare et soumet le formulaire
 * @param {Event} event - Événement de soumission
 */
async function handleFormSubmit(event) {
    event.preventDefault();
    
    if (!validateForm()) {
        return;
    }
    
    const saveBtn = document.getElementById('saveBtn');
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enregistrement...';
    
    const form = document.getElementById('editCourseForm');
    const formData = new FormData(form);
    
    // Ajouter les nouveaux fichiers
    newSelectedFiles.forEach(file => {
        formData.append('newFiles', file);
    });
    
    try {
        const actionUrl = form.getAttribute('action');
        const response = await fetch(actionUrl, {
            method: 'POST',
            body: formData
        });
        
        if (response.redirected || response.ok) {
            showNotification('Cours modifié avec succès !', 'success');
            
            // Redirection après un court délai
            setTimeout(() => {
                window.location.href = '/teacher/dashboard';
            }, 1500);
        } else {
            throw new Error('Erreur lors de la modification');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors de la modification du cours', 'error');
        saveBtn.disabled = false;
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Enregistrer';
    }
}

// ========== INITIALISATION ==========

/**
 * Initialise la page
 */
function initPage() {
    console.log('📚 Page de modification de cours initialisée');
    
    // Configuration de l'upload de fichiers
    setupNewFileUpload();
    
    // Configuration de la soumission du formulaire
    const form = document.getElementById('editCourseForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
}

// Initialisation au chargement du DOM
document.addEventListener('DOMContentLoaded', initPage);