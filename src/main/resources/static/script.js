// Sidebar functionality for maintaining scrollability
document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('sidebar');
    
    if (sidebar) {
        sidebar.addEventListener('show.bs.offcanvas', function () {
            document.body.classList.add('offcanvas-open');
        });
        
        sidebar.addEventListener('hide.bs.offcanvas', function () {
            document.body.classList.remove('offcanvas-open');
        });
    }
});