/**
 * dashbord.js - Teacher Dashboard Logic
 * Version complète avec mise à jour des statistiques
 */

// ========== VARIABLES GLOBALES ==========
let courseToDelete = null;
let quizToDelete = null;
let selectedFiles = [];
let editSelectedFiles = [];
let allCourses = [];
let allQuizzes = [];

// ========== INITIALISATION PROFIL ==========
function initUserProfile() {
    let teacherName = document.getElementById('teacherName')?.value;
    if (!teacherName || teacherName === 'null' || teacherName === '') {
        teacherName = sessionStorage.getItem('teacherName') || 'Professor';
    }
    updateUserProfile(teacherName);
}

function updateUserProfile(teacherName) {
    const avatarImg = document.getElementById('userAvatar');
    const nameElement = document.getElementById('userName');
    if (avatarImg) {
        avatarImg.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(teacherName)}&background=6366f1&color=fff&size=128`;
    }
    if (nameElement) nameElement.textContent = teacherName;
    sessionStorage.setItem('teacherName', teacherName);
}

// ========== FONCTIONS UTILITAIRES ==========
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getFileIcon(fileName) {
    const ext = fileName.split('.').pop().toLowerCase();
    const icons = {
        'pdf': 'fa-file-pdf', 'doc': 'fa-file-word', 'docx': 'fa-file-word',
        'ppt': 'fa-file-powerpoint', 'pptx': 'fa-file-powerpoint',
        'jpg': 'fa-file-image', 'jpeg': 'fa-file-image', 'png': 'fa-file-image',
        'mp4': 'fa-file-video', 'txt': 'fa-file-alt'
    };
    return icons[ext] || 'fa-file';
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`;
    document.body.appendChild(notification);
    setTimeout(() => notification.remove(), 3000);
}

// ========== NAVIGATION ==========
function showCoursesSection() {
    document.getElementById('pageTitle').innerHTML = '📚 My courses';
    document.getElementById('coursesSection').style.display = 'block';
    document.getElementById('quizzesSection').style.display = 'none';
    document.querySelector('.btn-add-course').style.display = 'block';
    document.getElementById('filterModule').style.display = 'block';
    loadCourses();
    loadStats();
}

function showQuizzesSection() {
    document.getElementById('pageTitle').innerHTML = '📝 My Quizzes';
    document.getElementById('coursesSection').style.display = 'none';
    document.getElementById('quizzesSection').style.display = 'block';
    document.querySelector('.btn-add-course').style.display = 'none';
    document.getElementById('filterModule').style.display = 'none';
    loadQuizzes();
    loadStats();
}

// ========== FILTRAGE DES COURS ==========
function getUniqueModules(courses) {
    const modules = new Set();
    courses.forEach(course => {
        if (course.module && course.module.trim()) modules.add(course.module);
    });
    return Array.from(modules).sort();
}

function updateModuleFilter(courses) {
    const filterSelect = document.getElementById('filterModule');
    if (!filterSelect) return;
    
    const uniqueModules = getUniqueModules(courses);
    filterSelect.innerHTML = '<option value="all">📂 All Modules</option>';
    uniqueModules.forEach(module => {
        filterSelect.innerHTML += `<option value="${escapeHtml(module)}">📁 ${escapeHtml(module)}</option>`;
    });
}

function filterCoursesByModule(courses, selectedModule) {
    if (!selectedModule || selectedModule === 'all') return courses;
    return courses.filter(course => course.module === selectedModule);
}

function displayFilteredCourses(courses) {
    const coursesGrid = document.getElementById('coursesGrid');
    const selectedModule = document.getElementById('filterModule')?.value || 'all';
    const filteredCourses = filterCoursesByModule(courses, selectedModule);
    
    if (!coursesGrid) return;
    
    if (filteredCourses.length === 0) {
        coursesGrid.innerHTML = `<div class="empty-state"><i class="fas fa-folder-open"></i><p>Aucun cours trouvé</p></div>`;
        return;
    }
    
    coursesGrid.innerHTML = '';
    filteredCourses.forEach(course => {
        coursesGrid.appendChild(createCourseCard(course));
    });
}

function handleFilterChange() {
    displayFilteredCourses(allCourses);
}

