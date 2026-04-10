/**
 * dashbord.js - Teacher Dashboard Logic
 * Version complète avec gestion des quiz, filtres et visualisation des cours
 */

// ========== VARIABLES GLOBALES ==========
let courseToDelete = null;
let selectedFiles = [];
let allCourses = []; // Stocker tous les cours pour le filtrage

// Variables pour la gestion des quiz
let tempQuestionsList = [];
let createdQuizId = null;

// ========== INITIALISATION PROFIL UTILISATEUR ==========
function initUserProfile() {
    let teacherName = document.getElementById('teacherName')?.value;
    let teacherId = document.getElementById('teacherId')?.value;
    
    console.log('Teacher Name from hidden field:', teacherName);
    console.log('Teacher ID from hidden field:', teacherId);
    
    if (!teacherName || teacherName === 'null' || teacherName === '') {
        teacherName = sessionStorage.getItem('teacherName');
        if (teacherName) {
            console.log('Teacher Name from sessionStorage:', teacherName);
        }
    }
    
    if (!teacherName || teacherName === 'null' || teacherName === '') {
        teacherName = 'Professor';
    }
    
    updateUserProfile(teacherName);
}

function updateUserProfile(teacherName) {
    const avatarImg = document.getElementById('userAvatar');
    const nameElement = document.getElementById('userName');
    const roleElement = document.querySelector('.user-profile .user-role');
    
    if (avatarImg) {
        const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(teacherName)}&background=6366f1&color=fff&size=128`;
        avatarImg.src = avatarUrl;
        avatarImg.alt = `Avatar de ${teacherName}`;
    }
    
    if (nameElement) {
        nameElement.textContent = teacherName;
    }
    
    if (roleElement) {
        roleElement.innerHTML = '👨‍🏫 Teacher';
    }
    
    sessionStorage.setItem('teacherName', teacherName);
}

// ========== GESTION DU FILTRE PAR MODULE ==========

// Récupérer la liste unique des modules depuis les cours
function getUniqueModules(courses) {
    const modules = new Set();
    courses.forEach(course => {
        if (course.module && course.module.trim() !== '') {
            modules.add(course.module);
        }
    });
    return Array.from(modules).sort();
}

// Mettre à jour le select des modules
function updateModuleFilter(courses) {
    const filterSelect = document.getElementById('filterModule');
    if (!filterSelect) return;
    
    const uniqueModules = getUniqueModules(courses);
    const currentValue = filterSelect.value;
    
    filterSelect.innerHTML = '<option value="all">📂 All Modules</option>';
    
    uniqueModules.forEach(module => {
        filterSelect.innerHTML += `<option value="${escapeHtml(module)}">📁 ${escapeHtml(module)}</option>`;
    });
    
    if (currentValue && currentValue !== 'all') {
        const optionExists = Array.from(filterSelect.options).some(opt => opt.value === currentValue);
        if (optionExists) {
            filterSelect.value = currentValue;
        }
    }
}

// Filtrer les cours par module
function filterCoursesByModule(courses, selectedModule) {
    if (!selectedModule || selectedModule === 'all') {
        return courses;
    }
    return courses.filter(course => course.module === selectedModule);
}

// Afficher les cours filtrés
function displayFilteredCourses(courses) {
    const coursesGrid = document.getElementById('coursesGrid');
    const selectedModule = document.getElementById('filterModule')?.value || 'all';
    
    if (!coursesGrid) return;
    
    const filteredCourses = filterCoursesByModule(courses, selectedModule);
    
    console.log(`📚 Affichage des cours - Module: ${selectedModule}, Total: ${filteredCourses.length}/${courses.length}`);
    
    if (filteredCourses.length === 0) {
        coursesGrid.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-folder-open"></i>
                <p>Aucun cours trouvé pour le module "${escapeHtml(selectedModule)}"</p>
                <button class="btn-primary" onclick="resetFilter()" style="margin-top: 1rem;">
                    <i class="fas fa-undo"></i> Show all courses
                </button>
            </div>
        `;
        return;
    }
    
    coursesGrid.innerHTML = '';
    filteredCourses.forEach(course => {
        coursesGrid.appendChild(createCourseCard(course));
    });
}

// Réinitialiser le filtre
function resetFilter() {
    const filterSelect = document.getElementById('filterModule');
    if (filterSelect) {
        filterSelect.value = 'all';
    }
    displayFilteredCourses(allCourses);
}

