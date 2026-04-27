// take-quiz.js
let timeLeft = 0;
let timerInterval = null;
let userAnswers = {};

$(document).ready(function() {
    const durationMinutes = window.quizData.durationMinutes || 30;
    const totalQuestions = window.quizData.totalQuestions || 0;
    const quizId = window.quizData.quizId;
    
    if (!quizId) {
        console.error('Quiz ID not found');
        return;
    }
    
    timeLeft = durationMinutes * 60;
    updateTimerDisplay();
    startTimer();
    restoreAnswers(quizId);
});

function startTimer() {
    timerInterval = setInterval(function() {
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            alert('Temps écoulé ! Le quiz va être soumis automatiquement.');
            submitQuiz();
        } else {
            timeLeft--;
            updateTimerDisplay();
            
            if (timeLeft === 60) {
                $('#timer').addClass('warning');
                $('#timer').html('01:00 ⚠️');
            }
        }
    }, 1000);
}

function updateTimerDisplay() {
    const minutes = Math.floor(timeLeft / 60);
    const seconds = timeLeft % 60;
    $('#timer').html(`${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`);
    
    if (timeLeft <= 60) {
        $('#timer').addClass('warning');
    }
}

function selectOption(element) {
    const $option = $(element);
    const $questionCard = $option.closest('.question-card');
    const qIndex = $questionCard.index();
    const optIndex = $option.data('optindex');
    
    $questionCard.find('.option').removeClass('selected');
    $option.addClass('selected');
    $option.find('input').prop('checked', true);
    
    userAnswers[qIndex] = optIndex;
    saveAnswers();
    updateProgress();
}

function updateProgress() {
    const totalQuestions = window.quizData.totalQuestions || 0;
    const answeredCount = Object.keys(userAnswers).length;
    const percentage = totalQuestions > 0 ? (answeredCount / totalQuestions) * 100 : 0;
    $('#progressFill').css('width', percentage + '%');
}

function saveAnswers() {
    const quizId = window.quizData.quizId;
    sessionStorage.setItem('quiz_' + quizId + '_answers', JSON.stringify(userAnswers));
}

function restoreAnswers(quizId) {
    const saved = sessionStorage.getItem('quiz_' + quizId + '_answers');
    if (saved) {
        userAnswers = JSON.parse(saved);
        for (let qIndex in userAnswers) {
            const optIndex = userAnswers[qIndex];
            const $questionCard = $('.question-card').eq(parseInt(qIndex));
            if ($questionCard.length) {
                const $option = $questionCard.find('.option').eq(parseInt(optIndex));
                if ($option.length) {
                    $option.addClass('selected');
                    $option.find('input').prop('checked', true);
                }
            }
        }
        updateProgress();
    }
}

function submitQuiz() {
    if (timerInterval) {
        clearInterval(timerInterval);
    }
    
    const quizId = window.quizData.quizId;
    const totalQuestions = window.quizData.totalQuestions || 0;
    
    const answers = [];
    for (let i = 0; i < totalQuestions; i++) {
        const selected = $(`input[name="q_${i}"]:checked`).val();
        answers.push(selected ? parseInt(selected) : -1);
    }
    
    const $submitBtn = $('.btn-submit');
    $submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Submission...');
    
    $.ajax({
        url: '/quiz/submit/' + quizId,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ answers: answers }),
        success: function(response) {
            if (response.success) {
                sessionStorage.removeItem('quiz_' + quizId + '_answers');
                window.location.href = '/quiz/result/' + quizId + 
                                      '?score=' + response.score + 
                                      '&total=' + response.totalPoints + 
                                      '&percentage=' + response.percentage + 
                                      '&passed=' + response.passed;
            } else {
                alert('Erreur: ' + response.message);
                $submitBtn.prop('disabled', false).html('<i class="fas fa-check-circle"></i> Submit Quiz');
            }
        },
        error: function(xhr) {
            console.error('Error:', xhr);
            alert('Erreur lors de la soumission du quiz');
            $submitBtn.prop('disabled', false).html('<i class="fas fa-check-circle"></i> Submit Quiz');
        }
    });
}

window.addEventListener('beforeunload', function(e) {
    if (Object.keys(userAnswers).length > 0 && timerInterval) {
        e.preventDefault();
        e.returnValue = 'Vous avez des réponses non sauvegardées. Êtes-vous sûr de vouloir quitter ?';
        return e.returnValue;
    }
});