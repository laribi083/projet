/**
 * welcom.js - JavaScript pour la page d'accueil
 * Gère le modal de connexion/inscription, le chatbot, les animations
 */

// ========== VARIABLES ==========
const modal = document.getElementById('authModal');
const modalTitle = document.getElementById('modalTitle');
const modalActionBtn = document.getElementById('modalActionBtn');
const modalSwitchTextSpan = document.getElementById('modalSwitchText');
const emailInput = document.getElementById('emailInput');
const passwordInput = document.getElementById('passwordInput');
const errorDiv = document.getElementById('modalErrorMessage');
let currentMode = 'login'; // 'login' ou 'signup'

// ========== FONCTIONS UTILITAIRES ==========
function showToast(message) {
    const toast = document.getElementById('toastMsg');
    toast.innerText = message;
    toast.style.opacity = '1';
    setTimeout(() => {
        toast.style.opacity = '0';
    }, 3000);
}

function showError(message) {
    errorDiv.innerText = message;
    errorDiv.style.display = 'block';
    setTimeout(() => {
        errorDiv.style.display = 'none';
    }, 3000);
}

function closeModal() {
    modal.style.display = 'none';
}

function openModal(mode) {
    currentMode = mode;
    errorDiv.style.display = 'none';
    emailInput.value = '';
    passwordInput.value = '';
    
    if (mode === 'login') {
        modalTitle.innerText = 'Connexion';
        modalActionBtn.innerText = 'Se connecter';
        modalSwitchTextSpan.innerHTML = `Pas encore de compte ? <a href="#" id="switchToSignup">S'inscrire</a>`;
    } else {
        modalTitle.innerText = 'Inscription';
        modalActionBtn.innerText = "S'inscrire";
        modalSwitchTextSpan.innerHTML = `Déjà un compte ? <a href="#" id="switchToLogin">Se connecter</a>`;
    }
    
    // Réattacher l'événement au nouveau lien
    const switchLink = modalSwitchTextSpan.querySelector('a');
    if (switchLink) {
        switchLink.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentMode === 'login') {
                openModal('signup');
            } else {
                openModal('login');
            }
        });
    }
    
    modal.style.display = 'flex';
}

// ========== LOGIN (Connexion) ==========
async function performLogin(email, password) {
    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                email: email,
                password: password
            })
        });
        
        if (response.redirected) {
            window.location.href = response.url;
        } else {
            const text = await response.text();
            if (text.includes("error")) {
                showError("Email or password incorrect");
            } else {
                showError("Connection error");
            }
        }
    } catch (error) {
        console.error('Error:', error);
        showError("Server connection error");
    }
}

// ========== SIGNUP (Inscription) ==========
async function performSignup(name, email, password) {
    try {
        const response = await fetch('/api/inscription', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: name,
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast("✅ Inscription réussie ! Vous pouvez maintenant vous connecter.");
            closeModal();
            setTimeout(() => {
                openModal('login');
            }, 1000);
        } else {
            showError(data.message || "Error during signup");
        }
    } catch (error) {
        console.error('Error:', error);
        showError("Server connection error");
    }
}

// ========== GESTION DU CLIC SUR LE BOUTON PRINCIPAL ==========
modalActionBtn.addEventListener('click', async () => {
    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();
    
    if (!email || !password) {
        showError("Please fill in all fields");
        return;
    }
    
    if (currentMode === 'login') {
        await performLogin(email, password);
    } else {
        const name = prompt("Enter your full name:", "");
        if (!name || name.trim() === "") {
            showError("Name is required for signup");
            return;
        }
        await performSignup(name.trim(), email, password);
    }
});

// ========== INITIALISATION DES BOUTONS ==========
// Les boutons Login et Sign Up redirigent déjà vers les pages,
// donc pas besoin de les gérer ici pour l'ouverture du modal

// Chatbot
const chatBtn = document.getElementById('chatBotBtn');
if (chatBtn) {
    chatBtn.addEventListener('click', () => {
        showToast("🤖 AI Assistant: How can I help you today? Explain a concept or ask for a recommendation.");
    });
}

// Animation des cartes
const cards = document.querySelectorAll('.card-feature, .step-card, .benefit-item');
cards.forEach(card => {
    card.addEventListener('click', () => {
        const text = card.querySelector('h3, h4')?.innerText || 'feature';
        showToast(`✨ ${text} — available on your personalized dashboard.`);
    });
});

// Smooth scroll pour les ancres
document.querySelectorAll('.nav-links a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        const targetId = this.getAttribute('href');
        if (targetId && targetId !== '#') {
            const target = document.querySelector(targetId);
            if (target) {
                e.preventDefault();
                target.scrollIntoView({ behavior: 'smooth' });
            }
        }
    });
});

// Fermer le modal
document.getElementById('closeModalBtn').addEventListener('click', closeModal);
window.addEventListener('click', (e) => {
    if (e.target === modal) closeModal();
});

// Message de bienvenue
setTimeout(() => {
    showToast("Welcome to Brainlearning! Adaptive AI awaits you 🚀");
}, 1000);