// ========== CRÉATION DE CARTE DE COURS ==========
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
                    <span><i class="fas fa-tag"></i> ${escapeHtml(course.module || 'Non défini')}</span>
                    <span><i class="fas fa-question-circle"></i> ${course.quizCount || 0} quiz</span>
                    <span><i class="fas fa-users"></i> ${course.totalStudents || 0} étudiants</span>
                </div>
                <div class="course-actions">
                    <button class="btn-view" onclick="viewCourse(${course.id})"><i class="fas fa-eye"></i> Voir</button>
                    <button class="btn-edit" onclick="editCourse(${course.id})"><i class="fas fa-edit"></i> Modifier</button>
                    <button class="btn-quiz" onclick="createQuizForCourse(${course.id}, '${escapeHtml(course.module || course.niveau)}', '${escapeHtml(course.niveau)}')">
                        <i class="fas fa-question-circle"></i> Quiz
                    </button>
                    <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')"><i class="fas fa-trash"></i> Supprimer</button>
                </div>
            </div>
        </div>
    `;
    return div;
}

// ========== CRÉATION DE CARTE DE QUIZ ==========
function createQuizCard(quiz) {
    const div = document.createElement('div');
    div.className = 'quiz-card';
    div.innerHTML = `
        <div class="quiz-image">
            📝
            <span class="quiz-badge">${quiz.status === 'ACTIVE' ? 'Actif' : 'Inactif'}</span>
        </div>
        <div class="quiz-content">
            <h3 class="quiz-title">${escapeHtml(quiz.title)}</h3>
            <p class="quiz-description">${escapeHtml(quiz.description ? quiz.description.substring(0, 100) : 'Aucune description')}${quiz.description && quiz.description.length > 100 ? '...' : ''}</p>
            <div class="quiz-meta">
                <div class="quiz-stats">
                    <span><i class="fas fa-book"></i> Cours: ${escapeHtml(quiz.courseTitle || 'N/A')}</span>
                    <span><i class="fas fa-tag"></i> Module: ${escapeHtml(quiz.module || 'N/A')}</span>
                    <span><i class="fas fa-graduation-cap"></i> Niveau: ${escapeHtml(quiz.niveau || 'N/A')}</span>
                </div>
                <div class="quiz-details">
                    <span><i class="fas fa-clock"></i> Durée: ${quiz.timeLimit || 30} min</span>
                    <span><i class="fas fa-question-circle"></i> ${quiz.totalQuestions || 0} questions</span>
                    <span><i class="fas fa-check-circle"></i> Score requis: ${quiz.passingScore || 70}%</span>
                </div>
                <div class="quiz-actions">
                    <button class="btn-view" onclick="previewQuiz(${quiz.id})">
                        <i class="fas fa-eye"></i> Aperçu
                    </button>
                    <button class="btn-delete" onclick="showDeleteQuizModal(${quiz.id}, '${escapeHtml(quiz.title)}')">
                        <i class="fas fa-trash"></i> Supprimer
                    </button>
                </div>
            </div>
        </div>
    `;
    return div;
}

// ========== CHARGEMENT DES COURS ==========
async function loadCourses() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        allCourses = await response.json();
        
        updateModuleFilter(allCourses);
        displayFilteredCourses(allCourses);
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors du chargement', 'error');
    }
}

// ========== CHARGEMENT DES QUIZZES ==========
async function loadQuizzes() {
    try {
        const response = await fetch('/teacher/api/quizzes');
        if (!response.ok) throw new Error('Failed to fetch quizzes');
        
        allQuizzes = await response.json();
        const quizzesGrid = document.getElementById('quizzesGrid');
        
        if (!quizzesGrid) return;
        
        if (allQuizzes.length === 0) {
            quizzesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-question-circle"></i>
                    <p>You have not yet created any quiz</p>
                    <button class="btn-primary" onclick="showCoursesSection()">
                        Go to My Courses to create a quiz
                    </button>
                </div>
            `;
            return;
        }
        
        quizzesGrid.innerHTML = '';
        allQuizzes.forEach(quiz => {
            quizzesGrid.appendChild(createQuizCard(quiz));
        });
        
    } catch (error) {
        console.error('Error loading quizzes:', error);
        showNotification('Erreur lors du chargement des quiz', 'error');
    }
}

// ========== CHARGEMENT DES STATISTIQUES (AVEC API /teacher/api/stats) ==========
async function loadStats() {
    try {
        const response = await fetch('/teacher/api/stats');
        if (!response.ok) throw new Error('Failed to fetch stats');
        
        const stats = await response.json();
        
        document.getElementById('totalCourses').textContent = stats.totalCourses || 0;
        document.getElementById('totalQuizzes').textContent = stats.totalQuizzes || 0;
        document.getElementById('totalStudents').textContent = stats.totalStudents || 0;
        
        console.log(`📊 Stats mises à jour: ${stats.totalCourses} cours, ${stats.totalQuizzes} quiz, ${stats.totalStudents} étudiants`);
        
    } catch (error) {
        console.error('Error loading stats:', error);
        document.getElementById('totalCourses').textContent = '0';
        document.getElementById('totalQuizzes').textContent = '0';
        document.getElementById('totalStudents').textContent = '0';
    }
}

