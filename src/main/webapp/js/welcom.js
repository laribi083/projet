  const modal = document.getElementById('authModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalActionBtn = document.getElementById('modalActionBtn');
    const modalSwitchTextSpan = document.getElementById('modalSwitchText');
    const switchLink = document.getElementById('switchToSignup');
    let currentMode = 'login'; // 'login' or 'signup'

    function openModal(mode) {
        currentMode = mode;
        if (mode === 'login') {
            modalTitle.innerText = 'Connexion';
            modalActionBtn.innerText = 'Se connecter';
            modalSwitchTextSpan.innerHTML = `Pas encore de compte ? <a href="#" id="switchToSignup">S'inscrire</a>`;
        } else {
            modalTitle.innerText = 'Inscription';
            modalActionBtn.innerText = "S'inscrire";
            modalSwitchTextSpan.innerHTML = `Déjà un compte ? <a href="#" id="switchToLogin">Se connecter</a>`;
        }
        // réattacher events après refresh innerHTML
        const newSwitch = modalSwitchTextSpan.querySelector('a');
        if (newSwitch) {
            newSwitch.addEventListener('click', (e) => {
                e.preventDefault();
                if (currentMode === 'login') openModal('signup');
                else openModal('login');
            });
        }
        modal.style.display = 'flex';
    }

    document.getElementById('loginBtn').addEventListener('click', (e) => {
        e.preventDefault();
        openModal('login');
    });
    document.getElementById('signupNavBtn').addEventListener('click', (e) => {
        e.preventDefault();
        openModal('signup');
    });
    document.getElementById('heroCta').addEventListener('click', () => {
        openModal('signup');
    });
    document.getElementById('finalCtaBtn').addEventListener('click', () => {
        openModal('signup');
    });

    function closeModalFunc() {
        modal.style.display = 'none';
    }
    document.getElementById('closeModalBtn').addEventListener('click', closeModalFunc);
    window.addEventListener('click', (e) => {
        if (e.target === modal) closeModalFunc();
    });

    // action submit login/signup simulation
    modalActionBtn.addEventListener('click', () => {
        const email = document.getElementById('emailInput').value.trim();
        const pwd = document.getElementById('passwordInput').value.trim();
        if (!email || !pwd) {
            showToast("Veuillez remplir tous les champs.");
            return;
        }
        if (currentMode === 'login') {
            showToast(`Connexion simulée : Bienvenue ${email} ! (démo interactive)`);
        } else {
            showToast(`Inscription réussie ! Bienvenue sur AdaptLearn, ${email} 🎉`);
        }
        closeModalFunc();
        document.getElementById('emailInput').value = '';
        document.getElementById('passwordInput').value = '';
    });

    // chat bot simulation
    const chatBtn = document.getElementById('chatBotBtn');
    const toastMsg = document.getElementById('toastMsg');
    function showToast(message) {
        toastMsg.innerText = message;
        toastMsg.style.opacity = '1';
        setTimeout(() => {
            toastMsg.style.opacity = '0';
        }, 2800);
    }
    chatBtn.addEventListener('click', () => {
        showToast("🤖 Assistant IA : Comment puis-je vous aider aujourd'hui ? Expliquez un concept ou demandez une recommandation.");
    });

    // petite interaction d'exemple sur les cartes pour rendre plus vivant (optionnel)
    const cards = document.querySelectorAll('.card-feature, .step-card, .benefit-item');
    cards.forEach(card => {
        card.addEventListener('click', () => {
            // juste un feedback léger (ne pas déranger)
            const text = card.querySelector('h3, h4')?.innerText || 'fonctionnalité';
            showToast(`✨ ${text} — disponible sur votre tableau de bord personnalisé.`);
        });
    });

    // ajout d'un scroll smooth pour ancres
    document.querySelectorAll('.nav-links a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href');
            if(targetId && targetId !== '#') {
                const target = document.querySelector(targetId);
                if(target) {
                    e.preventDefault();
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });
    // bascule inscription depuis le modal si besoin (réattachement dynamique déjà fait)
    // rajout petit fix pour switch dans le modal init
    if(switchLink) {
        switchLink.addEventListener('click', (e) => {
            e.preventDefault();
            if(currentMode === 'login') openModal('signup');
            else openModal('login');
        });
    }

    // animation de bienvenue initiale
    setTimeout(() => {
        showToast("Bienvenue sur AdaptLearn ! L'IA adaptative vous attend 🚀");
    }, 1000);