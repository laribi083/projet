// quiz-result.js

$(document).ready(function() {
    // Animation du score circulaire
    const percentage = window.resultData.percentage || 0;
    const angle = (percentage / 100) * 360;
    const scoreCircle = document.getElementById('scoreCircle');
    
    if (scoreCircle) {
        scoreCircle.style.background = `conic-gradient(#667eea ${angle}deg, #e0e0e0 ${angle}deg)`;
    }
});