// ========== ACTIONS SUR LES COURS ==========
function viewCourse(courseId) {
    window.location.href = `/teacher/view-course/${courseId}`;
}

function editCourse(courseId) {
    openEditCourseModal(courseId);
}

function createQuizForCourse(courseId, courseModule, courseNiveau) {
    window.location.href = `/teacher/create-quiz?courseId=${courseId}&courseModule=${encodeURIComponent(courseModule)}&courseNiveau=${encodeURIComponent(courseNiveau)}`;
}

// ========== ACTIONS SUR LES QUIZZES ==========
function previewQuiz(quizId) {
    window.location.href = `/quiz/preview/${quizId}`;
}

// ========== MODAL DE MODIFICATION DE COURS ==========
async function openEditCourseModal(courseId) {
    try {
        const response = await fetch(`/teacher/course/${courseId}`);
        if (!response.ok) throw new Error('Failed to fetch course');
        
        const course = await response.json();
        
        document.getElementById('editCourseId').value = course.id;
        document.getElementById('editCourseTitle').value = course.title || '';
        document.getElementById('editCourseDescription').value = course.description || '';
        document.getElementById('editCourseModule').value = course.module || '';
        document.getElementById('editCourseNiveau').value = course.niveau || '1year';
        
        editSelectedFiles = [];
        updateEditFileList();
        
        document.getElementById('editCourseModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors du chargement', 'error');
    }
}

function closeEditCourseModal() {
    document.getElementById('editCourseModal').style.display = 'none';
    document.body.style.overflow = 'auto';
    editSelectedFiles = [];
}

function updateEditFileList() {
    const fileListDiv = document.getElementById('editFileList');
    if (!fileListDiv) return;
    
    if (editSelectedFiles.length === 0) {
        fileListDiv.innerHTML = '';
        return;
    }
    
    fileListDiv.innerHTML = editSelectedFiles.map((file, index) => `
        <div class="file-item">
            <i class="fas ${getFileIcon(file.name)}"></i>
            <span>${escapeHtml(file.name)}</span>
            <small>(${formatFileSize(file.size)})</small>
            <button onclick="removeEditFile(${index})" class="remove-file">🗑</button>
        </div>
    `).join('');
}

function removeEditFile(index) {
    editSelectedFiles.splice(index, 1);
    updateEditFileList();
}

function setupEditFileUpload() {
    const fileInput = document.getElementById('editFilesInput');
    const dropArea = document.getElementById('editFileUploadArea');
    
    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            editSelectedFiles = [...editSelectedFiles, ...Array.from(e.target.files)];
            updateEditFileList();
        });
    }
    
    if (dropArea) {
        dropArea.addEventListener('click', () => fileInput?.click());
        dropArea.addEventListener('dragover', (e) => e.preventDefault());
        dropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            editSelectedFiles = [...editSelectedFiles, ...Array.from(e.dataTransfer.files)];
            updateEditFileList();
        });
    }
}

