// jsTeacher/quiz.js

let questionCount = 0;

$(document).ready(function() {
    addQuestion();
    
    $('#quizForm').on('submit', function(e) {
        e.preventDefault();
        saveQuiz();
    });
});

function addQuestion() {
    questionCount++;
    const questionHtml = `
        <div class="question-card" data-index="${questionCount}">
            <div class="question-header">
                <h3>Question ${questionCount}</h3>
                <button type="button" class="btn-remove" onclick="removeQuestion(this)">✕ Supprimer</button>
            </div>
            
            <div class="form-group">
                <label>Texte de la question</label>
                <input type="text" class="question-text" placeholder="Entrez votre question...">
            </div>
            
            <div class="options-container">
                <div class="option-row">
                    <span class="option-letter">A.</span>
                    <input type="text" class="option-input" placeholder="Option A">
                </div>
                <div class="option-row">
                    <span class="option-letter">B.</span>
                    <input type="text" class="option-input" placeholder="Option B">
                </div>
                <div class="option-row">
                    <span class="option-letter">C.</span>
                    <input type="text" class="option-input" placeholder="Option C">
                </div>
                <div class="option-row">
                    <span class="option-letter">D.</span>
                    <input type="text" class="option-input" placeholder="Option D">
                </div>
            </div>
            
            <div class="form-group">
                <label>Réponse correcte</label>
                <select class="correct-answer">
                    <option value="0">A</option>
                    <option value="1">B</option>
                    <option value="2">C</option>
                    <option value="3">D</option>
                </select>
            </div>
        </div>
    `;
    
    $('#questionsContainer').append(questionHtml);
}

function removeQuestion(button) {
    $(button).closest('.question-card').remove();
    updateQuestionNumbers();
}

function updateQuestionNumbers() {
    $('.question-card').each(function(index) {
        $(this).attr('data-index', index + 1);
        $(this).find('h3').text(`Question ${index + 1}`);
    });
    questionCount = $('.question-card').length;
}

function saveQuiz() {
    // Récupérer les valeurs
    const title = $('#quizTitle').val();
    const description = $('#quizDescription').val();
    const durationMinutes = $('#durationMinutes').val();
    const passingScore = $('#passingScore').val();
    const courseId = $('#courseId').val();
    const courseModule = $('#courseModule').val();
    const courseNiveau = $('#courseNiveau').val();
    
    // Validation
    if (!title) {
        alert('Veuillez entrer un titre pour le quiz');
        return;
    }
    
    if (!durationMinutes || durationMinutes < 1) {
        alert('Veuillez entrer une durée valide (minimum 1 minute)');
        return;
    }
    
    if (!passingScore || passingScore < 0 || passingScore > 100) {
        alert('Veuillez entrer un score de réussite entre 0 et 100');
        return;
    }
    
    // Collecter les questions
    const questionsData = [];
    let hasError = false;
    
    $('.question-card').each(function() {
        const questionText = $(this).find('.question-text').val();
        const options = [
            $(this).find('.option-input').eq(0).val(),
            $(this).find('.option-input').eq(1).val(),
            $(this).find('.option-input').eq(2).val(),
            $(this).find('.option-input').eq(3).val()
        ];
        const correctAnswer = parseInt($(this).find('.correct-answer').val());
        
        // Vérifier que la question est complète
        if (!questionText) {
            alert('Veuillez remplir le texte de toutes les questions');
            hasError = true;
            return false;
        }
        
        // Vérifier que toutes les options sont remplies
        for (let i = 0; i < options.length; i++) {
            if (!options[i]) {
                alert(`Veuillez remplir toutes les options de la question "${questionText.substring(0, 30)}..."`);
                hasError = true;
                return false;
            }
        }
        
        questionsData.push({
            text: questionText,
            options: options,
            correctAnswer: correctAnswer
        });
    });
    
    if (hasError) return;
    
    if (questionsData.length === 0) {
        alert('Veuillez ajouter au moins une question');
        return;
    }
    
    // Afficher le loader
    $('.btn-submit').prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Création...');
    
    // Envoyer au serveur
    $.ajax({
        url: '/quiz/api/create',
        type: 'POST',
        data: {
            title: title,
            description: description,
            courseId: courseId,
            courseModule: courseModule,
            courseNiveau: courseNiveau,
            durationMinutes: durationMinutes,
            passingScore: passingScore,
            questionsData: JSON.stringify(questionsData)
        },
        success: function(response) {
            if (response.success) {
                alert('✅ Quiz créé avec succès !');
                window.location.href = '/teacher/dashboard';
            } else {
                alert('❌ Erreur: ' + response.message);
            }
        },
        error: function(xhr) {
            console.error('Error:', xhr);
            alert('❌ Erreur lors de la création du quiz: ' + (xhr.responseJSON?.message || xhr.statusText));
        },
        complete: function() {
            $('.btn-submit').prop('disabled', false).html('Create Quiz');
        }
    });
}

function cancelQuiz() {
    if (confirm('Annuler la création du quiz ? Les données non sauvegardées seront perdues.')) {
        window.location.href = '/teacher/dashboard';
    }
}