// Initialiser le filtre
function initFilter(courses) {
    updateModuleFilter(courses);
    
    const filterSelect = document.getElementById('filterModule');
    if (filterSelect) {
        filterSelect.removeEventListener('change', handleFilterChange);
        filterSelect.addEventListener('change', handleFilterChange);
    }
}

function handleFilterChange() {
    displayFilteredCourses(allCourses);
}

// ========== MODAL COURS ==========
function openAddCourseModal() {
    console.log('Opening modal...');
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
        resetCourseForm();
        console.log('Modal opened');
    } else {
        console.error('Modal not found!');
    }
}

function closeAddCourseModal() {
    console.log('Closing modal...');
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = 'auto';
        resetCourseForm();
    }
}

function resetCourseForm() {
    const form = document.getElementById('courseForm');
    if (form) {
        form.reset();
    }
    selectedFiles = [];
    updateFileList();
    const fileInput = document.getElementById('filesInput');
    if (fileInput) fileInput.value = '';
}

// ========== FILE UPLOAD ==========
function setupFileUpload() {
    const fileInput = document.getElementById('filesInput');
    const dropArea = document.getElementById('fileUploadArea');
    
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const files = Array.from(e.target.files);
            selectedFiles = [...selectedFiles, ...files];
            updateFileList();
        });
    }
    
    if (dropArea) {
        dropArea.addEventListener('click', () => {
            if (fileInput) fileInput.click();
        });
        
        dropArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#667eea';
            dropArea.style.background = '#f3f4f6';
            dropArea.classList.add('dragover');
        });
        
        dropArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
            dropArea.classList.remove('dragover');
        });
        
        dropArea.addEventListener('drop', (e) => {
            e.preventDefault();
            dropArea.style.borderColor = '#d1d5db';
            dropArea.style.background = '#f9fafb';
            dropArea.classList.remove('dragover');
            const files = Array.from(e.dataTransfer.files);
            selectedFiles = [...selectedFiles, ...files];
            updateFileList();
            if (fileInput) {
                const dataTransfer = new DataTransfer();
                selectedFiles.forEach(file => dataTransfer.items.add(file));
                fileInput.files = dataTransfer.files;
            }
        });
    }
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getFileIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    if (['jpg', 'jpeg', 'png', 'gif', 'svg', 'webp'].includes(extension)) return 'fa-file-image';
    if (extension === 'pdf') return 'fa-file-pdf';
    if (['doc', 'docx'].includes(extension)) return 'fa-file-word';
    if (['xls', 'xlsx', 'csv'].includes(extension)) return 'fa-file-excel';
    if (['ppt', 'pptx'].includes(extension)) return 'fa-file-powerpoint';
    if (['mp4', 'avi', 'mov', 'mkv', 'webm'].includes(extension)) return 'fa-file-video';
    if (['mp3', 'wav', 'ogg', 'flac'].includes(extension)) return 'fa-file-audio';
    if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) return 'fa-file-archive';
    if (['txt', 'md', 'rtf'].includes(extension)) return 'fa-file-alt';
    return 'fa-file';
}

function updateFileList() {
    const fileListDiv = document.getElementById('fileList');
    if (!fileListDiv) return;
    
    if (selectedFiles.length === 0) {
        fileListDiv.innerHTML = '';
        return;
    }
    
    const totalSize = selectedFiles.reduce((sum, file) => sum + file.size, 0);
    const totalSizeFormatted = formatFileSize(totalSize);
    
    const header = `
        <div class="file-stats">
            <span>📁 ${selectedFiles.length} fichier(s)</span>
            <span>💾 ${totalSizeFormatted}</span>
        </div>
    `;
    
    const filesList = selectedFiles.map((file, index) => `
        <div class="file-item">
            <i class="fas ${getFileIcon(file.name)}"></i>
            <span title="${escapeHtml(file.name)}">${escapeHtml(file.name.length > 30 ? file.name.substring(0, 27) + '...' : file.name)}</span>
            <small>(${formatFileSize(file.size)})</small>
            <button onclick="removeFile(${index})" class="remove-file">🗑</button>
        </div>
    `).join('');
    
    fileListDiv.innerHTML = header + filesList;
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
    const fileInput = document.getElementById('filesInput');
    if (fileInput) {
        const dataTransfer = new DataTransfer();
        selectedFiles.forEach(file => dataTransfer.items.add(file));
        fileInput.files = dataTransfer.files;
    }
}

