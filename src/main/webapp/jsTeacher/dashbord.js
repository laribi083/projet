/**
 * dashbord.js - Teacher Dashboard Logic
 * Version complète avec teacherId, teacherName, suppression corrigée et création de quiz
 */

// ========== VARIABLES GLOBALES ==========
let courseToDelete = null;
let selectedFiles = [];

// ========== MODAL MANAGEMENT ==========
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

// ========== FILE UPLOAD ==========
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

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getFileIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'svg'].includes(extension)) return 'fa-file-image';
    if (extension === 'pdf') return 'fa-file-pdf';
    if (['doc', 'docx'].includes(extension)) return 'fa-file-word';
    if (['xls', 'xlsx'].includes(extension)) return 'fa-file-excel';
    if (['ppt', 'pptx'].includes(extension)) return 'fa-file-powerpoint';
    if (['mp4', 'avi', 'mov'].includes(extension)) return 'fa-file-video';
    if (['mp3', 'wav'].includes(extension)) return 'fa-file-audio';
    if (['zip', 'rar'].includes(extension)) return 'fa-file-archive';
    return 'fa-file';
}

function updateFileList() {
    const fileListDiv = document.getElementById('fileList');
    if (!fileListDiv) return;
    
    if (selectedFiles.length === 0) {
        fileListDiv.innerHTML = '';
        return;
    }
    
    const totalSize = selectedFiles.reduce((sum, file) => sum + file.size, 0);
    const totalSizeFormatted = formatFileSize(totalSize);
    
    const header = `
        <div class="file-stats">
            <span>📁 ${selectedFiles.length} fichier(s)</span>
            <span>💾 ${totalSizeFormatted}</span>
        </div>
    `;
    
    const filesList = selectedFiles.map((file, index) => `
        <div class="file-item">
            <i class="fas ${getFileIcon(file.name)}"></i>
            <span>${escapeHtml(file.name)}</span>
            <small>(${formatFileSize(file.size)})</small>
            <button onclick="removeFile(${index})" class="remove-file">🗑</button>
        </div>
    `).join('');
    
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

// ========== COURSE SUBMISSION ==========
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
    
    const teacherId = document.getElementById('teacherId')?.value;
    const teacherName = document.getElementById('teacherName')?.value;
    
    if (teacherId) formData.append('teacherId', teacherId);
    if (teacherName) formData.append('teacherName', teacherName);
    
    selectedFiles.forEach(file => {
        formData.append('files', file);
    });
    
    console.log('📤 Envoi du cours:', { title, niveau, teacherId, teacherName });
    
    try {
        const response = await fetch('/teacher/api/courses', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Cours créé avec succès!', 'success');
            closeAddCourseModal();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
    }
}

// ========== COURSE MANAGEMENT ==========
async function loadCourses() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesGrid = document.getElementById('coursesGrid');
        
        console.log('📚 Cours chargés:', courses.length);
        
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
            coursesGrid.appendChild(createCourseCard(course));
        });
    } catch (error) {
        console.error('Error loading courses:', error);
        showNotification('Erreur lors du chargement des cours', 'error');
    }
}

// ⭐ FONCTION CREATE COURSE CARD AVEC BOUTON CREATE QUIZ ⭐
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
                    <button class="btn-quiz" onclick="createQuiz(${course.id}, '${escapeHtml(course.module)}', '${escapeHtml(course.niveau)}')">
                        <i class="fas fa-question-circle"></i> Create Quiz
                    </button>
                    <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')">🗑 Supprimer</button>
                </div>
            </div>
        </div>
    `;
    return div;
}

async function loadStats() {
    try {
        const response = await fetch('/teacher/my-courses');
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

// ⭐ FONCTION POUR CRÉER UN QUIZ ⭐
function createQuiz(courseId, courseModule, courseNiveau) {
    console.log('📝 Création d\'un quiz pour le cours:', { courseId, courseModule, courseNiveau });
    window.location.href = `/teacher/create-quiz?courseId=${courseId}&courseModule=${courseModule}&courseNiveau=${courseNiveau}`;
}

// ========== DELETE MODAL ==========
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
            console.log('🗑 Suppression du cours ID:', courseToDelete);
            
            const response = await fetch(`/teacher/delete-course/${courseToDelete}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
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
                throw new Error('Erreur HTTP: ' + response.status);
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Erreur lors de la suppression: ' + error.message, 'error');
        }
    }
}

// ========== UTILITIES ==========
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => notification.remove(), 3000);
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

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing...');
    
    loadCourses();
    loadStats();
    setupFileUpload();
    
    const openBtn = document.getElementById('openNewCourseBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    
    if (openBtn) {
        openBtn.addEventListener('click', (e) => {
            e.preventDefault();
            openAddCourseModal();
        });
    }
    
    if (closeModalBtn) closeModalBtn.addEventListener('click', closeAddCourseModal);
    if (cancelModalBtn) cancelModalBtn.addEventListener('click', closeAddCourseModal);
    
    const courseForm = document.getElementById('courseForm');
    if (courseForm) courseForm.addEventListener('submit', submitCourseForm);
    
    window.addEventListener('click', (event) => {
        const addModal = document.getElementById('addCourseModal');
        const deleteModal = document.getElementById('deleteModal');
        if (event.target === addModal) closeAddCourseModal();
        if (event.target === deleteModal) closeDeleteModal();
    });
    
    console.log('Initialization complete');
});