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
        ['username', 'email', 'password', 'confirmPassword'].forEach(id => hideError(id));
        
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
        }
        
        // Valider confirmation du mot de passe
        const confirmPassword = document.getElementById('confirmPassword').value;
        if (password !== confirmPassword) {
            showError('confirmPassword', 'Les mots de passe ne correspondent pas');
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
                name: document.getElementById('username').value.trim(),  // Note: 'name' pas 'username'
                email: document.getElementById('email').value.trim(),
                password: document.getElementById('password').value
            };
            
            // Afficher un indicateur de chargement
            const submitBtn = document.querySelector('.signup-btn');
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'INSCRIPTION EN COURS...';
            submitBtn.disabled = true;
            
            try {
                // 🔥 APPEL API RÉEL VERS VOTRE BACKEND SPRING
                const response = await fetch('http://localhost:8082/api/inscription', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(formData)
                });
                
                const responseText = await response.text();
                
                if (response.ok) {
                    // Succès
                    console.log('✅ Inscription réussie:', responseText);
                    showSuccessMessage('Inscription réussie ! Redirection vers la page de connexion...');
                    
                    // Rediriger après 2 secondes
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 2000);
                } else {
                    // Erreur (email déjà utilisé, etc.)
                    throw new Error(responseText || 'Erreur lors de l\'inscription');
                }
                
            } catch (error) {
                // Erreur
                console.error('❌ Erreur:', error);
                alert('Erreur: ' + error.message);
            } finally {
                // Restaurer le bouton
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        }
    });
    
    // Afficher un message de succès
    function showSuccessMessage(message) {
        // Supprimer l'ancien message s'il existe
        const oldSuccess = document.querySelector('.success-message');
        if (oldSuccess) oldSuccess.remove();
        
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;
        
        const signupBox = document.querySelector('.signup-box');
        signupBox.appendChild(successDiv);
    }
    
    // Validation en temps réel
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    
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
            // Vérifier aussi la confirmation si elle est déjà remplie
            if (confirmPasswordInput && confirmPasswordInput.value) {
                if (this.value === confirmPasswordInput.value) {
                    hideError('confirmPassword');
                }
            }
        });
    }
    
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', function() {
            const password = document.getElementById('password').value;
            if (this.value === password && password.length >= 6) {
                hideError('confirmPassword');
            }
        });
    }
});