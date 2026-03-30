// addcourse.js
document.addEventListener('DOMContentLoaded', function() {
    initializeFileUpload();
    initializeFormValidation();
});

function initializeFileUpload() {
    const fileInput = document.getElementById('files');
    const fileList = document.getElementById('fileList');
    const fileUploadArea = document.getElementById('fileUploadArea');
    
    if (!fileInput) return;
    
    fileInput.addEventListener('change', function(e) {
        updateFileList(this.files);
    });
    
    if (fileUploadArea) {
        fileUploadArea.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.style.borderColor = '#6366f1';
            this.style.backgroundColor = 'rgba(99, 102, 241, 0.05)';
        });
        
        fileUploadArea.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.style.borderColor = '#e2e8f0';
            this.style.backgroundColor = '';
        });
        
        fileUploadArea.addEventListener('drop', function(e) {
            e.preventDefault();
            this.style.borderColor = '#e2e8f0';
            this.style.backgroundColor = '';
            
            const files = e.dataTransfer.files;
            fileInput.files = files;
            updateFileList(files);
        });
    }
    
    function updateFileList(files) {
        if (!fileList) return;
        fileList.innerHTML = '';
        
        if (files.length === 0) return;
        
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const fileItem = createFileItem(file, i);
            fileList.appendChild(fileItem);
        }
        
        window.removeFile = function(index) {
            const newFiles = Array.from(fileInput.files).filter((_, idx) => idx !== index);
            const dataTransfer = new DataTransfer();
            newFiles.forEach(file => dataTransfer.items.add(file));
            fileInput.files = dataTransfer.files;
            updateFileList(fileInput.files);
        };
    }
    
    function createFileItem(file, index) {
        const div = document.createElement('div');
        div.className = 'file-item';
        
        const fileIcon = getFileIcon(file.type);
        const fileSize = formatFileSize(file.size);
        
        div.innerHTML = `
            <div class="file-info">
                <i class="fas ${fileIcon}"></i>
                <div>
                    <div class="file-name">${escapeHtml(file.name)}</div>
                    <div class="file-size">${fileSize}</div>
                </div>
            </div>
            <button type="button" class="remove-file" onclick="removeFile(${index})">
                <i class="fas fa-times"></i>
            </button>
        `;
        
        return div;
    }
    
    function getFileIcon(fileType) {
        if (fileType.includes('pdf')) return 'fa-file-pdf';
        if (fileType.includes('word') || fileType.includes('document')) return 'fa-file-word';
        if (fileType.includes('powerpoint') || fileType.includes('presentation')) return 'fa-file-powerpoint';
        if (fileType.includes('text')) return 'fa-file-alt';
        if (fileType.includes('zip') || fileType.includes('compressed')) return 'fa-file-archive';
        return 'fa-file';
    }
    
    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
    
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

function initializeFormValidation() {
    const form = document.getElementById('courseForm');
    if (!form) return;
    
    form.addEventListener('submit', function(e) {
        const title = document.getElementById('title')?.value.trim();
        const description = document.getElementById('description')?.value.trim();
        const niveau = document.getElementById('niveau')?.value;
        
        if (!title) {
            e.preventDefault();
            showError('Please enter a course title');
            return;
        }
        
        if (title.length < 3) {
            e.preventDefault();
            showError('Course title must be at least 3 characters');
            return;
        }
        
        if (!description) {
            e.preventDefault();
            showError('Please enter a course description');
            return;
        }
        
        if (description.length < 10) {
            e.preventDefault();
            showError('Course description must be at least 10 characters');
            return;
        }
        
        if (!niveau) {
            e.preventDefault();
            showError('Please select a level for this course');
            return;
        }
    });
}

function showError(message) {
    let alert = document.querySelector('.alert-error');
    if (!alert) {
        alert = document.createElement('div');
        alert.className = 'alert alert-error';
        const form = document.getElementById('courseForm');
        if (form) {
            form.insertBefore(alert, form.firstChild);
        }
    }
    
    alert.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    
    setTimeout(() => {
        if (alert) alert.remove();
    }, 5000);
}