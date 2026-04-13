let levels = [
    {
        name: "Level 1",
        semesters: [
            { name: "Semester 1", modules: ["Math", "Physics", "Algo"] },
            { name: "Semester 2", modules: ["Web", "C", "English"] }
        ]
    },
    {
        name: "Level 2",
        semesters: [
            { name: "Semester 3", modules: ["Java", "DB", "OS"] },
            { name: "Semester 4", modules: ["Spring", "Networks", "AI"] }
        ]
    },
    {
        name: "Level 3",
        semesters: [
            { name: "Semester 5", modules: ["React", "Security", "Cloud"] },
            { name: "Semester 6", modules: ["Project", "Internship"] }
        ]
    }
];

function renderLevels() {
    let container = document.getElementById("levelsContainer");
    container.innerHTML = "";

    levels.forEach(level => {
        let html = `
        <div class="level-card">
            <div class="level-title">${level.name}</div>
        `;

        level.semesters.forEach(sem => {
            html += `
            <div class="semester">
                <div class="semester-title">${sem.name}</div>
                <div class="modules">
            `;

            sem.modules.forEach(m => {
                html += <div class="module">${m}</div>;
            });

            html += </div ></div >;
        });

        html += </div >;
        container.innerHTML += html;
    });
}

function go(page) {
    window.location.href = page;
}

renderLevels();