// create.js - Version complète et corrigée

// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', function() {
    
    const signupForm = document.getElementById('signupForm');
    
    if (!signupForm) {
        console.error('Formulaire d\'inscription non trouvé!');
        return;
    }
    
    // Créer les conteneurs pour les messages d'erreur
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        // Vérifier si le conteneur d'erreur existe déjà
        let errorDiv = document.getElementById(`${input.id}-error`);
        if (!errorDiv && input.parentNode) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.id = `${input.id}-error`;
            errorDiv.style.cssText = 'color: #f44336; font-size: 12px; margin-top: 5px; display: none;';
            input.parentNode.appendChild(errorDiv);
        }
    });
    
    // Fonction pour afficher une erreur
    function showError(inputId, message) {
        const input = document.getElementById(inputId);
        const errorDiv = document.getElementById(`${inputId}-error`);
        
        if (input) {
            input.style.borderColor = '#f44336';
        }
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = 'block';
        }
    }
    
    // Fonction pour cacher une erreur
    function hideError(inputId) {
        const input = document.getElementById(inputId);
        const errorDiv = document.getElementById(`${inputId}-error`);
        
        if (input) {
            input.style.borderColor = '#ddd';
        }
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }
    
    // Fonction de validation
    function validateForm() {
        let isValid = true;
        
        // Réinitialiser les erreurs
        ['username', 'email', 'password', 'confirmPassword'].forEach(id => {
            const element = document.getElementById(id);
            if (element) hideError(id);
        });
        
        // Valider username
        const username = document.getElementById('username');
        if (username) {
            const usernameValue = username.value.trim();
            if (usernameValue.length < 3) {
                showError('username', 'Le nom d\'utilisateur doit contenir au moins 3 caractères');
                isValid = false;
            } else if (usernameValue.length > 50) {
                showError('username', 'Le nom d\'utilisateur ne peut pas dépasser 50 caractères');
                isValid = false;
            }
        }
        
        // Valider email
        const email = document.getElementById('email');
        if (email) {
            const emailValue = email.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(emailValue)) {
                showError('email', 'Veuillez entrer une adresse email valide');
                isValid = false;
            }
        }
        
        // Valider password
        const password = document.getElementById('password');
        if (password) {
            const passwordValue = password.value;
            if (passwordValue.length < 6) {
                showError('password', 'Le mot de passe doit contenir au moins 6 caractères');
                isValid = false;
            }
        }
        
        // Valider confirmation du mot de passe
        const confirmPassword = document.getElementById('confirmPassword');
        const passwordField = document.getElementById('password');
        if (confirmPassword && passwordField) {
            if (passwordField.value !== confirmPassword.value) {
                showError('confirmPassword', 'Les mots de passe ne correspondent pas');
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    // Gérer la soumission du formulaire
    signupForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        if (validateForm()) {
            // Récupérer les données du formulaire
            const name = document.getElementById('username').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            
            const formData = {
                name: name,
                email: email,
                password: password
            };
            
            console.log('📤 Envoi des données:', { name, email });
            
            // Afficher un indicateur de chargement
            const submitBtn = document.querySelector('.signup-btn');
            const originalText = submitBtn ? submitBtn.textContent : 'S\'INSCRIRE';
            
            if (submitBtn) {
                submitBtn.textContent = 'INSCRIPTION EN COURS...';
                submitBtn.disabled = true;
            }
            
            try {
                // Appel API vers le backend
                const response = await fetch('/api/inscription', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(formData)
                });
                
                const data = await response.json();
                console.log('📥 Réponse reçue:', data);
                
                if (data.success) {
                    // Succès
                    showSuccessMessage('✅ Inscription réussie ! Redirection vers la page de connexion...');
                    
                    // Rediriger après 2 secondes
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 2000);
                } else {
                    // Erreur
                    showErrorMessage(data.message || 'Erreur lors de l\'inscription');
                }
                
            } catch (error) {
                console.error('❌ Erreur:', error);
                showErrorMessage('Erreur de connexion au serveur: ' + error.message);
            } finally {
                // Restaurer le bouton
                if (submitBtn) {
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                }
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
        successDiv.style.cssText = `
            background: #4CAF50;
            color: white;
            padding: 12px;
            border-radius: 5px;
            margin-top: 15px;
            text-align: center;
            animation: fadeIn 0.5s ease;
        `;
        
        const signupBox = document.querySelector('.signup-box');
        if (signupBox) {
            signupBox.appendChild(successDiv);
        }
    }
    
    // Afficher un message d'erreur
    function showErrorMessage(message) {
        // Supprimer l'ancien message d'erreur s'il existe
        const oldError = document.querySelector('.error-message-global');
        if (oldError) oldError.remove();
        
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message-global';
        errorDiv.textContent = message;
        errorDiv.style.cssText = `
            background: #f44336;
            color: white;
            padding: 12px;
            border-radius: 5px;
            margin-top: 15px;
            text-align: center;
            animation: fadeIn 0.5s ease;
        `;
        
        const signupBox = document.querySelector('.signup-box');
        if (signupBox) {
            signupBox.appendChild(errorDiv);
        }
        
        // Faire disparaître après 5 secondes
        setTimeout(() => {
            if (errorDiv) errorDiv.remove();
        }, 5000);
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
    
    console.log('✅ create.js chargé avec succès');
});