// ========== COURSE SUBMISSION ==========
async function submitCourseForm(event) {
    event.preventDefault();
    
    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Création...';
    
    const title = document.getElementById('courseTitle').value.trim();
    const description = document.getElementById('courseDescription').value.trim();
    const niveau = document.getElementById('courseLevel').value;
    
    if (!title || !description || !niveau) {
        showNotification('Veuillez remplir tous les champs obligatoires', 'error');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
        return;
    }
    
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('niveau', niveau);
    
    const teacherId = document.getElementById('teacherId')?.value;
    const teacherName = document.getElementById('teacherName')?.value;
    
    if (teacherId) formData.append('teacherId', teacherId);
    if (teacherName) formData.append('teacherName', teacherName);
    
    selectedFiles.forEach(file => {
        formData.append('files', file);
    });
    
    console.log('📤 Envoi du cours:', { title, niveau, teacherId, teacherName });
    
    try {
        const response = await fetch('/teacher/api/courses', {
            method: 'POST',
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification('Cours créé avec succès!', 'success');
            closeAddCourseModal();
            await loadCourses();
            await loadStats();
        } else {
            throw new Error(data.message || 'Erreur lors de la création');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-save"></i> Créer';
    }
}

// ========== COURSE MANAGEMENT ==========
async function loadCourses() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch courses');
        
        const courses = await response.json();
        const coursesGrid = document.getElementById('coursesGrid');
        
        console.log('📚 Cours chargés:', courses.length);
        
        // Stocker tous les cours pour le filtrage
        allCourses = courses;
        
        // Initialiser le filtre avec les cours chargés
        initFilter(courses);
        
        if (!coursesGrid) return;
        
        if (courses.length === 0) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book-open"></i>
                    <p>You have not yet created a course</p>
                    <button class="btn-primary" onclick="openAddCourseModal()">
                        Create your first course
                    </button>
                </div>
            `;
            return;
        }
        
        // Afficher les cours (filtrés ou non)
        displayFilteredCourses(courses);
        
    } catch (error) {
        console.error('Error loading courses:', error);
        showNotification('Erreur lors du chargement des cours', 'error');
        
        const coursesGrid = document.getElementById('coursesGrid');
        if (coursesGrid) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-exclamation-triangle"></i>
                    <p>Erreur de chargement des cours</p>
                    <button class="btn-primary" onclick="loadCourses()">
                        Réessayer
                    </button>
                </div>
            `;
        }
    }
}

function createCourseCard(course) {
    const div = document.createElement('div');
    div.className = 'course-card';
    div.innerHTML = `
        <div class="course-image">
            📘
            <span class="course-badge">${course.status === 'ACTIVE' ? 'Actif' : 'Brouillon'}</span>
        </div>
        <div class="course-content">
            <h3 class="course-title">${escapeHtml(course.title)}</h3>
            <p class="course-description">${escapeHtml(course.description ? course.description.substring(0, 100) : '')}${course.description && course.description.length > 100 ? '...' : ''}</p>
            <div class="course-meta">
                <div class="course-stats">
                    <span><i class="fas fa-tag"></i> ${escapeHtml(course.module || 'Non défini')}</span>
                    <span><i class="fas fa-question-circle"></i> ${course.totalQuizzes || 0} quiz</span>
                    <span><i class="fas fa-users"></i> ${course.totalStudents || 0} étudiants</span>
                </div>
                <div class="course-actions">
                    <button class="btn-view" onclick="viewCourse(${course.id})"><i class="fas fa-eye"></i> Voir</button>
                    <button class="btn-edit" onclick="editCourse(${course.id})"><i class="fas fa-edit"></i> Modifier</button>
                    <button class="btn-quiz" onclick="createQuiz(${course.id}, '${escapeHtml(course.module || course.niveau)}', '${escapeHtml(course.niveau)}')">
                        <i class="fas fa-question-circle"></i> Quiz
                    </button>
                    <button class="btn-delete" onclick="showDeleteModal(${course.id}, '${escapeHtml(course.title)}')"><i class="fas fa-trash"></i> Supprimer</button>
                </div>
            </div>
        </div>
    `;
    return div;
}

