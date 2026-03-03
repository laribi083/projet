(function() {
    const form = document.getElementById('signupForm');

    // helper pour afficher les erreurs
    function setError(elementId, message, inputId = null) {
        const errorDiv = document.getElementById(elementId);
        errorDiv.textContent = message;

        if (inputId) {
            const input = document.getElementById(inputId);
            if (message) {
                input.classList.add('error');
            } else {
                input.classList.remove('error');
            }
        }
    }

    // validation email
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // basique mais efficace
        return re.test(String(email).toLowerCase());
    }

    // validation téléphone (français / international simple)
    function validatePhone(phone) {
        // accepte chiffres, espaces, +, -, . et min 8 chiffres (flexible)
        const digits = phone.replace(/\D/g, '');
        return digits.length >= 8 && digits.length <= 15; // fourchette classique
    }

    form.addEventListener('submit', function(e) {
        e.preventDefault();  // on bloque l'envoi réel pour la démo

        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const confirm = document.getElementById('confirmPassword');
        const phone = document.getElementById('phone');

        // reset erreurs
        setError('emailError', '', 'email');
        setError('passwordError', '', 'password');
        setError('confirmError', '', 'confirmPassword');
        setError('phoneError', '', 'phone');

        let isValid = true;

        // 1. Email
        if (!email.value.trim()) {
            setError('emailError', '✱ L\'email est requis', 'email');
            isValid = false;
        } else if (!validateEmail(email.value.trim())) {
            setError('emailError', '✱ Format d\'email invalide (ex: nom@domaine.fr)', 'email');
            isValid = false;
        }

        // 2. Mot de passe
        if (!password.value) {
            setError('passwordError', '✱ Mot de passe requis', 'password');
            isValid = false;
        } else if (password.value.length < 6) {
            setError('passwordError', '✱ Au moins 6 caractères', 'password');
            isValid = false;
        }

        // 3. Confirmation
        if (!confirm.value) {
            setError('confirmError', '✱ Confirmation requise', 'confirmPassword');
            isValid = false;
        } else if (password.value !== confirm.value) {
            setError('confirmError', '✱ Les mots de passe ne correspondent pas', 'confirmPassword');
            isValid = false;
        }

        // 4. Téléphone (optionnel mais avec validation s'il est rempli)
        if (phone.value.trim() !== '' && !validatePhone(phone.value.trim())) {
            setError('phoneError', '✱ Numéro semble invalide (8 à 15 chiffres)', 'phone');
            isValid = false;
        } else if (phone.value.trim() === '') {
           
        }

        if (isValid) {
           
            alert('✅ Inscription réussie (validation JS uniquement — démo)');
             
        } else {
           
            form.classList.add('shake');
            setTimeout(() => form.classList.remove('shake'), 400);
        }
    })});