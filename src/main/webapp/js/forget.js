<<<<<<< HEAD
﻿function changePassword() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const message = document.getElementById("message");

    if (email === ""  password === ""  confirmPassword === "") {
        message.style.color = "red";
        message.innerText = "Please fill in all fields";
        return;
    }

    if (password !== confirmPassword) {
        message.style.color = "red";
        message.innerText = "Passwords do not match";
        return;
    }

    message.style.color = "green";
    message.innerText = "Password changed successfully ✔️";
}
=======
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
>>>>>>> 806e130867f956bc2f89bd5ece5a77fdba857ad0
