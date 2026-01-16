function toggleModal() {
    const modal = document.getElementById('addAssetModal');
    if (modal.classList.contains('active')) {
        modal.classList.remove('active');
        setTimeout(() => modal.style.display = 'none', 300);
    } else {
        modal.style.display = 'flex';
        // Force reflow
        modal.offsetHeight;
        modal.classList.add('active');
    }
}

// Close modal when clicking outside
window.onclick = function (event) {
    const modal = document.getElementById('addAssetModal');
    if (event.target == modal) {
        toggleModal();
    }
}
