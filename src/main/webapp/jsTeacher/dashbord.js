/**
 * dashbord.js - Teacher Dashboard Logic
 * Version complète avec toutes les fonctions (Courses, Quizzes, Ratings)
 */

// ========== VARIABLES GLOBALES ==========
let courseToDelete = null;
let quizToDelete = null;
let selectedFiles = [];
let editSelectedFiles = [];
let allCourses = [];
let allQuizzes = [];
let tempQuestionsList = [];

// ========== INITIALISATION PROFIL ==========
function initUserProfile() {
    let teacherName = document.getElementById('teacherName')?.value;
    if (!teacherName || teacherName === 'null' || teacherName === '') {
        teacherName = sessionStorage.getItem('teacherName') || 'Professor';
    }
    updateUserProfile(teacherName);
    loadUserData();
}

function updateUserProfile(teacherName) {
    const avatarImg = document.getElementById('userAvatar');
    const nameElement = document.getElementById('userName');
    if (avatarImg) {
        avatarImg.src = getUserAvatar(teacherName);
    }
    if (nameElement) nameElement.textContent = teacherName;
    sessionStorage.setItem('teacherName', teacherName);
}

function getUserAvatar(name) {
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=6366f1&color=fff&size=128`;
}

function loadUserData() {
    const teacherId = document.getElementById('teacherId')?.value;
    const teacherName = document.getElementById('teacherName')?.value;
    const teacherEmail = document.getElementById('teacherEmail')?.value;
    
    if (teacherName) {
        const userNameElement = document.getElementById('userName');
        if (userNameElement) userNameElement.textContent = teacherName;
    }
    
    console.log('📋 Données enseignant:', { teacherId, teacherName, teacherEmail });
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
        'mp4': 'fa-file-video', 'txt': 'fa-file-alt', 'html': 'fa-code',
        'htm': 'fa-code', 'css': 'fa-css3', 'js': 'fa-js'
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

// ========== FONCTIONS DE STATUT ==========
function getStatusClass(status) {
    if (status === 'PENDING') return 'pending';
    if (status === 'VALIDATED') return 'validated';
    if (status === 'PUBLISHED' || status === 'ACTIVE') return 'published';
    return 'pending';
}

function getStatusLabel(status) {
    if (status === 'PENDING') return '⏳ PENDING';
    if (status === 'VALIDATED') return '✅ VALIDATED';
    if (status === 'PUBLISHED' || status === 'ACTIVE') return '📖 PUBLISHED';
    return '⏳ PENDING';
}

// ========== NAVIGATION ==========
function showCoursesSection() {
    const pageTitle = document.querySelector('.page-title');
    const addCourseBtn = document.querySelector('.btn-add-course');
    
    if (pageTitle) pageTitle.innerHTML = '📚 My Courses';
    if (addCourseBtn) addCourseBtn.style.display = 'flex';
    
    document.querySelectorAll('.courses-section').forEach(section => {
        if (section.id !== 'quizzesSection' && section.id !== 'ratingsSection') {
            section.style.display = 'block';
        }
    });
    
    const quizzesSection = document.getElementById('quizzesSection');
    const ratingsSection = document.getElementById('ratingsSection');
    if (quizzesSection) quizzesSection.style.display = 'none';
    if (ratingsSection) ratingsSection.style.display = 'none';
    
    loadCourses();
    loadStats();
}

function showQuizzesSection() {
    const pageTitle = document.querySelector('.page-title');
    const addCourseBtn = document.querySelector('.btn-add-course');
    
    if (pageTitle) pageTitle.innerHTML = '📝 My Quizzes';
    if (addCourseBtn) addCourseBtn.style.display = 'none';
    
    document.querySelectorAll('.courses-section').forEach(section => {
        if (section.id !== 'quizzesSection') {
            section.style.display = 'none';
        }
    });
    
    const quizzesSection = document.getElementById('quizzesSection');
    if (quizzesSection) quizzesSection.style.display = 'block';
    
    const ratingsSection = document.getElementById('ratingsSection');
    if (ratingsSection) ratingsSection.style.display = 'none';
    
    loadQuizzes();
    loadStats();
}

function showRatingsSection() {
    console.log("=== AFFICHAGE DE LA SECTION DES RATINGS ===");
    
    const pageTitle = document.querySelector('.page-title');
    const addCourseBtn = document.querySelector('.btn-add-course');
    
    if (pageTitle) pageTitle.innerHTML = '⭐ Course Ratings';
    if (addCourseBtn) addCourseBtn.style.display = 'none';
    
    document.querySelectorAll('.courses-section').forEach(section => {
        section.style.display = 'none';
    });
    
    const ratingsSection = document.getElementById('ratingsSection');
    if (ratingsSection) {
        ratingsSection.style.display = 'block';
        
        const ratingsGrid = document.getElementById('ratingsGrid');
        if (ratingsGrid) {
            ratingsGrid.innerHTML = `
                <div class="loading" style="text-align: center; padding: 3rem; grid-column: 1/-1;">
                    <i class="fas fa-spinner fa-spin fa-2x"></i>
                    <p>Loading ratings...</p>
                </div>
            `;
        }
    }
    
    loadRatings();
    loadStats();
}

// ========== CHARGEMENT DES COURS ==========
async function loadCourses() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        allCourses = await response.json();
        
        const pendingCount = allCourses.filter(c => c.status === 'PENDING').length;
        const validatedCount = allCourses.filter(c => c.status === 'VALIDATED').length;
        const publishedCount = allCourses.filter(c => c.status === 'PUBLISHED' || c.status === 'ACTIVE').length;
        
        const pendingHeader = document.querySelector('.courses-section:first-child .count');
        const validatedHeader = document.querySelector('.courses-section:nth-child(2) .count');
        const publishedHeader = document.querySelector('.courses-section:nth-child(3) .count');
        
        if (pendingHeader) pendingHeader.textContent = pendingCount;
        if (validatedHeader) validatedHeader.textContent = validatedCount;
        if (publishedHeader) publishedHeader.textContent = publishedCount;
        
        displayCoursesByStatus();
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error loading courses', 'error');
    }
}

function displayCoursesByStatus() {
    const pendingCourses = allCourses.filter(c => c.status === 'PENDING');
    const validatedCourses = allCourses.filter(c => c.status === 'VALIDATED');
    const publishedCourses = allCourses.filter(c => c.status === 'PUBLISHED' || c.status === 'ACTIVE');
    
    const pendingGrid = document.getElementById('pendingCoursesGrid');
    if (pendingGrid) {
        if (pendingCourses.length === 0) {
            pendingGrid.innerHTML = `<div class="empty-message"><i class="fas fa-check-circle"></i><p>No pending courses</p></div>`;
        } else {
            pendingGrid.innerHTML = '';
            pendingCourses.forEach(course => {
                pendingGrid.appendChild(createCourseCard(course));
            });
        }
    }
    
    const validatedGrid = document.getElementById('validatedCoursesGrid');
    if (validatedGrid) {
        if (validatedCourses.length === 0) {
            validatedGrid.innerHTML = `<div class="empty-message"><i class="fas fa-check-circle"></i><p>No validated courses waiting for publication</p></div>`;
        } else {
            validatedGrid.innerHTML = '';
            validatedCourses.forEach(course => {
                validatedGrid.appendChild(createCourseCard(course));
            });
        }
    }
    
    const publishedGrid = document.getElementById('publishedCoursesGrid');
    if (publishedGrid) {
        if (publishedCourses.length === 0) {
            publishedGrid.innerHTML = `<div class="empty-message"><i class="fas fa-book-open"></i><p>No published courses yet</p></div>`;
        } else {
            publishedGrid.innerHTML = '';
            publishedCourses.forEach(course => {
                publishedGrid.appendChild(createCourseCard(course));
            });
        }
    }
}

function createCourseCard(course) {
    const div = document.createElement('div');
    const statusClass = getStatusClass(course.status);
    div.className = `course-card ${statusClass}`;
    div.setAttribute('data-course-id', course.id);
    
    div.innerHTML = `
        <h3>
            ${escapeHtml(course.title)}
            <span class="status-badge status-${statusClass}">${getStatusLabel(course.status)}</span>
        </h3>
        <p>${escapeHtml(course.description ? course.description.substring(0, 100) : '')}${course.description && course.description.length > 100 ? '...' : ''}</p>
        <div class="course-meta">
            <span>📚 ${escapeHtml(course.module || 'Not defined')}</span>
            <span>🎓 ${escapeHtml(course.niveau || 'N/A')}</span>
            <span>📝 Quiz: ${course.quizCount || 0}</span>
            ${course.status === 'PUBLISHED' || course.status === 'ACTIVE' ? `<span>👥 Students: ${course.totalStudents || 0}</span>` : ''}
        </div>
        <div class="course-actions">
            <button class="btn-view" data-id="${course.id}" onclick="viewCourse(this)"><i class="fas fa-eye"></i> View</button>
            <button class="btn-edit" data-id="${course.id}" onclick="editCourse(this)"><i class="fas fa-edit"></i> Edit</button>
            <button class="btn-delete" data-id="${course.id}" data-title="${escapeHtml(course.title)}" onclick="showDeleteModal(this)"><i class="fas fa-trash"></i> Delete</button>
            ${course.status === 'PUBLISHED' || course.status === 'ACTIVE' ? `<button class="btn-add-quiz" data-id="${course.id}" data-module="${escapeHtml(course.module)}" data-niveau="${escapeHtml(course.niveau)}" onclick="openCreateQuizModal(this)"><i class="fas fa-plus"></i> Add Quiz</button>` : ''}
        </div>
    `;
    return div;
}

// ========== CHARGEMENT DES QUIZZES ==========
async function loadQuizzes() {
    console.log("=== CHARGEMENT DES QUIZ ===");
    
    try {
        const response = await fetch('/teacher/api/quizzes');
        if (!response.ok) throw new Error('Failed to fetch quizzes');
        
        const data = await response.json();
        console.log("Données reçues:", data);
        
        const quizzesGrid = document.getElementById('quizzesGrid');
        const quizzesCount = document.getElementById('quizzesCount');
        
        if (!quizzesGrid) return;
        
        let quizzes = [];
        if (data.success && Array.isArray(data.quizzes)) {
            quizzes = data.quizzes;
        } else if (Array.isArray(data)) {
            quizzes = data;
        } else {
            quizzes = [];
        }
        
        if (quizzesCount) quizzesCount.textContent = quizzes.length;
        
        if (quizzes.length === 0) {
            quizzesGrid.innerHTML = `
                <div class="empty-message" style="grid-column: 1/-1; text-align: center; padding: 3rem;">
                    <i class="fas fa-question-circle" style="font-size: 3rem; color: #cbd5e1;"></i>
                    <h3>No quizzes created yet</h3>
                    <p>You haven't created any quiz for your courses.</p>
                    <button class="btn-add-course" onclick="showCoursesSection()" style="margin-top: 1rem;">
                        Go to My Courses to create a quiz
                    </button>
                </div>
            `;
            return;
        }
        
        quizzesGrid.innerHTML = '';
        quizzes.forEach(quiz => {
            const quizCard = createQuizCard(quiz);
            quizzesGrid.appendChild(quizCard);
        });
        
    } catch (error) {
        console.error('Error loading quizzes:', error);
        showNotification('Error loading quizzes: ' + error.message, 'error');
    }
}

function createQuizCard(quiz) {
    const div = document.createElement('div');
    div.className = 'course-card published';
    div.setAttribute('data-quiz-id', quiz.id);
    
    const title = quiz.title || 'Sans titre';
    const description = quiz.description ? quiz.description.substring(0, 100) : 'Aucune description';
    const courseTitle = quiz.courseTitle || 'Cours inconnu';
    const timeLimit = quiz.timeLimit || 30;
    const totalQuestions = quiz.totalQuestions || 0;
    const passingScore = quiz.passingScore || 70;
    const module = quiz.module || 'Non défini';
    
    div.innerHTML = `
        <h3>
            ${escapeHtml(title)} 
            <span class="status-badge status-published">
                <i class="fas fa-question-circle"></i> QUIZ
            </span>
        </h3>
        <p>${escapeHtml(description)}</p>
        <div class="course-meta">
            <span><i class="fas fa-book"></i> ${escapeHtml(courseTitle)}</span>
            <span><i class="fas fa-folder"></i> ${escapeHtml(module)}</span>
            <span><i class="fas fa-clock"></i> ${timeLimit} min</span>
            <span><i class="fas fa-question-circle"></i> ${totalQuestions} questions</span>
            <span><i class="fas fa-trophy"></i> ${passingScore}% requis</span>
        </div>
        <div class="course-actions">
            <button class="btn-view" onclick="previewQuiz(${quiz.id})">
                <i class="fas fa-eye"></i> Preview
            </button>
            <button class="btn-delete" onclick="showDeleteQuizModal(${quiz.id}, '${escapeHtml(title)}')">
                <i class="fas fa-trash"></i> Delete
            </button>
        </div>
    `;
    return div;
}

// ========== CHARGEMENT DES RATINGS ==========
async function loadRatings() {
    console.log("=== CHARGEMENT DES RATINGS ===");
    
    try {
        const response = await fetch('/api/ratings/teacher/all');
        
        if (!response.ok) {
            throw new Error('Failed to fetch ratings: ' + response.status);
        }
        
        const data = await response.json();
        console.log("Données reçues:", data);
        
        const ratingsGrid = document.getElementById('ratingsGrid');
        const ratingsCount = document.getElementById('ratingsCount');
        
        if (!ratingsGrid) return;
        
        let ratings = [];
        if (data.success && Array.isArray(data.ratings)) {
            ratings = data.ratings;
        } else if (Array.isArray(data)) {
            ratings = data;
        } else {
            ratings = [];
        }
        
        if (ratingsCount) ratingsCount.textContent = ratings.length;
        
        if (ratings.length === 0) {
            ratingsGrid.innerHTML = `
                <div class="empty-message" style="grid-column: 1/-1; text-align: center; padding: 3rem;">
                    <i class="fas fa-star" style="font-size: 3rem; color: #cbd5e1;"></i>
                    <h3>No ratings yet</h3>
                    <p>Your courses haven't received any ratings from students yet.</p>
                </div>
            `;
            return;
        }
        
        const ratingsByCourse = groupRatingsByCourse(ratings);
        displayRatingsByCourse(ratingsGrid, ratingsByCourse);
        
    } catch (error) {
        console.error('Error loading ratings:', error);
        const ratingsGrid = document.getElementById('ratingsGrid');
        if (ratingsGrid) {
            ratingsGrid.innerHTML = `
                <div class="empty-message" style="grid-column: 1/-1; text-align: center; padding: 3rem;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 3rem; color: #ef4444;"></i>
                    <h3>Error loading ratings</h3>
                    <p>${error.message}</p>
                    <button onclick="loadRatings()" style="margin-top: 1rem; padding: 0.5rem 1rem; background: #6366f1; color: white; border: none; border-radius: 8px;">
                        Retry
                    </button>
                </div>
            `;
        }
        showNotification('Error loading ratings: ' + error.message, 'error');
    }
}

function groupRatingsByCourse(ratings) {
    const groups = new Map();
    
    ratings.forEach(rating => {
        if (!groups.has(rating.courseId)) {
            groups.set(rating.courseId, {
                courseId: rating.courseId,
                courseTitle: rating.courseTitle,
                ratings: [],
                averageRating: 0
            });
        }
        groups.get(rating.courseId).ratings.push(rating);
    });
    
    for (let group of groups.values()) {
        const sum = group.ratings.reduce((acc, r) => acc + r.ratingValue, 0);
        group.averageRating = (sum / group.ratings.length).toFixed(1);
    }
    
    return Array.from(groups.values());
}

function displayRatingsByCourse(container, courseGroups) {
    container.innerHTML = '';
    
    courseGroups.forEach(group => {
        const courseDiv = document.createElement('div');
        courseDiv.className = 'course-card published';
        courseDiv.style.marginBottom = '1.5rem';
        
        courseDiv.innerHTML = `
            <h3>
                ${escapeHtml(group.courseTitle)}
                <span class="status-badge status-published">
                    <i class="fas fa-star"></i> ${group.averageRating}/5
                </span>
            </h3>
            <div class="course-meta" style="margin-bottom: 1rem;">
                <span><i class="fas fa-users"></i> ${group.ratings.length} avis</span>
            </div>
            <table class="ratings-table">
                <thead>
                    <tr>
                        <th>Étudiant</th>
                        <th>Note</th>
                        <th>Commentaire</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    ${group.ratings.map(r => `
                        <tr>
                            <td>${escapeHtml(r.studentName)}</td>
                            <td>${generateStarsStatic(r.ratingValue)}</td>
                            <td class="comment-cell">${r.comment ? `<div class="comment-text">${escapeHtml(r.comment)}</div>` : '<span style="color:#9ca3af;">Aucun commentaire</span>'}</td>
                            <td>${new Date(r.createdAt).toLocaleDateString('fr-FR')}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        container.appendChild(courseDiv);
    });
}

function generateStarsStatic(rating) {
    let stars = '<div class="stars">';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<i class="fas fa-star filled"></i>';
        } else {
            stars += '<i class="far fa-star empty"></i>';
        }
    }
    stars += '</div>';
    return stars;
}

// ========== CHARGEMENT DES STATISTIQUES ==========
async function loadStats() {
    try {
        const response = await fetch('/teacher/api/stats');
        if (!response.ok) throw new Error('Failed to fetch stats');
        
        const stats = await response.json();
        
        document.getElementById('totalQuizzes').textContent = stats.totalQuizzes || 0;
        document.getElementById('totalStudents').textContent = stats.totalStudents || 0;
        
    } catch (error) {
        console.error('Error loading stats:', error);
        document.getElementById('totalQuizzes').textContent = '0';
        document.getElementById('totalStudents').textContent = '0';
    }
}

// ========== ACTIONS SUR LES COURS ==========
function viewCourse(btn) {
    const courseId = btn.getAttribute('data-id');
    window.location.href = `/teacher/view-course/${courseId}`;
}

function editCourse(btn) {
    const courseId = btn.getAttribute('data-id');
    openEditCourseModal(courseId);
}

// ========== MODAL DE CRÉATION DE COURS ==========
function openAddCourseModal() {
    document.getElementById('addCourseModal').style.display = 'flex';
    document.body.style.overflow = 'hidden';
}

function closeAddCourseModal() {
    document.getElementById('addCourseModal').style.display = 'none';
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
        showNotification('Error loading course', 'error');
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

// ========== MODAL DE SUPPRESSION ==========
function showDeleteModal(btn) {
    courseToDelete = btn.getAttribute('data-id');
    const courseTitle = btn.getAttribute('data-title');
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

// ========== MODAL POUR CRÉER UN QUIZ ==========
function openCreateQuizModal(btn) {
    const courseId = btn.getAttribute('data-id');
    const courseModule = btn.getAttribute('data-module');
    const courseNiveau = btn.getAttribute('data-niveau');
    
    tempQuestionsList = [];
    
    document.getElementById('quizCourseId').value = courseId;
    document.getElementById('quizCourseModule').value = courseModule;
    document.getElementById('quizCourseNiveau').value = courseNiveau;
    
    document.getElementById('quizTitle').value = '';
    document.getElementById('quizDescription').value = '';
    document.getElementById('quizTimeLimit').value = '30';
    document.getElementById('quizPassingScore').value = '70';
    document.getElementById('newQuestionText').value = '';
    document.getElementById('newQuestionPoints').value = '10';
    document.getElementById('newQuestionType').value = 'SINGLE_CHOICE';
    
    resetModalOptions();
    updateQuestionsDisplay();
    
    const modal = document.getElementById('createQuizModal');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeQuizModal() {
    const modal = document.getElementById('createQuizModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function resetModalOptions() {
    const container = document.getElementById('modalOptionsContainer');
    if (container) {
        container.innerHTML = `
            <div class="option-row">
                <input type="checkbox" class="correct-checkbox">
                <input type="text" class="option-text" placeholder="Option 1" style="flex: 1;">
                <button type="button" class="remove-option-btn" onclick="removeModalOption(this)">🗑</button>
            </div>
            <div class="option-row">
                <input type="checkbox" class="correct-checkbox">
                <input type="text" class="option-text" placeholder="Option 2" style="flex: 1;">
                <button type="button" class="remove-option-btn" onclick="removeModalOption(this)">🗑</button>
            </div>
        `;
    }
}

function addModalOption() {
    const container = document.getElementById('modalOptionsContainer');
    if (container) {
        const optionCount = container.children.length + 1;
        const div = document.createElement('div');
        div.className = 'option-row';
        div.innerHTML = `
            <input type="checkbox" class="correct-checkbox">
            <input type="text" class="option-text" placeholder="Option ${optionCount}" style="flex: 1;">
            <button type="button" class="remove-option-btn" onclick="removeModalOption(this)">🗑</button>
        `;
        container.appendChild(div);
    }
}

function removeModalOption(btn) {
    const container = document.getElementById('modalOptionsContainer');
    if (container && container.children.length > 2) {
        btn.closest('.option-row').remove();
    } else {
        showNotification('You need at least 2 options', 'error');
    }
}

function addQuestionToList() {
    const questionText = document.getElementById('newQuestionText')?.value.trim();
    if (!questionText) {
        showNotification('Please enter a question', 'error');
        return;
    }
    
    const options = [];
    let correctAnswerIndex = null;
    
    document.querySelectorAll('#modalOptionsContainer .option-row').forEach((row, idx) => {
        const textInput = row.querySelector('.option-text');
        const checkbox = row.querySelector('.correct-checkbox');
        
        if (textInput && textInput.value.trim()) {
            options.push(textInput.value.trim());
            if (checkbox && checkbox.checked) {
                correctAnswerIndex = idx;
            }
        }
    });
    
    if (options.length < 2) {
        showNotification('Please add at least 2 options', 'error');
        return;
    }
    
    if (correctAnswerIndex === null) {
        showNotification('Please select the correct answer', 'error');
        return;
    }
    
    const points = parseInt(document.getElementById('newQuestionPoints')?.value || 10);
    const questionType = document.getElementById('newQuestionType')?.value || 'SINGLE_CHOICE';
    
    tempQuestionsList.push({
        id: Date.now(),
        text: questionText,
        options: options,
        correctAnswer: correctAnswerIndex,
        points: points,
        questionType: questionType,
        orderNumber: tempQuestionsList.length + 1
    });
    
    updateQuestionsDisplay();
    
    document.getElementById('newQuestionText').value = '';
    document.getElementById('newQuestionPoints').value = '10';
    resetModalOptions();
    
    showNotification('Question added!', 'success');
}

function removeQuestionFromList(questionId) {
    tempQuestionsList = tempQuestionsList.filter(q => q.id !== questionId);
    tempQuestionsList.forEach((q, idx) => q.orderNumber = idx + 1);
    updateQuestionsDisplay();
}

function updateQuestionsDisplay() {
    const questionsList = document.getElementById('questionsList');
    const emptyMsg = document.getElementById('emptyQuestionsMsg');
    const questionCountSpan = document.getElementById('questionCount');
    
    if (tempQuestionsList.length === 0) {
        if (emptyMsg) emptyMsg.style.display = 'block';
        if (questionsList) questionsList.innerHTML = '';
        if (questionCountSpan) questionCountSpan.textContent = '(0 questions)';
        return;
    }
    
    if (emptyMsg) emptyMsg.style.display = 'none';
    if (questionCountSpan) questionCountSpan.textContent = `(${tempQuestionsList.length} questions)`;
    
    questionsList.innerHTML = tempQuestionsList.map(q => `
        <div style="background: #f3f4f6; padding: 0.75rem; margin-bottom: 0.75rem; border-radius: 12px; border-left: 3px solid #667eea;">
            <div style="display: flex; justify-content: space-between; align-items: start;">
                <div style="flex: 1;">
                    <strong>${q.orderNumber}. ${escapeHtml(q.text)}</strong>
                    <div style="font-size: 0.75rem; color: #6b7280; margin-top: 0.25rem;">
                        <span><i class="fas fa-star"></i> ${q.points} pts</span>
                        <span style="margin-left: 0.5rem;"><i class="fas ${q.questionType === 'SINGLE_CHOICE' ? 'fa-dot-circle' : 'fa-check-square'}"></i> ${q.questionType === 'SINGLE_CHOICE' ? 'Single Choice' : 'Multiple Choice'}</span>
                    </div>
                    <div style="font-size: 0.7rem; color: #10b981; margin-top: 0.25rem;">
                        ✓ Correct: ${escapeHtml(q.options[q.correctAnswer] || 'N/A')}
                    </div>
                </div>
                <button onclick="removeQuestionFromList(${q.id})" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
}

// ========== SAVE QUIZ MODAL ==========
async function saveQuizModal(event) {
    event.preventDefault();
    
    const title = document.getElementById('quizTitle')?.value.trim();
    if (!title) {
        showNotification('Veuillez entrer un titre pour le quiz', 'error');
        return;
    }
    
    if (tempQuestionsList.length === 0) {
        showNotification('Veuillez ajouter au moins une question', 'error');
        return;
    }
    
    const saveBtn = document.getElementById('saveQuizBtn');
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Création en cours...';
    
    const quizData = {
        courseId: parseInt(document.getElementById('quizCourseId')?.value || 0),
        title: title,
        description: document.getElementById('quizDescription')?.value || '',
        timeLimit: parseInt(document.getElementById('quizTimeLimit')?.value || 30),
        passingScore: parseInt(document.getElementById('quizPassingScore')?.value || 70),
        courseModule: document.getElementById('quizCourseModule')?.value || '',
        courseNiveau: document.getElementById('quizCourseNiveau')?.value || ''
    };
    
    try {
        const quizResponse = await fetch('/teacher/api/create-quiz', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(quizData)
        });
        
        const quizResult = await quizResponse.json();
        
        if (!quizResult.success) {
            throw new Error(quizResult.message || 'Erreur lors de la création du quiz');
        }
        
        const quizId = quizResult.quizId;
        
        let questionsAdded = 0;
        for (const question of tempQuestionsList) {
            const questionData = {
                text: question.text,
                type: question.questionType,
                points: question.points,
                order: question.orderNumber,
                options: question.options,
                correctAnswer: question.correctAnswer
            };
            
            const questionResponse = await fetch(`/teacher/api/quizzes/${quizId}/questions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(questionData)
            });
            
            if (!questionResponse.ok) {
                throw new Error(`Erreur lors de l'ajout de la question: ${questionResponse.status}`);
            }
            
            questionsAdded++;
        }
        
        showNotification(`✅ Quiz "${title}" créé avec ${questionsAdded} question(s)!`, 'success');
        closeQuizModal();
        
        await loadCourses();
        await loadQuizzes();
        await loadStats();
        showQuizzesSection();
        
    } catch (error) {
        console.error('❌ Erreur:', error);
        showNotification('Erreur: ' + error.message, 'error');
    } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Enregistrer le Quiz';
    }
}

