let courses = [
    { name: "React Basics", teacher: "Martin", level: "Level 3", status: "Pending" },
    { name: "Java OOP", teacher: "Sophie", level: "Level 2", status: "Pending" },
    { name: "HTML Intro", teacher: "Ali", level: "Level 1", status: "Approved" }
];

function render() {
    let table = document.getElementById("courseTable");
    table.innerHTML = "";

    let pending = 0, approved = 0, rejected = 0;

    courses.forEach((c, i) => {

        if (c.status == "Pending") pending++;
        if (c.status == "Approved") approved++;
        if (c.status == "Rejected") rejected++;

        let badgeClass =
            c.status == "Pending" ? "pending" :
                c.status == "Approved" ? "approved" : "rejected";

        table.innerHTML += `
        <tr>
            <td><b>${c.name}</b></td>
            <td>${c.teacher}</td>
            <td>${c.level}</td>
            <td><span class="badge ${badgeClass}">${c.status}</span></td>
            <td class="actions">
                ${c.status == "Pending" ? `
                    <button class="btn-approve" onclick="approve(${i})">✔</button>
                    <button class="btn-reject" onclick="reject(${i})">✖</button>
                ` : "-"}
            </td>
        </tr>`;
    });

    document.getElementById("pendingCount").innerText = pending;
    document.getElementById("approvedCount").innerText = approved;
    document.getElementById("rejectedCount").innerText = rejected;
}

function approve(i) {
    courses[i].status = "Approved";
    render();
}

function reject(i) {
    courses[i].status = "Rejected";
    render();
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