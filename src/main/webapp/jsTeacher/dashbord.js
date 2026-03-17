const addCourseBtn = document.getElementById("addCourseBtn");
const modal = document.getElementById("modal");
const closeModal = document.getElementById("closeModal");
const saveCourse = document.getElementById("saveCourse");
const coursesList = document.getElementById("coursesList");

const courseTitle = document.getElementById("courseTitle");
const courseDesc = document.getElementById("courseDesc");


addCourseBtn.onclick = () => {
    modal.style.display = "flex";
};


closeModal.onclick = () => {
    modal.style.display = "none";
};


saveCourse.onclick = () => {
    if (courseTitle.value.trim() === "") return;

    coursesList.classList.remove("empty");
    coursesList.innerHTML += 
        <div class="course-card">
            <h4>${courseTitle.value}</h4>
            <p>${courseDesc.value}</p>
        </div>
    ;

    courseTitle.value = "";
    courseDesc.value = "";
    modal.style.display = "none";
};