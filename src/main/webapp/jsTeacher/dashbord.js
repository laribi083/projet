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

// File upload handling
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
        });
        
        dropArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
        });
        
        dropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
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

function updateFileList() {
    const fileListDiv = document.getElementById('fileList');
    if (!fileListDiv) return;
    
    if (selectedFiles.length === 0) {
        fileListDiv.innerHTML = '';
        return;
    }
    
    fileListDiv.innerHTML = selectedFiles.map((file, index) => `
        <div class="file-item">
            <div>
                <i class="fas fa-file-alt"></i>
                <span>${escapeHtml(file.name)}</span>
                <small style="color:#6b7280; margin-left:8px;">(${(file.size / 1024).toFixed(1)} KB)</small>
            </div>
            <button type="button" class="remove-file" onclick="removeFile(${index})">
                <i class="fas fa-trash-alt"></i>
            </button>
        </div>
    `).join('');
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
    
    selectedFiles.forEach(file => {
        formData.append('files', file);
    });
    
    try {
        const response = await fetch('/teacher/api/courses', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            showNotification('Cours créé avec succès!', 'success');
            closeAddCourseModal();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error('Erreur lors de la création');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors de la création du cours', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
    }
}

// ==================== COURSE MANAGEMENT ====================
async function loadCourses() {
    try {
        const response = await fetch('/teacher/api/courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesGrid = document.getElementById('coursesGrid');
        
        if (!coursesGrid) return;
        
        if (courses.length === 0) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book-open"></i>
                    <p>Vous n'avez pas encore créé de cours</p>
                    <button class="btn-primary" onclick="openAddCourseModal()">
                        Créer votre premier cours
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
        const response = await fetch('/teacher/api/courses');
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
                showNotification('Cours supprimé avec succès', 'success');
                closeDeleteModal();
                loadCourses();
                loadStats();
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