async function loadStats() {
    try {
        const response = await fetch('/teacher/my-courses');
        if (!response.ok) throw new Error('Failed to fetch stats');
        
        const courses = await response.json();
        
        const totalCoursesEl = document.getElementById('totalCourses');
        const totalQuizzesEl = document.getElementById('totalQuizzes');
        const totalStudentsEl = document.getElementById('totalStudents');
        
        if (totalCoursesEl) totalCoursesEl.textContent = courses.length;
        
        let totalQuizzes = 0;
        let totalStudents = 0;
        courses.forEach(course => {
            totalQuizzes += course.totalQuizzes || 0;
            totalStudents += course.totalStudents || 0;
        });
        
        if (totalQuizzesEl) totalQuizzesEl.textContent = totalQuizzes;
        if (totalStudentsEl) totalStudentsEl.textContent = totalStudents;
    } catch (error) {
        console.error('Error loading stats:', error);
        const totalCoursesEl = document.getElementById('totalCourses');
        const totalQuizzesEl = document.getElementById('totalQuizzes');
        const totalStudentsEl = document.getElementById('totalStudents');
        
        if (totalCoursesEl) totalCoursesEl.textContent = '0';
        if (totalQuizzesEl) totalQuizzesEl.textContent = '0';
        if (totalStudentsEl) totalStudentsEl.textContent = '0';
    }
}

// ========== VISUALISATION DES COURS ==========
function viewCourse(courseId) {
    console.log('📖 Viewing course:', courseId);
    window.location.href = `/teacher/view-course/${courseId}`;
}

function editCourse(courseId) {
    window.location.href = `/teacher/edit-course/${courseId}`;
}

// ========== GESTION UNIQUE DES QUIZ (FENÊTRE MODALE) ==========

