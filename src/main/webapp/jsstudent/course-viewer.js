// ============================================
// COURSE VIEWER JAVASCRIPT
// ============================================

/**
 * Copie le contenu du cours dans le presse-papier
 */
function copyContent() {
    const contentElement = document.querySelector('.viewer-content');
    if (!contentElement) {
        console.error('❌ Contenu non trouvé');
        return;
    }
    
    const content = contentElement.innerText;
    
    navigator.clipboard.writeText(content)
        .then(() => {
            showTemporaryMessage('📋 Contenu copié dans le presse-papier !', 'success');
        })
        .catch(err => {
            console.error('❌ Erreur de copie:', err);
            showTemporaryMessage('❌ Erreur lors de la copie', 'error');
        });
}

/**
 * Imprime le contenu du cours
 */
function printContent() {
    const content = document.querySelector('.viewer-content').innerHTML;
    const title = document.querySelector('.viewer-header h1').innerText;
    
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>${title}</title>
            <meta charset="UTF-8">
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    padding: 40px;
                    line-height: 1.6;
                }
                h1 { color: #333; }
                pre {
                    background: #f4f4f4;
                    padding: 15px;
                    border-radius: 8px;
                    overflow-x: auto;
                }
                img { max-width: 100%; }
                @media print {
                    body { padding: 20px; }
                }
            </style>
        </head>
        <body>
            <h1>${title}</h1>
            ${content}
        </body>
        </html>
    `);
    printWindow.document.close();
    printWindow.print();
}

/**
 * Affiche un message temporaire à l'utilisateur
 */
function showTemporaryMessage(message, type = 'info') {
    // Supprimer les messages existants
    const existingMessage = document.querySelector('.temp-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // Créer le message
    const messageDiv = document.createElement('div');
    messageDiv.className = `temp-message ${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 8px;
        background: ${type === 'success' ? '#28a745' : '#dc3545'};
        color: white;
        font-size: 0.9rem;
        z-index: 1000;
        animation: slideIn 0.3s ease-out;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    `;
    
    document.body.appendChild(messageDiv);
    
    // Ajouter l'animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(100px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }
    `;
    document.head.appendChild(style);
    
    // Supprimer après 3 secondes
    setTimeout(() => {
        messageDiv.style.opacity = '0';
        messageDiv.style.transition = 'opacity 0.3s';
        setTimeout(() => {
            messageDiv.remove();
        }, 300);
    }, 3000);
}

/**
 * Vérifie si le contenu est chargé et ajoute des fonctionnalités supplémentaires
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('📚 Course Viewer chargé');
    
    // Ajouter la possibilité de zoom sur les images
    const images = document.querySelectorAll('.viewer-content img');
    images.forEach(img => {
        img.style.cursor = 'pointer';
        img.addEventListener('click', function() {
            openImageModal(this.src);
        });
    });
    
    // Ajouter des liens de table des matières automatique si h2/h3 présents
    const headings = document.querySelectorAll('.viewer-content h2, .viewer-content h3');
    if (headings.length > 3) {
        addTableOfContents();
    }
});

/**
 * Ouvre une image en grand format
 */
function openImageModal(imageSrc) {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.9);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10000;
        cursor: pointer;
    `;
    
    const img = document.createElement('img');
    img.src = imageSrc;
    img.style.cssText = `
        max-width: 90%;
        max-height: 90%;
        border-radius: 10px;
    `;
    
    modal.appendChild(img);
    modal.addEventListener('click', () => modal.remove());
    document.body.appendChild(modal);
}

/**
 * Ajoute une table des matières automatique
 */
function addTableOfContents() {
    const content = document.querySelector('.viewer-content');
    const headings = content.querySelectorAll('h2, h3');
    
    const toc = document.createElement('div');
    toc.className = 'table-of-contents';
    toc.style.cssText = `
        background: #f8f9fa;
        padding: 20px;
        border-radius: 12px;
        margin-bottom: 30px;
    `;
    
    const tocTitle = document.createElement('h3');
    tocTitle.textContent = '📑 Table des matières';
    tocTitle.style.marginTop = '0';
    toc.appendChild(tocTitle);
    
    const tocList = document.createElement('ul');
    tocList.style.listStyle = 'none';
    tocList.style.paddingLeft = '0';
    
    headings.forEach(heading => {
        const id = heading.textContent.toLowerCase().replace(/[^a-z0-9]+/g, '-');
        heading.id = id;
        
        const item = document.createElement('li');
        item.style.margin = '8px 0';
        item.style.paddingLeft = heading.tagName === 'H3' ? '20px' : '0';
        
        const link = document.createElement('a');
        link.href = `#${id}`;
        link.textContent = heading.textContent;
        link.style.cssText = `
            color: #667eea;
            text-decoration: none;
        `;
        link.onclick = function(e) {
            e.preventDefault();
            heading.scrollIntoView({ behavior: 'smooth' });
        };
        
        item.appendChild(link);
        tocList.appendChild(item);
    });
    
    toc.appendChild(tocList);
    content.insertBefore(toc, content.firstChild);
}