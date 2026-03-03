// Script pour la page de mot de passe oublié
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('forgotForm');
    
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            
            console.log('Email pour récupération:', email);
            
            // Ici vous enverrez la demande au backend
            alert('Un email de récupération a été envoyé !');
        });
    }
});
