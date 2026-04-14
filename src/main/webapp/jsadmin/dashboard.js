/**
 * dashboard.js - Admin Dashboard JavaScript
 * Version finale avec chargement des vraies statistiques, activités et cours en attente
 */

// ========== TOGGLE SIDEBAR ==========
const toggleBtn = document.getElementById('toggleBtn');
const sidebar = document.getElementById('sidebar');

if (toggleBtn) {
    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
    });
}

// ========== ANIMATION DES NOMBRES ==========
function animateNumber(elementId, targetNumber) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    let current = 0;
    const increment = Math.ceil(targetNumber / 50);
    const interval = setInterval(() => {
        current += increment;
        if (current >= targetNumber) {
            current = targetNumber;
            clearInterval(interval);
        }
        element.innerText = current;
    }, 20);
}

// ========== CHARGER LES STATISTIQUES ==========
async function loadStats() {
    try {
        const response = await fetch('/admin/api/stats');
        const data = await response.json();
        
        if (data.success) {
            animateNumber('totalUsers', data.totalUsers || 0);
            animateNumber('totalCourses', data.publishedCourses || 0);
            animateNumber('pendingCourses', data.pendingCourses || 0);
            animateNumber('validatedCourses', data.validatedCourses || 0);
            
            console.log(`📊 Stats: ${data.totalUsers} users, ${data.publishedCourses} published courses`);
        } else {
            fallbackAnimation();
        }
    } catch (error) {
        console.error('Error loading stats:', error);
        fallbackAnimation();
    }
}

// ========== CHARGER LES ACTIVITÉS RÉCENTES ==========
async function loadRecentActivities() {
    try {
        const response = await fetch('/admin/api/recent-activities');
        const activities = await response.json();
        const activityList = document.querySelector('.activity-list');
        
        if (!activityList) return;
        
        if (!activities || activities.length === 0) {
            activityList.innerHTML = `
                <div class="activity-item">
                    <div class="activity-icon gray-bg">
                        <i class="fas fa-info-circle"></i>
                    </div>
                    <div class="activity-details">
                        <p>No recent activities</p>
                        <small>Activities will appear here</small>
                    </div>
                </div>
            `;
            return;
        }
        
        activityList.innerHTML = activities.map(activity => `
            <div class="activity-item">
                <div class="activity-icon ${getIconClass(activity.type)}">
                    <i class="fas ${getIcon(activity.type)}"></i>
                </div>
                <div class="activity-details">
                    <p><b>${escapeHtml(activity.userName)}</b> ${escapeHtml(activity.message)}</p>
                    <small>${escapeHtml(activity.timeAgo)}</small>
                </div>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error loading activities:', error);
        const activityList = document.querySelector('.activity-list');
        if (activityList) {
            activityList.innerHTML = `
                <div class="activity-item">
                    <div class="activity-icon gray-bg">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="activity-details">
                        <p>Error loading activities</p>
                        <small>Please refresh the page</small>
                    </div>
                </div>
            `;
        }
    }
}

// ========== CHARGER LES COURS EN ATTENTE ==========
async function loadPendingCourses() {
    try {
        const response = await fetch('/admin/api/pending-courses');
        const courses = await response.json();
        const pendingList = document.querySelector('.pending-list');
        
        if (!pendingList) return;
        
        if (!courses || courses.length === 0) {
            pendingList.innerHTML = `
                <div class="pending-item">
                    <div class="pending-info">
                        <h4>No pending courses</h4>
                        <p>All courses have been validated</p>
                        <small>Great job!</small>
                    </div>
                </div>
            `;
            return;
        }
        
        pendingList.innerHTML = courses.map(course => `
            <div class="pending-item">
                <div class="pending-info">
                    <h4>${escapeHtml(course.title)}</h4>
                    <p>${escapeHtml(course.teacherName || 'Unknown Teacher')}</p>
                    <small>Submitted: ${formatDate(course.createdAt)}</small>
                </div>
                <button class="btn-validate" onclick="validateCourse(${course.id})">
                    <i class="fas fa-check"></i> Validate
                </button>
            </div>
        `).join('');
        
    } catch (error) {
        console.error('Error loading pending courses:', error);
        const pendingList = document.querySelector('.pending-list');
        if (pendingList) {
            pendingList.innerHTML = `
                <div class="pending-item">
                    <div class="pending-info">
                        <h4>Error loading courses</h4>
                        <p>Please refresh the page</p>
                    </div>
                </div>
            `;
        }
    }
}

// ========== VALIDER UN COURS ==========
async function validateCourse(courseId) {
    if (!confirm('Are you sure you want to validate this course?')) return;
    
    try {
        const response = await fetch(`/admin/api/course/${courseId}/status?status=VALIDATED`, {
            method: 'PUT'
        });
        const data = await response.json();
        
        if (data.success) {
            showNotification('Course validated successfully!', 'success');
            loadPendingCourses();
            loadStats();
        } else {
            showNotification('Error: ' + data.message, 'error');
        }
    } catch (error) {
        showNotification('Error validating course', 'error');
    }
}

// ========== FONCTIONS UTILITAIRES ==========
function getIconClass(type) {
    switch(type) {
        case 'COURSE_PUBLISHED': return 'blue-bg';
        case 'USER_REGISTERED': return 'green-bg';
        case 'COURSE_UPDATED': return 'purple-bg';
        case 'COURSE_DOWNLOADED': return 'orange-bg';
        default: return 'gray-bg';
    }
}

function getIcon(type) {
    switch(type) {
        case 'COURSE_PUBLISHED': return 'fa-plus-circle';
        case 'USER_REGISTERED': return 'fa-user-plus';
        case 'COURSE_UPDATED': return 'fa-edit';
        case 'COURSE_DOWNLOADED': return 'fa-download';
        default: return 'fa-info-circle';
    }
}

function formatDate(dateString) {
    if (!dateString) return 'Unknown';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-CA');
    } catch {
        return dateString;
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showNotification(message, type) {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`;
    notification.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
        background: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        border-radius: 8px;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    document.body.appendChild(notification);
    
    setTimeout(() => notification.remove(), 3000);
}

function fallbackAnimation() {
    animateNumber('totalUsers', 0);
    animateNumber('totalCourses', 0);
    animateNumber('pendingCourses', 0);
    animateNumber('validatedCourses', 0);
}

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadRecentActivities();
    loadPendingCourses();
    
    const cards = document.querySelectorAll('.stat-card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'translateY(-3px)';
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0)';
        });
    });
    
    setInterval(() => {
        loadStats();
        loadRecentActivities();
        loadPendingCourses();
    }, 30000);
    
    console.log('Admin Dashboard initialized');
});

// Add animation styles
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    .loading-placeholder {
        text-align: center;
        padding: 30px;
        color: #94a3b8;
    }
    .btn-validate {
        background: linear-gradient(135deg, #10b981, #059669);
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 0.75rem;
        font-weight: 500;
        transition: transform 0.2s;
    }
    .btn-validate:hover {
        transform: scale(1.02);
    }
    .notification {
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    }
    .notification-success { background: #10b981; color: white; padding: 12px 20px; border-radius: 8px; }
    .notification-error { background: #ef4444; color: white; padding: 12px 20px; border-radius: 8px; }
`;
document.head.appendChild(style);