const form = document.getElementById("forgotForm");
const email = document.getElementById("email");
const error = document.getElementById("emailError");
const success = document.getElementById("successMsg");

form.addEventListener("submit", e => {
    e.preventDefault();

    error.style.display = "none";
    success.style.display = "none";
    email.classList.remove("error");

    if (email.value.trim() === "" || !email.value.includes("@")) {
        error.style.display = "block";
        email.classList.add("error");
        return;
    }

    success.style.display = "block";
    form.reset();
});