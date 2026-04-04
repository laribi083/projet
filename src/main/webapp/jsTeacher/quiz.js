let questionCount = 0;
let questions = [];

function addQuestion() {
    questionCount++;
    const container = document.getElementById('questionsContainer');
    const questionDiv = document.createElement('div');
    questionDiv.className = 'question-card';
    questionDiv.id = `question-${questionCount}`;
    questionDiv.innerHTML = `
        <div class="question-header">
            <span class="question-number">Question ${questionCount}</span>
            <button type="button" class="btn-remove-question" onclick="removeQuestion(${questionCount})">
                <i class="fas fa-trash"></i>
            </button>
        </div>
        <div class="form-group">
            <input type="text" class="question-text" placeholder="Enter your question" required>
        </div>
        <div class="options-container">
            <div class="option-item">
                <input type="radio" name="correct-${questionCount}" value="0">
                <input type="text" class="option-text" placeholder="Option 1" required>
            </div>
            <div class="option-item">
                <input type="radio" name="correct-${questionCount}" value="1">
                <input type="text" class="option-text" placeholder="Option 2" required>
            </div>
            <div class="option-item">
                <input type="radio" name="correct-${questionCount}" value="2">
                <input type="text" class="option-text" placeholder="Option 3" required>
            </div>
            <div class="option-item">
                <input type="radio" name="correct-${questionCount}" value="3">
                <input type="text" class="option-text" placeholder="Option 4" required>
            </div>
        </div>
    `;
    container.appendChild(questionDiv);
}

function removeQuestion(id) {
    const element = document.getElementById(`question-${id}`);
    if (element) element.remove();
}

function collectQuestions() {
    const questionCards = document.querySelectorAll('.question-card');
    const questions = [];
    
    questionCards.forEach((card, index) => {
        const questionText = card.querySelector('.question-text').value;
        const options = Array.from(card.querySelectorAll('.option-text')).map(opt => opt.value);
        const correctAnswer = card.querySelector(`input[type="radio"]:checked`);
        
        if (questionText && options.every(opt => opt)) {
            questions.push({
                text: questionText,
                options: options,
                correctAnswer: correctAnswer ? parseInt(correctAnswer.value) : 0,
                points: 1
            });
        }
    });
    
    return questions;
}

document.getElementById('quizForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const title = document.getElementById('quizTitle').value;
    const description = document.getElementById('quizDescription').value;
    const durationMinutes = document.getElementById('durationMinutes').value;
    const passingScore = document.getElementById('passingScore').value;
    const courseId = document.getElementById('courseId').value;
    const courseModule = document.getElementById('courseModule').value;
    const courseNiveau = document.getElementById('courseNiveau').value;
    const questions = collectQuestions();
    
    if (!title || questions.length === 0) {
        alert('Please fill in quiz title and at least one question');
        return;
    }
    
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('courseId', courseId);
    formData.append('courseModule', courseModule);
    formData.append('courseNiveau', courseNiveau);
    formData.append('durationMinutes', durationMinutes);
    formData.append('passingScore', passingScore);
    formData.append('questions', JSON.stringify(questions));
    
    try {
        const response = await fetch('/teacher/api/quizzes', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        if (result.success) {
            window.location.href = `/teacher/course/${courseId}/quizzes`;
        } else {
            alert(result.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error creating quiz');
    }
});

function cancelQuiz() {
    window.history.back();
}

// Add first question by default
addQuestion();