// ========== Modal Functions ==========
function toggleModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    if (modal.classList.contains('active')) {
        modal.classList.remove('active');
    } else {
        modal.classList.add('active');
    }
}

// Close modal when clicking outside or pressing Escape
document.addEventListener('click', function (e) {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.closest('.modal').classList.remove('active');
    }
});

document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal.active').forEach(modal => {
            modal.classList.remove('active');
        });
    }
});

// ========== Sidebar Toggle ==========
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('open');
    }
}

// ========== Search & Filter ==========
function filterAssets(searchTerm) {
    const assets = document.querySelectorAll('.asset-card');
    const term = searchTerm.toLowerCase().trim();

    assets.forEach(asset => {
        const name = (asset.dataset.name || '').toLowerCase();
        const type = (asset.dataset.type || '').toLowerCase();

        if (name.includes(term) || type.includes(term)) {
            asset.style.display = '';
            asset.style.animation = 'fadeIn 0.3s ease forwards';
        } else {
            asset.style.display = 'none';
        }
    });

    checkEmptyState();
}

function filterByType(type) {
    const assets = document.querySelectorAll('.asset-card');

    assets.forEach(asset => {
        if (!type || asset.dataset.type === type) {
            asset.style.display = '';
            asset.style.animation = 'fadeIn 0.3s ease forwards';
        } else {
            asset.style.display = 'none';
        }
    });

    checkEmptyState();
}

function checkEmptyState() {
    const assetsList = document.getElementById('assetsList');
    if (!assetsList) return;

    const visibleAssets = assetsList.querySelectorAll('.asset-card:not([style*="display: none"])');
    const emptyState = assetsList.querySelector('.search-empty-state');

    if (visibleAssets.length === 0 && !emptyState) {
        const empty = document.createElement('div');
        empty.className = 'search-empty-state empty-state';
        empty.innerHTML = `
            <div class="empty-icon">
                <i class="fa-solid fa-search"></i>
            </div>
            <h4>No Results Found</h4>
            <p>Try adjusting your search or filter criteria.</p>
        `;
        assetsList.appendChild(empty);
    } else if (visibleAssets.length > 0 && emptyState) {
        emptyState.remove();
    }
}

// ========== Delete Confirmation ==========
function confirmDelete(assetId) {
    const deleteForm = document.getElementById('deleteForm');
    if (deleteForm) {
        deleteForm.action = '/assets/delete/' + assetId;
    }
    toggleModal('deleteModal');
}

// ========== Edit Asset Modal ==========
function openEditModal(assetId) {
    // For now, we'll use a simple approach - fetch asset data and populate form
    // In a real app, you'd have a separate edit modal or use AJAX
    alert('Edit functionality: Asset ID ' + assetId + '\nThis would open an edit modal. For demo, use the dashboard form.');
}

// ========== Password Strength ==========
function checkPasswordStrength(password) {
    const strengthBar = document.querySelector('.strength-bar');
    if (!strengthBar) return;

    let strength = 0;

    if (password.length >= 8) strength += 25;
    if (password.match(/[a-z]+/)) strength += 25;
    if (password.match(/[A-Z]+/)) strength += 25;
    if (password.match(/[0-9]+/)) strength += 12.5;
    if (password.match(/[^a-zA-Z0-9]+/)) strength += 12.5;

    strengthBar.style.width = strength + '%';

    if (strength < 50) {
        strengthBar.style.background = '#ef4444';
    } else if (strength < 75) {
        strengthBar.style.background = '#f59e0b';
    } else {
        strengthBar.style.background = '#10b981';
    }
}

// Add password strength listener
document.addEventListener('DOMContentLoaded', function () {
    const passwordInput = document.querySelector('input[name="password"][type="password"]');
    if (passwordInput && document.querySelector('.password-strength')) {
        passwordInput.addEventListener('input', function () {
            checkPasswordStrength(this.value);
        });
    }
});

// ========== Auto-dismiss Alerts ==========
document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.animation = 'fadeOut 0.3s ease forwards';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
});

// Add fadeOut animation
const style = document.createElement('style');
style.textContent = `
    @keyframes fadeOut {
        from { opacity: 1; transform: translateX(0); }
        to { opacity: 0; transform: translateX(-20px); }
    }
`;
document.head.appendChild(style);

// ========== Form Validation ==========
document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const requiredFields = form.querySelectorAll('[required]');
            let valid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    valid = false;
                    field.style.borderColor = '#ef4444';
                    field.style.animation = 'shake 0.3s ease';
                } else {
                    field.style.borderColor = '';
                }
            });

            if (!valid) {
                e.preventDefault();
            }
        });
    });
});

// Add shake animation
const shakeStyle = document.createElement('style');
shakeStyle.textContent = `
    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(-5px); }
        75% { transform: translateX(5px); }
    }
`;
document.head.appendChild(shakeStyle);

// ========== Number Formatting ==========
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// ========== Responsive Sidebar ==========
document.addEventListener('DOMContentLoaded', function () {
    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function (e) {
        const sidebar = document.querySelector('.sidebar');
        const toggle = document.querySelector('.sidebar-toggle');

        if (sidebar && sidebar.classList.contains('open')) {
            if (!sidebar.contains(e.target) && !toggle?.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        }
    });
});

// ========== Chart Helpers ==========
const chartColors = {
    purple: 'rgba(124, 58, 237, ',
    blue: 'rgba(14, 165, 233, ',
    green: 'rgba(16, 185, 129, ',
    orange: 'rgba(245, 158, 11, ',
    red: 'rgba(239, 68, 68, ',
    pink: 'rgba(168, 85, 247, '
};

function getChartColors(count, opacity = 0.8) {
    const colors = Object.values(chartColors);
    return Array.from({ length: count }, (_, i) =>
        colors[i % colors.length] + opacity + ')'
    );
}

// ========== Smooth scroll for anchor links ==========
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({ behavior: 'smooth' });
        }
    });
});

// ========== Date formatting helper ==========
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// ========== Loading states ==========
function showLoading(button) {
    const originalContent = button.innerHTML;
    button.disabled = true;
    button.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Loading...';
    return () => {
        button.disabled = false;
        button.innerHTML = originalContent;
    };
}

console.log('🛡️ AssetGuard loaded successfully!');
