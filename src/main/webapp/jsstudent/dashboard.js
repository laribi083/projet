/**
 * dashboard.js - JavaScript pour le dashboard étudiant
 */

// ========== VARIABLES GLOBALES ==========
let recentCourses = [];

// ========== CHARGER LES COURS RÉCEMMENT TÉLÉCHARGÉS ==========
async function loadRecentDownloads() {
    const container = document.getElementById('recentCoursesContainer');
    if (!container) return;
    
    container.innerHTML = `
        <div class="loading-spinner">
            <i class="fas fa-spinner fa-spin"></i>
            <p>Loading your recent downloads...</p>
        </div>
    `;
    
    try {
        const response = await fetch('/api/recent-downloads');
        if (!response.ok) throw new Error('Failed to fetch recent downloads');
        
        const courses = await response.json();
        recentCourses = courses;
        displayRecentCourses(courses);
        updateStats();
    } catch (error) {
        console.error('Error loading recent downloads:', error);
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-download"></i>
                <p>No courses downloaded yet</p>
                <p class="empty-hint">Download a course to see it here!</p>
            </div>
        `;
    }
}

function displayRecentCourses(courses) {
    const container = document.getElementById('recentCoursesContainer');
    
    if (!container) return;
    
    if (!courses || courses.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-download"></i>
                <p>No courses downloaded yet</p>
                <p class="empty-hint">Download a course to see it here!</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = courses.map(course => `
        <div class="course-card" onclick="viewCourse(${course.id})">
            <div class="course-image">
                <i class="fas ${getCourseIcon(course.module)}"></i>
                <span class="course-badge">Downloaded</span>
            </div>
            <div class="course-content">
                <h3 class="course-title">${escapeHtml(course.title)}</h3>
                <p class="course-description">${escapeHtml(course.description ? course.description.substring(0, 100) : '')}</p>
                <div class="course-meta">
                    <div class="course-info">
                        <span><i class="fas fa-chalkboard-user"></i> ${escapeHtml(course.teacherName || 'Professor')}</span>
                        <span><i class="fas fa-calendar"></i> ${formatDate(course.lastDownloadedAt)}</span>
                    </div>
                    <div class="course-stats">
                        <span><i class="fas fa-download"></i> Downloaded ${course.downloadCount || 1} time(s)</span>
                        <span><i class="fas fa-file-alt"></i> ${course.totalQuizzes || 0} quizzes</span>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

function updateStats() {
    const activeCount = recentCourses.length;
    document.getElementById('coursActifs').textContent = activeCount;
    document.getElementById('coursCompletes').textContent = recentCourses.length;
    
    const totalQuizzes = recentCourses.reduce((sum, course) => sum + (course.totalQuizzes || 0), 0);
    document.getElementById('quizDisponibles').textContent = totalQuizzes;
}

function getCourseIcon(module) {
    const icons = {
        'Algebra 01': 'fa-calculator', 'Algebra 02': 'fa-calculator',
        'Analysis 01': 'fa-chart-line', 'Analysis 02': 'fa-chart-line',
        'DataBases and SQL': 'fa-database', 'web development 01': 'fa-laptop-code',
        'Git and GitHub': 'fa-code-branch'
    };
    return icons[module] || 'fa-graduation-cap';
}

function formatDate(dateString) {
    if (!dateString) return 'Recently';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    } catch { return 'Recently'; }
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

function openChatbot() { 
    alert('AI Assistant coming soon!'); 
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('📚 Dashboard Student initialisé');
    loadRecentDownloads();
    
    // Navigation active
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
    navItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath === href) {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        }
    });
});