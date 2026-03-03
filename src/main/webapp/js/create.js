// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', function() {
    
    const signupForm = document.getElementById('signupForm');
    
    // Créer les conteneurs pour les messages d'erreur
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.id = `${input.id}-error`;
        input.parentNode.appendChild(errorDiv);
    });
    
    // Fonction pour afficher une erreur
    function showError(inputId, message) {
        const input = document.getElementById(inputId);
        const errorDiv = document.getElementById(`${inputId}-error`);
        
        input.classList.add('error');
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
    
    // Fonction pour cacher une erreur
    function hideError(inputId) {
        const input = document.getElementById(inputId);
        const errorDiv = document.getElementById(`${inputId}-error`);
        
        input.classList.remove('error');
        errorDiv.style.display = 'none';
    }
    
    // Fonction de validation
    function validateForm() {
        let isValid = true;
        
        // Réinitialiser les erreurs
        ['username', 'email', 'password'].forEach(id => hideError(id));
        
        // Valider username
        const username = document.getElementById('username').value.trim();
        if (username.length < 3) {
            showError('username', 'Le nom d\'utilisateur doit contenir au moins 3 caractères');
            isValid = false;
        } else if (username.length > 50) {
            showError('username', 'Le nom d\'utilisateur ne peut pas dépasser 50 caractères');
            isValid = false;
        }
        
        // Valider email
        const email = document.getElementById('email').value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showError('email', 'Veuillez entrer une adresse email valide');
            isValid = false;
        }
        
        // Valider password
        const password = document.getElementById('password').value;
        if (password.length < 6) {
            showError('password', 'Le mot de passe doit contenir au moins 6 caractères');
            isValid = false;
        } else if (!/[A-Z]/.test(password)) {
            showError('password', 'Le mot de passe doit contenir au moins une majuscule');
            isValid = false;
        } else if (!/[0-9]/.test(password)) {
            showError('password', 'Le mot de passe doit contenir au moins un chiffre');
            isValid = false;
        }
        
        return isValid;
    }
    
    // Gérer la soumission du formulaire
    signupForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        if (validateForm()) {
            // Récupérer les données du formulaire
            const formData = {
                username: document.getElementById('username').value.trim(),
                email: document.getElementById('email').value.trim(),
                password: document.getElementById('password').value
            };
            
            // Afficher un indicateur de chargement
            const submitBtn = document.querySelector('.signup-btn');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'INSCRIPTION EN COURS...';
            submitBtn.disabled = true;
            
            try {
                // Simuler un appel API
                await simulateApiCall(formData);
                
                // Succès
                showSuccessMessage();
                
                // Rediriger après 2 secondes
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 2000);
                
            } catch (error) {
                // Erreur
                alert('Erreur lors de l\'inscription: ' + error.message);
            } finally {
                // Restaurer le bouton
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        }
    });
    
    // Simulation d'appel API
    function simulateApiCall(data) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                console.log('Données envoyées:', data);
                
                // Simulation: email déjà utilisé
                if (data.email === 'test@test.com') {
                    reject(new Error('Cet email est déjà utilisé'));
                } else {
                    resolve({ success: true });
                }
            }, 1500);
        });
    }
    
    // Afficher un message de succès
    function showSuccessMessage() {
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = 'Inscription réussie ! Redirection vers la page de connexion...';
        
        const signupBox = document.querySelector('.signup-box');
        signupBox.appendChild(successDiv);
    }
    
    // Validation en temps réel
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    
    if (usernameInput) {
        usernameInput.addEventListener('input', function() {
            if (this.value.length >= 3) {
                hideError('username');
            }
        });
    }
    
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (emailRegex.test(this.value)) {
                hideError('email');
            }
        });
    }
    
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            if (this.value.length >= 6) {
                hideError('password');
            }
        });
    }
});