const newPassword = document.getElementById("newPassword");
const bar = document.getElementById("bar");

const upper = document.getElementById("upper");
const number = document.getElementById("number");
const length = document.getElementById("length");

newPassword.addEventListener("input", () => {
    let value = newPassword.value;
    let strength = 0;

    if (/[A-Z]/.test(value)) {
        upper.textContent = "✅ At least 1 uppercase";
        strength++;
    } else {
        upper.textContent = "❌ At least 1 uppercase";
    }

    if (/[0-9]/.test(value)) {
        number.textContent = "✅ At least 1 number";
        strength++;
    } else {
        number.textContent = "❌ At least 1 number";
    }

    if (value.length >= 8) {
        length.textContent = "✅ At least 8 characters";
        strength++;
    } else {
        length.textContent = "❌ At least 8 characters";
    }

    bar.style.width = (strength * 33) + "%";
    bar.style.background = strength === 3 ? "green" : strength === 2 ? "orange" : "red";
});

document.querySelectorAll(".toggle").forEach(toggle => {
    toggle.addEventListener("click", () => {
        const input = toggle.previousElementSibling;
        input.type = input.type === "password" ? "text" : "password";
    });
});