// dashbord.js - Teacher Dashboard Logic

// Global variables
let courseToDelete = null;
let selectedFiles = [];

// ==================== MODAL MANAGEMENT ====================
function openAddCourseModal() {
    console.log('Opening modal...');
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        resetCourseForm();
        console.log('Modal opened');
    } else {
        console.error('Modal not found!');
    }
}

function closeAddCourseModal() {
    console.log('Closing modal...');
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = 'auto';
        resetCourseForm();
    }
}

function resetCourseForm() {
    const form = document.getElementById('courseForm');
    if (form) {
        form.reset();
    }
    selectedFiles = [];
    updateFileList();
    const fileInput = document.getElementById('filesInput');
    if (fileInput) fileInput.value = '';
}

// File upload handling - Sans limite de taille
function setupFileUpload() {
    const fileInput = document.getElementById('filesInput');
    const dropArea = document.getElementById('fileUploadArea');
    
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const files = Array.from(e.target.files);
            selectedFiles = [...selectedFiles, ...files];
            updateFileList();
        });
    }
    
    if (dropArea) {
        dropArea.addEventListener('click', () => {
            if (fileInput) fileInput.click();
        });
        
        dropArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#667eea';
            dropArea.style.background = '#f3f4f6';
            dropArea.classList.add('dragover');
        });
        
        dropArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
            dropArea.classList.remove('dragover');
        });
        
        dropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
            dropArea.classList.remove('dragover');
            const files = Array.from(e.dataTransfer.files);
            selectedFiles = [...selectedFiles, ...files];
            updateFileList();
            if (fileInput) {
                const dataTransfer = new DataTransfer();
                selectedFiles.forEach(file => dataTransfer.items.add(file));
                fileInput.files = dataTransfer.files;
            }
        });
    }
}

// Formater la taille du fichier (en Bytes, KB, MB, GB)
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Obtenir l'icône selon le type de fichier
function getFileIcon(fileName, fileType) {
    const extension = fileName.split('.').pop().toLowerCase();
    
    // Images
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg', 'webp'].includes(extension)) {
        return 'fa-file-image';
    }
    // PDF
    if (extension === 'pdf') {
        return 'fa-file-pdf';
    }
    // Word
    if (['doc', 'docx'].includes(extension)) {
        return 'fa-file-word';
    }
    // Excel
    if (['xls', 'xlsx', 'csv'].includes(extension)) {
        return 'fa-file-excel';
    }
    // PowerPoint
    if (['ppt', 'pptx'].includes(extension)) {
        return 'fa-file-powerpoint';
    }
    // Vidéos
    if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'webm'].includes(extension)) {
        return 'fa-file-video';
    }
    // Audio
    if (['mp3', 'wav', 'ogg', 'flac', 'aac', 'm4a'].includes(extension)) {
        return 'fa-file-audio';
    }
    // Archives
    if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) {
        return 'fa-file-archive';
    }
    // Code
    if (['html', 'css', 'js', 'java', 'py', 'cpp', 'c', 'php', 'xml', 'json'].includes(extension)) {
        return 'fa-file-code';
    }
    // Texte
    if (['txt', 'md', 'rtf'].includes(extension)) {
        return 'fa-file-alt';
    }
    // Par défaut
    return 'fa-file';
}

