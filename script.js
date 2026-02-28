// Éléments du DOM
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const switchToRegister = document.getElementById('switchToRegister');
const switchToLogin = document.getElementById('switchToLogin');
const successMessage = document.getElementById('successMessage');

// Gestionnaire d'événements pour les boutons de basculement
loginBtn.addEventListener('click', () => showForm('login'));
registerBtn.addEventListener('click', () => showForm('register'));
switchToRegister.addEventListener('click', (e) => {
    e.preventDefault();
    showForm('register');
});
switchToLogin.addEventListener('click', (e) => {
    e.preventDefault();
    showForm('login');
});

// Fonction pour basculer entre les formulaires
function showForm(formName) {
    if (formName === 'login') {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        loginBtn.classList.add('active');
        registerBtn.classList.remove('active');
    } else {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        loginBtn.classList.remove('active');
        registerBtn.classList.add('active');
    }
    // Effacer les erreurs lors du changement de formulaire
    clearAllErrors();
}

// Fonction pour basculer la visibilité du mot de passe
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
    } else {
        input.type = 'password';
    }
}

// Fonction pour afficher une erreur
function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    const inputElement = errorElement.previousElementSibling;
    
    if (inputElement && (inputElement.tagName === 'INPUT' || inputElement.classList.contains('password-input'))) {
        const input = inputElement.tagName === 'INPUT' ? inputElement : inputElement.querySelector('input');
        if (input) input.classList.add('error');
    }
    
    errorElement.textContent = message;
}

// Fonction pour effacer une erreur
function clearError(elementId) {
    const errorElement = document.getElementById(elementId);
    const inputElement = errorElement.previousElementSibling;
    
    if (inputElement && (inputElement.tagName === 'INPUT' || inputElement.classList.contains('password-input'))) {
        const input = inputElement.tagName === 'INPUT' ? inputElement : inputElement.querySelector('input');
        if (input) input.classList.remove('error');
    }
    
    errorElement.textContent = '';
}

// Fonction pour effacer toutes les erreurs
function clearAllErrors() {
    const errorMessages = document.querySelectorAll('.error-message');
    errorMessages.forEach(msg => msg.textContent = '');
    
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => input.classList.remove('error'));
}

// Validation de l'email
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Validation du mot de passe
function validatePassword(password) {
    return password.length >= 8;
}

// Gestionnaire du formulaire de connexion
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors();
    
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    let hasErrors = false;
    
    // Validation de l'email
    if (!email) {
        showError('loginEmailError', 'L\'email est requis');
        hasErrors = true;
    } else if (!validateEmail(email)) {
        showError('loginEmailError', 'Veuillez entrer un email valide');
        hasErrors = true;
    }
    
    // Validation du mot de passe
    if (!password) {
        showError('loginPasswordError', 'Le mot de passe est requis');
        hasErrors = true;
    }
    
    if (!hasErrors) {
        // Simulation de la connexion
        const submitBtn = loginForm.querySelector('.submit-btn');
        submitBtn.classList.add('loading');
        submitBtn.textContent = 'Connexion en cours...';
        
        // Simuler une requête API
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        submitBtn.classList.remove('loading');
        submitBtn.textContent = 'Se connecter';
        
        // Afficher le message de succès
        showSuccess('Connexion réussie ! Bienvenue.');
    }
});

// Gestionnaire du formulaire d'inscription
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAllErrors();
    
    const name = document.getElementById('registerName').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;
    const terms = document.getElementById('acceptTerms').checked;
    let hasErrors = false;
    
    // Validation du nom
    if (!name) {
        showError('registerNameError', 'Le nom est requis');
        hasErrors = true;
    } else if (name.length < 2) {
        showError('registerNameError', 'Le nom doit contenir au moins 2 caractères');
        hasErrors = true;
    }
    
    // Validation de l'email
    if (!email) {
        showError('registerEmailError', 'L\'email est requis');
        hasErrors = true;
    } else if (!validateEmail(email)) {
        showError('registerEmailError', 'Veuillez entrer un email valide');
        hasErrors = true;
    }
    
    // Validation du mot de passe
    if (!password) {
        showError('registerPasswordError', 'Le mot de passe est requis');
        hasErrors = true;
    } else if (!validatePassword(password)) {
        showError('registerPasswordError', 'Le mot de passe doit contenir au moins 8 caractères');
        hasErrors = true;
    }
    
    // Validation de la confirmation du mot de passe
    if (!confirmPassword) {
        showError('registerConfirmPasswordError', 'Veuillez confirmer votre mot de passe');
        hasErrors = true;
    } else if (password !== confirmPassword) {
        showError('registerConfirmPasswordError', 'Les mots de passe ne correspondent pas');
        hasErrors = true;
    }
    
    // Validation des conditions
    if (!terms) {
        showError('acceptTermsError', 'Vous devez accepter les conditions d\'utilisation');
        hasErrors = true;
    }
    
    if (!hasErrors) {
        // Simulation de l'inscription
        const submitBtn = registerForm.querySelector('.submit-btn');
        submitBtn.classList.add('loading');
        submitBtn.textContent = 'Création du compte...';
        
        // Simuler une requête API
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        submitBtn.classList.remove('loading');
        submitBtn.textContent = 'Créer mon compte';
        
        // Afficher le message de succès
        showSuccess('Compte créé avec succès ! Vous pouvez maintenant vous connecter.');
        
        // Réinitialiser le formulaire
        registerForm.reset();
        
        // Basculer vers le formulaire de connexion après 2 secondes
        setTimeout(() => {
            showForm('login');
        }, 3000);
    }
});

// Fonction pour afficher le message de succès
function showSuccess(message) {
    loginForm.classList.add('hidden');
    registerForm.classList.add('hidden');
    successMessage.classList.remove('hidden');
    document.getElementById('successText').textContent = message;
    
    // Animation de succès
    const successIcon = successMessage.querySelector('.success-icon');
    successIcon.style.animation = 'none';
    successIcon.offsetHeight; // Trigger reflow
    successIcon.style.animation = 'popIn 0.5s ease-out';
}

// Effet de focus sur les champs
const inputs = document.querySelectorAll('input');
inputs.forEach(input => {
    input.addEventListener('focus', () => {
        input.classList.remove('error');
        const errorElement = input.parentElement.nextElementSibling;
        if (errorElement && errorElement.classList.contains('error-message')) {
            errorElement.textContent = '';
        }
    });
});

// Gestion de la soumission avec la touche Entrée
document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        const activeForm = !loginForm.classList.contains('hidden') ? loginForm : registerForm;
        if (!activeForm.classList.contains('hidden')) {
            const submitBtn = activeForm.querySelector('.submit-btn');
            if (!submitBtn.classList.contains('loading')) {
                submitBtn.click();
            }
        }
    }
});