// Ouvrir la fenêtre modale unique
function openCreateQuizModal(courseId, courseModule, courseNiveau) {
    tempQuestionsList = [];
    createdQuizId = null;
    
    document.getElementById('quizCourseId').value = courseId;
    document.getElementById('quizCourseModule').value = courseModule;
    document.getElementById('quizCourseNiveau').value = courseNiveau;
    
    // Réinitialiser le formulaire
    document.getElementById('quizTitle').value = '';
    document.getElementById('quizDescription').value = '';
    document.getElementById('quizTimeLimit').value = '30';
    document.getElementById('quizPassingScore').value = '70';
    document.getElementById('newQuestionText').value = '';
    document.getElementById('newQuestionPoints').value = '10';
    document.getElementById('newQuestionType').value = 'SINGLE_CHOICE';
    
    // Réinitialiser les options
    resetModalOptions();
    
    // Réinitialiser l'affichage des questions
    updateQuestionsDisplay();
    
    // Afficher le modal
    const modal = document.getElementById('createQuizModal');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

// Nouvelle fonction createQuiz (remplace l'ancienne)
function createQuiz(courseId, courseModule, courseNiveau) {
    console.log('📝 Opening quiz creation modal for course:', { courseId, courseModule, courseNiveau });
    openCreateQuizModal(courseId, courseModule, courseNiveau);
}

// Fermer la fenêtre modale
function closeQuizModal() {
    const modal = document.getElementById('createQuizModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
    if (createdQuizId) {
        loadCourses();
        loadStats();
    }
}

// Réinitialiser les options
function resetModalOptions() {
    const container = document.getElementById('modalOptionsContainer');
    if (container) {
        container.innerHTML = `
            <div class="option-row" style="display: flex; gap: 0.5rem; margin-bottom: 0.5rem; align-items: center;">
                <input type="checkbox" class="correct-checkbox">
                <input type="text" class="option-text" placeholder="Option 1" style="flex: 1; padding: 0.5rem; border: 1px solid #e5e7eb; border-radius: 8px;">
                <button type="button" class="remove-option-btn" onclick="removeModalOption(this)" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;">🗑</button>
            </div>
            <div class="option-row" style="display: flex; gap: 0.5rem; margin-bottom: 0.5rem; align-items: center;">
                <input type="checkbox" class="correct-checkbox">
                <input type="text" class="option-text" placeholder="Option 2" style="flex: 1; padding: 0.5rem; border: 1px solid #e5e7eb; border-radius: 8px;">
                <button type="button" class="remove-option-btn" onclick="removeModalOption(this)" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;">🗑</button>
            </div>
        `;
    }
}

// Ajouter une option
function addModalOption() {
    const container = document.getElementById('modalOptionsContainer');
    if (container) {
        const optionCount = container.children.length + 1;
        const div = document.createElement('div');
        div.className = 'option-row';
        div.style.cssText = 'display: flex; gap: 0.5rem; margin-bottom: 0.5rem; align-items: center;';
        div.innerHTML = `
            <input type="checkbox" class="correct-checkbox">
            <input type="text" class="option-text" placeholder="Option ${optionCount}" style="flex: 1; padding: 0.5rem; border: 1px solid #e5e7eb; border-radius: 8px;">
            <button type="button" class="remove-option-btn" onclick="removeModalOption(this)" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;">🗑</button>
        `;
        container.appendChild(div);
    }
}

// Supprimer une option
function removeModalOption(btn) {
    const container = document.getElementById('modalOptionsContainer');
    if (container && container.children.length > 2) {
        btn.closest('.option-row').remove();
    } else if (container && container.children.length <= 2) {
        showNotification('You need at least 2 options', 'error');
    }
}

// Ajouter une question à la liste temporaire
function addQuestionToList() {
    const questionText = document.getElementById('newQuestionText')?.value.trim();
    if (!questionText) {
        showNotification('Please enter a question', 'error');
        return;
    }
    
    const options = [];
    const optionRows = document.querySelectorAll('#modalOptionsContainer .option-row');
    let hasCorrectAnswer = false;
    
    optionRows.forEach(row => {
        const checkbox = row.querySelector('.correct-checkbox');
        const textInput = row.querySelector('.option-text');
        if (textInput && textInput.value.trim()) {
            const isCorrect = checkbox ? checkbox.checked : false;
            if (isCorrect) hasCorrectAnswer = true;
            options.push({
                optionText: textInput.value.trim(),
                correct: isCorrect
            });
        }
    });
    
    if (options.length < 2) {
        showNotification('Please add at least 2 options', 'error');
        return;
    }
    
    if (!hasCorrectAnswer) {
        showNotification('Please select at least one correct answer', 'error');
        return;
    }
    
    const question = {
        id: Date.now(),
        questionText: questionText,
        questionType: document.getElementById('newQuestionType')?.value || 'SINGLE_CHOICE',
        points: parseInt(document.getElementById('newQuestionPoints')?.value || 10),
        orderNumber: tempQuestionsList.length + 1,
        options: options
    };
    
    tempQuestionsList.push(question);
    updateQuestionsDisplay();
    
    document.getElementById('newQuestionText').value = '';
    document.getElementById('newQuestionPoints').value = '10';
    resetModalOptions();
    
    showNotification('Question added!', 'success');
}

// Supprimer une question de la liste
function removeQuestionFromList(questionId) {
    tempQuestionsList = tempQuestionsList.filter(q => q.id !== questionId);
    tempQuestionsList.forEach((q, index) => {
        q.orderNumber = index + 1;
    });
    updateQuestionsDisplay();
    showNotification('Question removed', 'info');
}

// Modifier une question
function editQuestionInList(questionId) {
    const question = tempQuestionsList.find(q => q.id === questionId);
    if (!question) return;
    
    document.getElementById('newQuestionText').value = question.questionText;
    document.getElementById('newQuestionPoints').value = question.points;
    document.getElementById('newQuestionType').value = question.questionType;
    
    const container = document.getElementById('modalOptionsContainer');
    if (container) {
        container.innerHTML = '';
        question.options.forEach((opt, idx) => {
            const div = document.createElement('div');
            div.className = 'option-row';
            div.style.cssText = 'display: flex; gap: 0.5rem; margin-bottom: 0.5rem; align-items: center;';
            div.innerHTML = `
                <input type="checkbox" class="correct-checkbox" ${opt.correct ? 'checked' : ''}>
                <input type="text" class="option-text" value="${escapeHtml(opt.optionText)}" style="flex: 1; padding: 0.5rem; border: 1px solid #e5e7eb; border-radius: 8px;">
                <button type="button" class="remove-option-btn" onclick="removeModalOption(this)" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;">🗑</button>
            `;
            container.appendChild(div);
        });
    }
    
    removeQuestionFromList(questionId);
    document.querySelector('.btn-add-question')?.scrollIntoView({ behavior: 'smooth' });
}

// Mettre à jour l'affichage des questions
function updateQuestionsDisplay() {
    const questionsList = document.getElementById('questionsList');
    const emptyMsg = document.getElementById('emptyQuestionsMsg');
    const questionCount = document.getElementById('questionCount');
    
    if (tempQuestionsList.length === 0) {
        if (emptyMsg) emptyMsg.style.display = 'block';
        if (questionsList) questionsList.innerHTML = '';
        if (questionCount) questionCount.textContent = '(0 questions)';
        return;
    }
    
    if (emptyMsg) emptyMsg.style.display = 'none';
    if (questionCount) questionCount.textContent = `(${tempQuestionsList.length} questions)`;
    
    const questionsHtml = tempQuestionsList.map(q => `
        <div class="question-item" style="background: #f3f4f6; padding: 0.75rem; margin-bottom: 0.75rem; border-radius: 12px; border-left: 3px solid #667eea;">
            <div style="display: flex; justify-content: space-between; align-items: start;">
                <div style="flex: 1;">
                    <strong style="color: #1f2937;">${q.orderNumber}. ${escapeHtml(q.questionText)}</strong>
                    <div style="font-size: 0.75rem; color: #6b7280; margin-top: 0.25rem;">
                        <span><i class="fas fa-star"></i> ${q.points} pts</span>
                        <span style="margin-left: 0.5rem;"><i class="fas ${q.questionType === 'SINGLE_CHOICE' ? 'fa-dot-circle' : 'fa-check-square'}"></i> ${q.questionType === 'SINGLE_CHOICE' ? 'Single Choice' : 'Multiple Choice'}</span>
                        <span style="margin-left: 0.5rem;"><i class="fas fa-list"></i> ${q.options.length} options</span>
                    </div>
                    <div style="font-size: 0.7rem; color: #10b981; margin-top: 0.25rem;">
                        ${q.options.filter(o => o.correct).map(o => `<span style="background: #d1fae5; padding: 0.125rem 0.5rem; border-radius: 12px; margin-right: 0.25rem; display: inline-block; margin-bottom: 0.25rem;">✓ ${escapeHtml(o.optionText.substring(0, 30))}</span>`).join('')}
                    </div>
                </div>
                <div>
                    <button onclick="editQuestionInList(${q.id})" style="background: #fed7aa; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; margin-right: 0.25rem; cursor: pointer;" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button onclick="removeQuestionFromList(${q.id})" style="background: #fee2e2; border: none; border-radius: 8px; padding: 0.25rem 0.5rem; cursor: pointer;" title="Remove">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    if (questionsList) questionsList.innerHTML = questionsHtml;
}

// Sauvegarder le quiz ET toutes les questions
async function saveQuizAndQuestions() {
    const saveBtn = document.getElementById('saveQuizBtn');
    if (!saveBtn) return;
    
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving...';
    
    const quizTitle = document.getElementById('quizTitle')?.value.trim();
    if (!quizTitle) {
        showNotification('Please enter a quiz title', 'error');
        saveBtn.disabled = false;
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Save Quiz & Questions';
        return;
    }
    
    if (tempQuestionsList.length === 0) {
        showNotification('Please add at least one question', 'error');
        saveBtn.disabled = false;
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Save Quiz & Questions';
        return;
    }
    
    try {
        const quizData = {
            courseId: parseInt(document.getElementById('quizCourseId')?.value || 0),
            title: quizTitle,
            description: document.getElementById('quizDescription')?.value || '',
            timeLimit: parseInt(document.getElementById('quizTimeLimit')?.value || 30),
            passingScore: parseInt(document.getElementById('quizPassingScore')?.value || 70)
        };
        
        console.log('📝 Creating quiz:', quizData);
        
        const quizResponse = await fetch('/teacher/api/create-quiz', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(quizData)
        });
        
        const quizResult = await quizResponse.json();
        
        if (!quizResult.success) {
            throw new Error(quizResult.message || 'Failed to create quiz');
        }
        
        const quizId = quizResult.quizId;
        createdQuizId = quizId;
        
        let allQuestionsSuccess = true;
        
        for (const question of tempQuestionsList) {
            const questionData = {
                questionText: question.questionText,
                questionType: question.questionType,
                points: question.points,
                orderNumber: question.orderNumber,
                options: question.options
            };
            
            const questionResponse = await fetch(`/teacher/api/quizzes/${quizId}/questions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(questionData)
            });
            
            const questionResult = await questionResponse.json();
            
            if (!questionResult.success) {
                console.error('Failed to add question:', question.questionText);
                allQuestionsSuccess = false;
            }
        }
        
        if (allQuestionsSuccess) {
            showNotification(`Quiz "${quizTitle}" created successfully with ${tempQuestionsList.length} questions!`, 'success');
            closeQuizModal();
            await loadCourses();
            await loadStats();
        } else {
            showNotification('Quiz created but some questions failed to save', 'info');
        }
        
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error: ' + error.message, 'error');
    } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = '<i class="fas fa-save"></i> Save Quiz & Questions';
    }
}