function previewQuiz(quizId) {
    window.location.href = `/quiz/preview/${quizId}`;
}

// ========== DÉCONNEXION ==========
function logout() {
    if (confirm('Do you want to logout?')) {
        sessionStorage.clear();
        window.location.href = '/logout';
    }
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Dashboard initialisé');
    initUserProfile();
    loadCourses();
    loadStats();
    setupFileUpload();
    setupEditFileUpload();
    
    const myCoursesLink = document.getElementById('myCoursesLink');
    const myQuizzesLink = document.getElementById('myQuizzesLink');
    const myRatingsLink = document.getElementById('myRatingsLink');
    
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
    
    if (myRatingsLink) {
        myRatingsLink.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            myRatingsLink.classList.add('active');
            showRatingsSection();
        });
    }
    
    document.getElementById('openNewCourseBtn')?.addEventListener('click', openAddCourseModal);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeAddCourseModal);
    document.getElementById('cancelModalBtn')?.addEventListener('click', closeAddCourseModal);
    document.getElementById('courseForm')?.addEventListener('submit', submitCourseForm);
    document.getElementById('editCourseForm')?.addEventListener('submit', submitEditCourseForm);
    
    const quizForm = document.getElementById('createQuizForm');
    if (quizForm) {
        quizForm.addEventListener('submit', saveQuizModal);
    }
    
    window.addEventListener('click', (event) => {
        if (event.target === document.getElementById('addCourseModal')) closeAddCourseModal();
        if (event.target === document.getElementById('editCourseModal')) closeEditCourseModal();
        if (event.target === document.getElementById('deleteModal')) closeDeleteModal();
        if (event.target === document.getElementById('deleteQuizModal')) closeDeleteQuizModal();
        if (event.target === document.getElementById('createQuizModal')) closeQuizModal();
    });
});