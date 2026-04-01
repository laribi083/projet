// receive-courses.js
async function loadReceiveCourses() {
    console.log('📚 Chargement de la page receive-courses...');
    
    try {
        const response = await fetch('/student/api/courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesContainer = document.getElementById('coursesContainer');
        
        console.log('   Nombre de cours reçus:', courses.length);
        
        if (!coursesContainer) return;
        
        if (courses.length === 0) {
            coursesContainer.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book-open"></i>
                    <p>Aucun cours disponible pour le moment</p>
                    <p>Revenez plus tard pour découvrir les nouveaux cours</p>
                </div>
            `;
            return;
        }
        
        // Afficher tous les cours
        coursesContainer.innerHTML = `
            <div class="courses-grid">
                ${courses.map(course => createCourseCard(course)).join('')}
            </div>
        `;
        
    } catch (error) {
        console.error('Error loading receive courses:', error);
        const coursesContainer = document.getElementById('coursesContainer');
        if (coursesContainer) {
            coursesContainer.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Erreur de chargement des cours</p>
                    <button class="btn-primary" onclick="loadReceiveCourses()">Réessayer</button>
                </div>
            `;
        }
    }
}

function createCourseCard(course) {
    return `
        <div class="course-card">
            <div class="course-image">
                📘
                <span class="course-badge">${course.status === 'ACTIVE' ? 'Disponible' : 'Brouillon'}</span>
            </div>
            <div class="course-content">
                <h3 class="course-title">${escapeHtml(course.title)}</h3>
                <p class="course-description">${escapeHtml(course.description ? course.description.substring(0, 100) : '')}</p>
                <div class="course-meta">
                    <div class="course-stats">
                        <span>👨‍🏫 ${escapeHtml(course.teacherName || 'Enseignant')}</span>
                        <span>📁 ${course.fileNames ? course.fileNames.length : 0} fichiers</span>
                        <span>📖 ${course.totalQuizzes || 0} quiz</span>
                    </div>
                    <button class="btn-view" onclick="viewCourse(${course.id})">📖 Voir le cours</button>
                </div>
            </div>
        </div>
    `;
}

function viewCourse(courseId) {
    window.location.href = `/student/course/${courseId}`;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('Receive courses page loaded');
    loadReceiveCourses();
});