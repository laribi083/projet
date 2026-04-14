/**
 * user.js - JavaScript pour la gestion des utilisateurs
 */

// ========== INITIALISATION ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('User management initialized');
    initEventListeners();
    initSearch();
});

// ========== EVENT LISTENERS ==========
function initEventListeners() {
    // Students edit buttons
    const editStudentBtns = document.querySelectorAll('.edit-student');
    console.log('Edit student buttons found:', editStudentBtns.length);
    editStudentBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            const email = this.getAttribute('data-email');
            console.log('Edit student clicked:', id, name, email);
            editStudent(id, name, email);
        });
    });
    
    // Students delete buttons
    const deleteStudentBtns = document.querySelectorAll('.delete-student');
    console.log('Delete student buttons found:', deleteStudentBtns.length);
    deleteStudentBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            console.log('Delete student clicked:', id, name);
            deleteStudent(id, name);
        });
    });
    
    // Teachers edit buttons
    const editTeacherBtns = document.querySelectorAll('.edit-teacher');
    console.log('Edit teacher buttons found:', editTeacherBtns.length);
    editTeacherBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            const email = this.getAttribute('data-email');
            const department = this.getAttribute('data-department') || '';
            const phone = this.getAttribute('data-phone') || '';
            console.log('Edit teacher clicked:', id, name);
            editTeacher(id, name, email, department, phone);
        });
    });
    
    // Teachers delete buttons
    const deleteTeacherBtns = document.querySelectorAll('.delete-teacher');
    console.log('Delete teacher buttons found:', deleteTeacherBtns.length);
    deleteTeacherBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            console.log('Delete teacher clicked:', id, name);
            deleteTeacher(id, name);
        });
    });
}

// ========== STUDENT FUNCTIONS ==========

function deleteStudent(id, name) {
    if (confirm('Are you sure you want to delete student: ' + name + '? This action cannot be undone.')) {
        showNotification('Deleting student...', 'info');
        fetch('/admin/api/student/' + id, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('✅ Student deleted successfully!', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                showNotification('❌ Error: ' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('❌ Error deleting student: ' + error, 'error');
        });
    }
}

function editStudent(id, name, email) {
    document.getElementById('editStudentId').value = id;
    document.getElementById('editStudentName').value = name;
    document.getElementById('editStudentEmail').value = email;
    openModal('editStudentModal');
}

function saveEditStudent() {
    const id = document.getElementById('editStudentId').value;
    const name = document.getElementById('editStudentName').value.trim();
    const email = document.getElementById('editStudentEmail').value.trim();
    
    if (!name || !email) {
        showNotification('Please fill all fields', 'error');
        return;
    }
    
    showNotification('Updating student...', 'info');
    
    fetch('/admin/api/student/' + id, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: name, email: email })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('✅ Student updated successfully!', 'success');
            setTimeout(() => location.reload(), 1500);
        } else {
            showNotification('❌ Error: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('❌ Error updating student: ' + error, 'error');
    });
}

// ========== TEACHER FUNCTIONS ==========

function deleteTeacher(id, name) {
    if (confirm('Are you sure you want to delete teacher: ' + name + '? This action cannot be undone.')) {
        showNotification('Deleting teacher...', 'info');
        fetch('/admin/api/teacher/' + id, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('✅ Teacher deleted successfully!', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                showNotification('❌ Error: ' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('❌ Error deleting teacher: ' + error, 'error');
        });
    }
}

function editTeacher(id, name, email, department, phone) {
    document.getElementById('editTeacherId').value = id;
    document.getElementById('editTeacherName').value = name || '';
    document.getElementById('editTeacherEmail').value = email || '';
    document.getElementById('editTeacherDepartment').value = department || '';
    document.getElementById('editTeacherPhone').value = phone || '';
    openModal('editTeacherModal');
}

function saveEditTeacher() {
    const id = document.getElementById('editTeacherId').value;
    const name = document.getElementById('editTeacherName').value.trim();
    const email = document.getElementById('editTeacherEmail').value.trim();
    const department = document.getElementById('editTeacherDepartment').value;
    const phone = document.getElementById('editTeacherPhone').value;
    
    if (!name || !email) {
        showNotification('Please fill name and email fields', 'error');
        return;
    }
    
    showNotification('Updating teacher...', 'info');
    
    fetch('/admin/api/teacher/' + id, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
            name: name, 
            email: email, 
            department: department,
            phone: phone
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('✅ Teacher updated successfully!', 'success');
            setTimeout(() => location.reload(), 1500);
        } else {
            showNotification('❌ Error: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('❌ Error updating teacher: ' + error, 'error');
    });
}

// ========== MODAL FUNCTIONS ==========

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

function closeEditStudentModal() {
    closeModal('editStudentModal');
}

function closeEditTeacherModal() {
    closeModal('editTeacherModal');
}

// ========== SEARCH FUNCTIONS ==========

function initSearch() {
    const searchStudents = document.getElementById('searchStudents');
    const searchTeachers = document.getElementById('searchTeachers');
    
    if (searchStudents) {
        searchStudents.addEventListener('keyup', function() {
            filterTable('studentsTableBody', this.value);
        });
        console.log('Search students initialized');
    }
    
    if (searchTeachers) {
        searchTeachers.addEventListener('keyup', function() {
            filterTable('teachersTableBody', this.value);
        });
        console.log('Search teachers initialized');
    }
}

function filterTable(tableBodyId, searchTerm) {
    const tbody = document.getElementById(tableBodyId);
    if (!tbody) {
        console.log('Table body not found:', tableBodyId);
        return;
    }
    
    const rows = tbody.getElementsByTagName('tr');
    const term = searchTerm.toLowerCase();
    let visibleCount = 0;
    
    for (let row of rows) {
        const text = row.innerText.toLowerCase();
        const isVisible = text.includes(term);
        row.style.display = isVisible ? '' : 'none';
        if (isVisible) visibleCount++;
    }
    
    console.log(`Filtered ${tableBodyId}: ${visibleCount} rows visible`);
}

// ========== NOTIFICATION ==========

function showNotification(message, type) {
    // Remove existing notification
    const existingNotification = document.querySelector('.admin-notification');
    if (existingNotification) existingNotification.remove();
    
    const notification = document.createElement('div');
    notification.className = `admin-notification admin-notification-${type}`;
    
    let icon = 'fa-info-circle';
    if (type === 'success') icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';
    
    notification.innerHTML = `<i class="fas ${icon}"></i> ${message}`;
    notification.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
        background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
        color: white;
        border-radius: 8px;
        z-index: 1100;
        animation: slideIn 0.3s ease;
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 0.9rem;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        if (notification) {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (notification && notification.remove) notification.remove();
            }, 300);
        }
    }, 3000);
}

// ========== CLOSE MODALS ON OUTSIDE CLICK ==========
window.onclick = function(event) {
    const modals = ['editStudentModal', 'editTeacherModal'];
    modals.forEach(modalId => {
        const modal = document.getElementById(modalId);
        if (event.target === modal) {
            closeModal(modalId);
        }
    });
}

// Add animation styles
if (!document.querySelector('#notification-styles')) {
    const style = document.createElement('style');
    style.id = 'notification-styles';
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);
}