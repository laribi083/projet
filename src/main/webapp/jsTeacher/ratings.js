/**
 * ratings.js - Gestion des notes enseignant
 */

let allRatings = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Page des notes enseignant chargée');
    initUserProfile();
    loadTeacherRatings();
});

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

function loadTeacherRatings() {
    fetch('/api/ratings/teacher/all')
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                allRatings = data.ratings || [];
                displayCourseGroups(groupByCourse(allRatings));
                updateStats();
            } else {
                showEmptyState();
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            showErrorState(error.message);
        });
}

function groupByCourse(ratings) {
    const groups = new Map();
    ratings.forEach(rating => {
        if (!groups.has(rating.courseId)) {
            groups.set(rating.courseId, {
                courseId: rating.courseId,
                courseTitle: rating.courseTitle,
                ratings: []
            });
        }
        groups.get(rating.courseId).ratings.push(rating);
    });
    return Array.from(groups.values());
}

function displayCourseGroups(courseGroups) {
    const container = document.getElementById('ratingsContainer');
    if (!courseGroups || courseGroups.length === 0) {
        showEmptyState();
        return;
    }

    container.innerHTML = '';
    courseGroups.forEach(group => {
        const avg = group.ratings.reduce((sum, r) => sum + r.ratingValue, 0) / group.ratings.length;
        const groupDiv = document.createElement('div');
        groupDiv.className = 'course-group';
        groupDiv.innerHTML = `
            <div class="course-header">
                <div class="course-title">${escapeHtml(group.courseTitle)}</div>
                <div class="course-stats">
                    <span><i class="fas fa-star"></i> ${avg.toFixed(1)}/5</span>
                    <span><i class="fas fa-users"></i> ${group.ratings.length} avis</span>
                </div>
            </div>
            <table class="ratings-table">
                <thead>
                    <tr><th>Étudiant</th><th>Note</th><th>Commentaire</th><th>Date</th></tr>
                </thead>
                <tbody>
                    ${group.ratings.map(r => `
                        <tr>
                            <td>${escapeHtml(r.studentName)}</td>
                            <td>${generateStars(r.ratingValue)}</td>
                            <td class="comment-cell">${r.comment ? `<div class="comment-text">${escapeHtml(r.comment)}</div>` : '<span style="color:#9ca3af;">Aucun commentaire</span>'}</td>
                            <td>${new Date(r.createdAt).toLocaleDateString('fr-FR')}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        container.appendChild(groupDiv);
    });
}

function generateStars(rating) {
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

function updateStats() {
    const courseGroups = groupByCourse(allRatings);
    document.getElementById('totalCourses').textContent = courseGroups.length;
    document.getElementById('totalRatings').textContent = allRatings.length;
    
    if (allRatings.length > 0) {
        const sum = allRatings.reduce((acc, r) => acc + r.ratingValue, 0);
        const avg = (sum / allRatings.length).toFixed(1);
        document.getElementById('avgOverall').textContent = avg;
    }
}

function showEmptyState() {
    const container = document.getElementById('ratingsContainer');
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-star"></i>
            <h3>No ratings yet</h3>
            <p>Your courses haven't received any ratings yet.</p>
        </div>
    `;
}

function showErrorState(message) {
    const container = document.getElementById('ratingsContainer');
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-exclamation-triangle"></i>
            <h3>Error loading ratings</h3>
            <p>${escapeHtml(message)}</p>
            <button onclick="loadTeacherRatings()" class="btn-retry">Retry</button>
        </div>
    `;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function logout() {
    if (confirm('Do you want to logout?')) {
        sessionStorage.clear();
        window.location.href = '/logout';
    }
}