async function submitEditCourseForm(event) {
    event.preventDefault();
    
    const submitBtn = document.getElementById('editSubmitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    
    const formData = new FormData();
    formData.append('title', document.getElementById('editCourseTitle').value.trim());
    formData.append('description', document.getElementById('editCourseDescription').value.trim());
    formData.append('module', document.getElementById('editCourseModule').value);
    formData.append('niveau', document.getElementById('editCourseNiveau').value);
    editSelectedFiles.forEach(file => formData.append('newFiles', file));
    
    const courseId = document.getElementById('editCourseId').value;
    
    try {
        const response = await fetch(`/teacher/api/update-course/${courseId}`, {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Course updated successfully!', 'success');
            closeEditCourseModal();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Save Changes';
    }
}

// ========== MODAL DE SUPPRESSION DE COURS ==========
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
    if (!courseToDelete) return;
    
    try {
        const response = await fetch(`/teacher/delete-course/${courseToDelete}`, { method: 'DELETE' });
        const data = await response.json();
        
        if (data.success) {
            showNotification('Course deleted successfully!', 'success');
            closeDeleteModal();
            await loadCourses();
            await loadQuizzes();
            await loadStats();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== MODAL DE SUPPRESSION DE QUIZ ==========
function showDeleteQuizModal(quizId, quizTitle) {
    quizToDelete = quizId;
    document.getElementById('quizNameToDelete').textContent = quizTitle;
    document.getElementById('deleteQuizModal').style.display = 'flex';
}

function closeDeleteQuizModal() {
    document.getElementById('deleteQuizModal').style.display = 'none';
    quizToDelete = null;
}

async function confirmDeleteQuiz() {
    if (!quizToDelete) return;
    
    try {
        const response = await fetch(`/teacher/api/delete-quiz/${quizToDelete}`, { method: 'DELETE' });
        const data = await response.json();
        
        if (data.success) {
            showNotification('Quiz deleted successfully!', 'success');
            closeDeleteQuizModal();
            await loadQuizzes();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    }
}

// ========== MODAL DE CRÉATION DE COURS ==========
function openAddCourseModal() {
    document.getElementById('addCourseModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeAddCourseModal() {
    document.getElementById('addCourseModal').classList.remove('active');
    document.body.style.overflow = 'auto';
    selectedFiles = [];
    updateFileList();
    document.getElementById('courseForm')?.reset();
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
            <i class="fas ${getFileIcon(file.name)}"></i>
            <span>${escapeHtml(file.name)}</span>
            <small>(${formatFileSize(file.size)})</small>
            <button onclick="removeFile(${index})" class="remove-file">🗑</button>
        </div>
    `).join('');
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
}

function setupFileUpload() {
    const fileInput = document.getElementById('filesInput');
    const dropArea = document.getElementById('fileUploadArea');
    
    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            selectedFiles = [...selectedFiles, ...Array.from(e.target.files)];
            updateFileList();
        });
    }
    
    if (dropArea) {
        dropArea.addEventListener('click', () => fileInput?.click());
        dropArea.addEventListener('dragover', (e) => e.preventDefault());
        dropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            selectedFiles = [...selectedFiles, ...Array.from(e.dataTransfer.files)];
            updateFileList();
        });
    }
}

async function submitCourseForm(event) {
    event.preventDefault();
    
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating...';
    
    const formData = new FormData();
    formData.append('title', document.getElementById('courseTitle').value.trim());
    formData.append('description', document.getElementById('courseDescription').value.trim());
    formData.append('niveau', document.getElementById('courseLevel').value);
    selectedFiles.forEach(file => formData.append('files', file));
    
    try {
        const response = await fetch('/teacher/api/courses', { method: 'POST', body: formData });
        const data = await response.json();
        
        if (data.success) {
            showNotification('Course created successfully!', 'success');
            closeAddCourseModal();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        showNotification('Error: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Create';
    }
}

// ========== DÉCONNEXION ==========
function logout() {
    if (confirm('Voulez-vous vous déconnecter ?')) {
        sessionStorage.clear();
        window.location.href = '/logout';
    }
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    initUserProfile();
    loadCourses();
    loadStats();
    setupFileUpload();
    setupEditFileUpload();
    
    // Navigation
    const myCoursesLink = document.getElementById('myCoursesLink');
    const myQuizzesLink = document.getElementById('myQuizzesLink');
    
    if (myCoursesLink) {
        myCoursesLink.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            myCoursesLink.classList.add('active');
            showCoursesSection();
        });
    }
    
    if (myQuizzesLink) {
        myQuizzesLink.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            myQuizzesLink.classList.add('active');
            showQuizzesSection();
        });
    }
    
    // Boutons
    document.getElementById('openNewCourseBtn')?.addEventListener('click', openAddCourseModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeAddCourseModal);
    document.getElementById('cancelModalBtn')?.addEventListener('click', closeAddCourseModal);
    document.getElementById('courseForm')?.addEventListener('submit', submitCourseForm);
    document.getElementById('editCourseForm')?.addEventListener('submit', submitEditCourseForm);
    document.getElementById('filterModule')?.addEventListener('change', handleFilterChange);
    
    // Fermer les modaux en cliquant en dehors
    window.addEventListener('click', (event) => {
        if (event.target === document.getElementById('addCourseModal')) closeAddCourseModal();
        if (event.target === document.getElementById('editCourseModal')) closeEditCourseModal();
        if (event.target === document.getElementById('deleteModal')) closeDeleteModal();
        if (event.target === document.getElementById('deleteQuizModal')) closeDeleteQuizModal();
    });
});