function updateFileList() {
    const fileListDiv = document.getElementById('fileList');
    if (!fileListDiv) return;
    
    if (selectedFiles.length === 0) {
        fileListDiv.innerHTML = '';
        return;
    }
    
    // Calculer la taille totale
    const totalSize = selectedFiles.reduce((sum, file) => sum + file.size, 0);
    const totalSizeFormatted = formatFileSize(totalSize);
    
    // Afficher l'en-tête avec le nombre de fichiers et la taille totale
    const header = `
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; padding: 5px; background: #f9fafb; border-radius: 8px;">
            <span style="font-size: 0.8rem; font-weight: 600; color: #4b5563;">
                <i class="fas fa-files"></i> ${selectedFiles.length} fichier(s)
            </span>
            <span style="font-size: 0.75rem; color: #6b7280;">
                Taille totale: ${totalSizeFormatted}
            </span>
        </div>
    `;
    
    const filesList = selectedFiles.map((file, index) => {
        const fileIcon = getFileIcon(file.name, file.type);
        const fileSize = formatFileSize(file.size);
        
        return `
            <div class="file-item">
                <div>
                    <i class="fas ${fileIcon}"></i>
                    <span title="${escapeHtml(file.name)}">${escapeHtml(file.name.length > 40 ? file.name.substring(0, 37) + '...' : file.name)}</span>
                    <small style="color:#6b7280; margin-left:8px;">(${fileSize})</small>
                </div>
                <button type="button" class="remove-file" onclick="removeFile(${index})" title="Supprimer">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </div>
        `;
    }).join('');
    
    fileListDiv.innerHTML = header + filesList;
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
    const fileInput = document.getElementById('filesInput');
    if (fileInput) {
        const dataTransfer = new DataTransfer();
        selectedFiles.forEach(file => dataTransfer.items.add(file));
        fileInput.files = dataTransfer.files;
    }
}

