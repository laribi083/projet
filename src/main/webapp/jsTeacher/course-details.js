// course-details.js

function createQuiz(courseId, courseModule, courseNiveau) {
    console.log('📝 Création quiz pour cours:', { courseId, courseModule, courseNiveau });
    window.location.href = `/teacher/create-quiz?courseId=${courseId}&courseModule=${encodeURIComponent(courseModule)}&courseNiveau=${encodeURIComponent(courseNiveau)}`;
}

function previewQuiz(quizId) {
    console.log('👁 Aperçu du quiz:', quizId);
    window.location.href = `/quiz/preview/${quizId}`;
}

async function deleteQuiz(quizId) {
    if (!confirm('Voulez-vous vraiment supprimer ce quiz ? Cette action est irréversible.')) {
        return;
    }
    
    try {
        console.log('🗑 Suppression du quiz:', quizId);
        
        const response = await fetch(`/quiz/api/delete/${quizId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('✅ Quiz supprimé avec succès !');
            location.reload();
        } else {
            alert('❌ Erreur: ' + data.message);
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('❌ Erreur lors de la suppression: ' + error.message);
    }
}

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    console.log('📄 Page détails du cours chargée');
});