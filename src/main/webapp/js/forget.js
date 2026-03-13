document.getElementById("forgetForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const email = document.getElementById("email");
    const passwordInputs = document.querySelectorAll('input[type="password"]');
    const password = passwordInputs[0];
    const confirmPassword = passwordInputs[1];

    const emailError = document.getElementById("emailError");
    const successMsg = document.getElementById("successMsg");
    emailError.style.display = "none";
    successMsg.style.display = "none";

    let valid = true;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.value)) {
        emailError.style.display = "block";
        valid = false;
    }
    if (password.value.length < 6) {
        alert("Password must be at least 6 characters");
        valid = false;
    }

    if (password.value !== confirmPassword.value) {
        alert("Passwords do not match");
        valid = false;
    }


    if (valid) {
        successMsg.style.display = "block";

    
        email.value = "";
        password.value = "";
        confirmPassword.value = "";
    }
});