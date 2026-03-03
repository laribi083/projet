function changePassword() {
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