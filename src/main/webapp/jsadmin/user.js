let users = [
    { name: "Sara Ali", email: "sara@mail.com", role: "Teacher", status: "Active" },
    { name: "John Doe", email: "john@mail.com", role: "Student", status: "Active" }
];

function render() {
    let table = document.getElementById("userTable");
    table.innerHTML = "";

    users.forEach((u, i) => {

        let roleClass =
            u.role == "Admin" ? "role-admin" :
                u.role == "Teacher" ? "role-teacher" : "role-student";

        let statusClass =
            u.status == "Active" ? "status-active" : "status-blocked";

        table.innerHTML += `
        <tr>
            <td>
                <div class="user-cell">
                    <div class="avatar">${u.name[0]}</div>
                    <div>
                        <b>${u.name}</b><br>
                        <small>${u.email}</small>
                    </div>
                </div>
            </td>

            <td><span class="badge ${roleClass}">${u.role}</span></td>
            <td><span class="badge ${statusClass}">${u.status}</span></td>

            <td class="actions">
                <i class="fas fa-pen" onclick="editUser(${i})"></i>
                <i class="fas fa-trash" onclick="deleteUser(${i})"></i>
            </td>
        </tr>`;
    });
}

function addUser() {
    let name = document.getElementById("name").value;
    let email = document.getElementById("email").value;
    let role = document.getElementById("role").value;

    users.push({ name, email, role, status: "Active" });
    closeModal();
    render();
}

function deleteUser(i) {
    users.splice(i, 1);
    render();
}

function editUser(i) {
    alert("Edit soon 😄");
}

function openModal() {
    document.getElementById("modal").style.display = "block";
}

function closeModal() {
    document.getElementById("modal").style.display = "none";
}

document.getElementById("search").addEventListener("input", function () {
    let val = this.value.toLowerCase();
    document.querySelectorAll("tbody tr").forEach(row => {
        row.style.display = row.innerText.toLowerCase().includes(val) ? "" : "none";
    });
});

function go(page) {
    window.location.href = page;
}

render();