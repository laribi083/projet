// Script pour la page de création
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('createForm');
    
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Récupérer les données du formulaire
            const formData = new FormData(form);
            const data = Object.fromEntries(formData);
            
            console.log('Données du formulaire:', data);
            
            // Ici vous enverrez les données au backend plus tard
            alert('Formulaire soumis avec succès !');
        });
    }
});
