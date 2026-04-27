/**
 * ratings.js - Gestion des notes étudiant
 */

let allRatings = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Page des notes chargée');
    loadMyRatings();
});

function loadMyRatings() {
    fetch('/api/ratings/my-ratings')
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                allRatings = data.ratings || [];
                updateStats();
                displayRatings(allRatings);
            } else {
                showEmptyState();
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            showErrorState(error.message);
        });
}

function updateStats() {
    const total = allRatings.length;
    document.getElementById('totalRatings').textContent = total;
    
    if (total > 0) {
        const sum = allRatings.reduce((acc, r) => acc + r.ratingValue, 0);
        const avg = (sum / total).toFixed(1);
        document.getElementById('avgRating').textContent = avg;
        document.getElementById('avgStars').innerHTML = generateStarsStatic(avg);
    } else {
        document.getElementById('avgRating').textContent = '0';
        document.getElementById('avgStars').innerHTML = '';
    }
}

function generateStarsStatic(rating) {
    const fullStars = Math.floor(rating);
    const hasHalf = rating % 1 >= 0.5;
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= fullStars) {
            stars += '<i class="fas fa-star"></i>';
        } else if (hasHalf && i === fullStars + 1) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        } else {
            stars += '<i class="far fa-star"></i>';
        }
    }
    return stars;
}

function displayRatings(ratings) {
    const container = document.getElementById('ratingsGrid');
    if (!ratings || ratings.length === 0) {
        showEmptyState();
        return;
    }

    container.innerHTML = '';
    ratings.forEach(rating => {
        const card = document.createElement('div');
        card.className = 'rating-card';
        card.innerHTML = `
            <h3 class="course-title">${escapeHtml(rating.courseTitle)}</h3>
            <div class="stars-display">${generateStars(rating.ratingValue)}</div>
            <div class="rating-value">${rating.ratingValue}/5</div>
            ${rating.comment ? `<div class="comment"><i class="fas fa-quote-left"></i> ${escapeHtml(rating.comment)}</div>` : ''}
            <div class="rating-date">${new Date(rating.createdAt).toLocaleDateString('fr-FR')}</div>
        `;
        container.appendChild(card);
    });
}

function generateStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<i class="fas fa-star filled"></i>';
        } else {
            stars += '<i class="far fa-star empty"></i>';
        }
    }
    return stars;
}

function showEmptyState() {
    const container = document.getElementById('ratingsGrid');
    container.innerHTML = `
        <div class="empty-state" style="grid-column: 1/-1;">
            <i class="fas fa-star"></i>
            <h3>No ratings yet</h3>
            <p>You haven't rated any courses yet.</p>
            <a href="/receive-courses" class="btn-primary">Browse Courses</a>
        </div>
    `;
}

function showErrorState(message) {
    const container = document.getElementById('ratingsGrid');
    container.innerHTML = `
        <div class="empty-state" style="grid-column: 1/-1;">
            <i class="fas fa-exclamation-triangle"></i>
            <h3>Error loading ratings</h3>
            <p>${escapeHtml(message)}</p>
            <button onclick="loadMyRatings()" class="btn-primary">Retry</button>
        </div>
    `;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}