// Submit course form
async function submitCourseForm(event) {
    event.preventDefault();
    
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Création...';
    
    const title = document.getElementById('courseTitle').value.trim();
    const description = document.getElementById('courseDescription').value.trim();
    const niveau = document.getElementById('courseLevel').value;
    
    if (!title || !description || !niveau) {
        showNotification('Veuillez remplir tous les champs obligatoires', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
        return;
    }
    
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('niveau', niveau);
    
    // Ajouter tous les fichiers sans vérification de taille
    selectedFiles.forEach(file => {
        formData.append('files', file);
    });
    
    // Afficher une notification de progression
    if (selectedFiles.length > 0) {
        showNotification(`Téléchargement de ${selectedFiles.length} fichier(s) (${formatFileSize(selectedFiles.reduce((sum, f) => sum + f.size, 0))}) en cours...`, 'info');
    }
    
    try {
        // ⭐ URL POUR LA CRÉATION - POST vers /teacher/api/courses
        const response = await fetch('/teacher/api/courses', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                showNotification('Cours créé avec succès!', 'success');
                closeAddCourseModal();
                await loadCourses();
                await loadStats();
            } else {
                throw new Error(data.message || 'Erreur lors de la création');
            }
        } else {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erreur lors de la création');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification(`Erreur: ${error.message}`, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
    }
}

// ==================== COURSE MANAGEMENT ====================
async function loadCourses() {
    try {
        // ⭐ URL POUR RÉCUPÉRER LES COURS - GET vers /teacher/teacher-courses
        const response = await fetch('/teacher/teacher-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesGrid = document.getElementById('coursesGrid');
        
        if (!coursesGrid) return;
        
        if (courses.length === 0) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book-open"></i>
                    <p>You have not yet created a course</p>
                    <button class="btn-primary" onclick="openAddCourseModal()">
                        Create your first course
                    </button>
                </div>
            `;
            return;
        }
        
        coursesGrid.innerHTML = '';
        courses.forEach(course => {
            const courseCard = createCourseCard(course);
            coursesGrid.appendChild(courseCard);
        });
    } catch (error) {
        console.error('Error loading courses:', error);
        showNotification('Erreur lors du chargement des cours', 'error');
        
        // Afficher un message d'erreur dans la grille
        const coursesGrid = document.getElementById('coursesGrid');
        if (coursesGrid) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Erreur de chargement des cours</p>
                    <button class="btn-primary" onclick="loadCourses()">
                        Réessayer
                    </button>
                </div>
            `;
        }
    }
}

function createCourseCard(course) {
    const div = document.createElement('div');
    div.className = 'course-card';
    div.innerHTML = `
        <div class="course-image">
            📘
            <span class="course-badge">${course.status === 'ACTIVE' ? 'Actif' : 'Brouillon'}</span>
        </div>
        <div class="course-content">
            <h3 class="course-title">${escapeHtml(course.title)}</h3>
            <p class="course-description">${escapeHtml(course.description ? course.description.substring(0, 100) : '')}${course.description && course.description.length > 100 ? '...' : ''}</p>
            <div class="course-meta">
                <div class="course-stats">
                    <span>📖 ${course.totalQuizzes || 0} quiz</span>
                    <span>👥 ${course.totalStudents || 0} étudiants</span>
                </div>
                <div class="course-actions">
                    <button class="btn-view" onclick="viewCourse(${course.id})">📘 Voir</button>
                    <button class="btn-edit" onclick="editCourse(${course.id})">✏️ Modifier</button>
                    <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')">🗑 Supprimer</button>
                </div>
            </div>
        </div>
    `;
    return div;
}

async function loadStats() {
    try {
        // ⭐ URL POUR RÉCUPÉRER LES STATISTIQUES - GET vers /teacher/teacher-courses
        const response = await fetch('/teacher/teacher-courses');
        if (!response.ok) throw new Error('Failed to fetch stats');
        
        const courses = await response.json();
        
        document.getElementById('totalCourses').textContent = courses.length;
        
        let totalQuizzes = 0;
        let totalStudents = 0;
        courses.forEach(course => {
            totalQuizzes += course.totalQuizzes || 0;
            totalStudents += course.totalStudents || 0;
        });
        
        document.getElementById('totalQuizzes').textContent = totalQuizzes;
        document.getElementById('totalStudents').textContent = totalStudents;
    } catch (error) {
        console.error('Error loading stats:', error);
        document.getElementById('totalCourses').textContent = '0';
        document.getElementById('totalQuizzes').textContent = '0';
        document.getElementById('totalStudents').textContent = '0';
    }
}

function viewCourse(courseId) {
    window.location.href = `/teacher/course/${courseId}`;
}

function editCourse(courseId) {
    window.location.href = `/teacher/edit-course/${courseId}`;
}

function showDeleteModal(courseId, courseTitle) {
    courseToDelete = courseId;
    document.getElementById('courseNameToDelete').textContent = courseTitle;
    document.getElementById('deleteModal').style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
    courseToDelete = null;
}

async function confirmDelete() {
    if (courseToDelete) {
        try {
            const response = await fetch(`/teacher/delete-course/${courseToDelete}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    showNotification('Cours supprimé avec succès', 'success');
                    closeDeleteModal();
                    await loadCourses();
                    await loadStats();
                } else {
                    throw new Error(data.message || 'Delete failed');
                }
            } else {
                throw new Error('Delete failed');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Erreur lors de la suppression', 'error');
        }
    }
}

// ==================== UTILITY FUNCTIONS ====================
function showNotification(message, type) {
    // Supprimer les notifications existantes
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    notification.innerHTML = `
        <i class="fas ${icon}"></i>
        <span>${message}</span>
    `;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function logout() {
    if (confirm('Voulez-vous vraiment vous déconnecter ?')) {
        window.location.href = '/logout';
    }
}

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing...');
    
    // Load initial data
    loadCourses();
    loadStats();
    
    // Setup file upload
    setupFileUpload();
    
    // Setup button event listeners
    const openBtn = document.getElementById('openNewCourseBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    
    if (openBtn) {
        console.log('Open button found');
        openBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openAddCourseModal();
        });
    } else {
        console.error('Open button not found!');
    }
    
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', closeAddCourseModal);
    }
    
    if (cancelModalBtn) {
        cancelModalBtn.addEventListener('click', closeAddCourseModal);
    }
    
    // Setup form submission
    const courseForm = document.getElementById('courseForm');
    if (courseForm) {
        courseForm.addEventListener('submit', submitCourseForm);
    }
    
    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        const addModal = document.getElementById('addCourseModal');
        const deleteModal = document.getElementById('deleteModal');
        
        if (event.target === addModal) {
            closeAddCourseModal();
        }
        
        if (event.target === deleteModal) {
            closeDeleteModal();
        }
    });
    
    console.log('Initialization complete');
});