// ========== DELETE MODAL ==========
function showDeleteModal(courseId, courseTitle) {
    courseToDelete = courseId;
    const courseNameElement = document.getElementById('courseNameToDelete');
    if (courseNameElement) {
        courseNameElement.textContent = courseTitle;
    }
    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.style.display = 'flex';
    }
}

function closeDeleteModal() {
    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.style.display = 'none';
    }
    courseToDelete = null;
}

async function confirmDelete() {
    if (!courseToDelete) return;
    
    try {
        console.log('🗑 Suppression du cours ID:', courseToDelete);
        
        const response = await fetch(`/teacher/delete-course/${courseToDelete}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                showNotification('Cours supprimé avec succès', 'success');
                closeDeleteModal();
                await loadCourses();
                await loadStats();
            } else {
                throw new Error(data.message || 'Delete failed');
            }
        } else {
            throw new Error('Erreur HTTP: ' + response.status);
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Erreur lors de la suppression: ' + error.message, 'error');
    }
}

// ========== UTILITIES ==========
function showNotification(message, type) {
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        if (notification.remove) notification.remove();
    }, 3000);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function logout() {
    if (confirm('Voulez-vous vraiment vous déconnecter ?')) {
        sessionStorage.clear();
        window.location.href = '/logout';
    }
}

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing...');
    
    initUserProfile();
    loadCourses();
    loadStats();
    setupFileUpload();
    
    const openBtn = document.getElementById('openNewCourseBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    
    if (openBtn) {
        openBtn.addEventListener('click', (e) => {
            e.preventDefault();
            openAddCourseModal();
        });
    }
    
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', closeAddCourseModal);
    }
    
    if (cancelModalBtn) {
        cancelModalBtn.addEventListener('click', closeAddCourseModal);
    }
    
    // Formulaire de quiz
    const quizForm = document.getElementById('createQuizForm');
    if (quizForm) {
        quizForm.addEventListener('submit', (e) => {
            e.preventDefault();
            saveQuizAndQuestions();
        });
    }
    
    const myCoursesLink = document.getElementById('myCoursesLink');
    const myQuizzesLink = document.getElementById('myQuizzesLink');
    
    if (myCoursesLink) {
        myCoursesLink.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            myCoursesLink.classList.add('active');
            document.getElementById('pageTitle').innerHTML = '📚 My courses';
            loadCourses();
        });
    }
    
    if (myQuizzesLink) {
        myQuizzesLink.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            myQuizzesLink.classList.add('active');
            document.getElementById('pageTitle').innerHTML = '📝 My Quizzes';
            const coursesGrid = document.getElementById('coursesGrid');
            if (coursesGrid) {
                coursesGrid.innerHTML = `
                    <div class="empty-state">
                        <i class="fas fa-question-circle"></i>
                        <p>Quiz management coming soon!</p>
                    </div>
                `;
            }
        });
    }
    
    const courseForm = document.getElementById('courseForm');
    if (courseForm) {
        courseForm.addEventListener('submit', submitCourseForm);
    }
    
    window.addEventListener('click', (event) => {
        const addModal = document.getElementById('addCourseModal');
        const deleteModal = document.getElementById('deleteModal');
        const quizModal = document.getElementById('createQuizModal');
        if (event.target === addModal) closeAddCourseModal();
        if (event.target === deleteModal) closeDeleteModal();
        if (event.target === quizModal) closeQuizModal();
    });
    
    console.log('Initialization complete');
});