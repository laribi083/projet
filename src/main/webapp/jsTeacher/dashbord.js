// ========== VARIABLES GLOBALES ==========
let courseToDelete = null;
let quizToDelete = null;
let currentView = 'courses';

// ========== NAVIGATION ==========
function showMyCourses() {
    currentView = 'courses';
    document.getElementById('pageTitle').innerHTML = '📚 My courses';
    document.getElementById('sectionTitle').innerHTML = '📖 All my courses';
    document.getElementById('coursesGrid').style.display = 'grid';
    document.getElementById('quizzesSection').style.display = 'none';
    document.querySelector('.btn-add-course').style.display = 'block';
    loadCourses();
}

function showMyQuizzes() {
    currentView = 'quizzes';
    document.getElementById('pageTitle').innerHTML = '📝 My Quizzes';
    document.getElementById('sectionTitle').innerHTML = '📝 All my quizzes';
    document.getElementById('coursesGrid').style.display = 'none';
    document.getElementById('quizzesSection').style.display = 'block';
    document.querySelector('.btn-add-course').style.display = 'none';
    loadQuizzes();
}

// ========== CHARGEMENT DES COURS ==========
async function loadCourses() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesGrid = document.getElementById('coursesGrid');
        
        if (courses.length === 0) {
            coursesGrid.innerHTML = `<div class="empty-state"><p>You have not yet created a course</p></div>`;
            return;
        }
        
        coursesGrid.innerHTML = '';
        courses.forEach(course => {
            coursesGrid.appendChild(createCourseCard(course));
        });
        
        document.getElementById('totalCourses').textContent = courses.length;
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors du chargement', 'error');
    }
}

function createCourseCard(course) {
    const div = document.createElement('div');
    div.className = 'course-card';
    div.innerHTML = `
        <div class="course-content">
            <h3>${escapeHtml(course.title)}</h3>
            <p>${escapeHtml(course.description || '').substring(0, 100)}</p>
            <div class="course-meta">
                <span>📖 ${course.totalQuizzes || 0} quiz</span>
            </div>
            <div class="course-actions">
                <button class="btn-view" onclick="viewCourse(${course.id})">📘 Voir</button>
                <button class="btn-quiz" onclick="createQuiz(${course.id}, '${escapeHtml(course.module)}', '${escapeHtml(course.niveau)}')">
                    <i class="fas fa-question-circle"></i> Create Quiz
                </button>
                <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')">🗑 Supprimer</button>
            </div>
        </div>
    `;
    return div;
}

// ========== CHARGEMENT DES QUIZZES ==========
async function loadQuizzes() {
    try {
        const response = await fetch('/teacher/api/quizzes');
        if (!response.ok) throw new Error('Failed to fetch quizzes');
        
        const quizzes = await response.json();
        const quizzesGrid = document.getElementById('quizzesGrid');
        
        if (quizzes.length === 0) {
            quizzesGrid.innerHTML = `<div class="empty-state"><p>You have not yet created any quiz</p></div>`;
            document.getElementById('totalQuizzes').textContent = '0';
            return;
        }
        
        quizzesGrid.innerHTML = '';
        quizzes.forEach(quiz => {
            quizzesGrid.appendChild(createQuizCard(quiz));
        });
        
        document.getElementById('totalQuizzes').textContent = quizzes.length;
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors du chargement des quiz', 'error');
    }
}

function createQuizCard(quiz) {
    const div = document.createElement('div');
    div.className = 'quiz-card';
    div.innerHTML = `
        <div class="quiz-content">
            <h3>${escapeHtml(quiz.title)}</h3>
            <p>${escapeHtml(quiz.description || '').substring(0, 80)}</p>
            <div class="quiz-meta">
                <span>⏱ ${quiz.durationMinutes} min</span>
                <span>❓ ${quiz.totalQuestions} questions</span>
                <span>✓ Score: ${quiz.passingScore}%</span>
            </div>
            <div class="quiz-actions">
                <button class="btn-preview" onclick="previewQuiz(${quiz.id})">👁 Aperçu</button>
                <button class="btn-delete" onclick="showDeleteQuizModal(${quiz.id}, '${escapeHtml(quiz.title)}')">🗑 Supprimer</button>
            </div>
        </div>
    `;
    return div;
}

// ========== SUPPRESSION DE COURS ==========
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
        console.log('Deleting course:', courseToDelete);
        
        const response = await fetch(`/teacher/delete-course/${courseToDelete}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Cours supprimé avec succès', 'success');
            closeDeleteModal();
            loadCourses();
            loadQuizzes();
        } else {
            throw new Error(data.message || 'Delete failed');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur: ' + error.message, 'error');
    }
}

// ========== SUPPRESSION DE QUIZ ==========
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
        console.log('Deleting quiz:', quizToDelete);
        
        const response = await fetch(`/teacher/api/delete-quiz/${quizToDelete}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Quiz supprimé avec succès', 'success');
            closeDeleteQuizModal();
            loadQuizzes();
            loadCourses();
        } else {
            throw new Error(data.message || 'Delete failed');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur: ' + error.message, 'error');
    }
}

// ========== AUTRES FONCTIONS ==========
function viewCourse(courseId) {
    window.location.href = `/teacher/course-view/${courseId}`;
}

function createQuiz(courseId, courseModule, courseNiveau) {
    window.location.href = `/teacher/create-quiz?courseId=${courseId}&courseModule=${encodeURIComponent(courseModule)}&courseNiveau=${encodeURIComponent(courseNiveau)}`;
}

function previewQuiz(quizId) {
    window.location.href = `/quiz/preview/${quizId}`;
}

async function submitCourseForm(event) {
    event.preventDefault();
    
    const title = document.getElementById('courseTitle').value.trim();
    const description = document.getElementById('courseDescription').value.trim();
    const niveau = document.getElementById('courseLevel').value;
    
    if (!title || !description || !niveau) {
        showNotification('Veuillez remplir tous les champs', 'error');
        return;
    }
    
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('niveau', niveau);
    
    try {
        const response = await fetch('/teacher/api/courses', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Cours créé avec succès!', 'success');
            closeAddCourseModal();
            loadCourses();
        } else {
            throw new Error(data.message);
        }
    } catch (error) {
        showNotification('Erreur: ' + error.message, 'error');
    }
}

function openAddCourseModal() {
    document.getElementById('addCourseModal').classList.add('active');
}

function closeAddCourseModal() {
    document.getElementById('addCourseModal').classList.remove('active');
    document.getElementById('courseForm').reset();
}

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`;
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

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    loadCourses();
    loadQuizzes();
    showMyCourses();
    
    document.getElementById('courseForm')?.addEventListener('submit', submitCourseForm);
    document.getElementById('closeModalBtn')?.addEventListener('click', closeAddCourseModal);
    document.getElementById('cancelModalBtn')?.addEventListener('click', closeAddCourseModal);
});