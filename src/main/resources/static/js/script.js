document.addEventListener('DOMContentLoaded', function() {
    // Dark mode toggle
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (darkModeToggle) {
        darkModeToggle.addEventListener('change', function() {
            document.body.classList.toggle('dark-mode');
            localStorage.setItem('darkMode', this.checked);
        });
        const isDarkMode = localStorage.getItem('darkMode') === 'true';
        darkModeToggle.checked = isDarkMode;
        if (isDarkMode) {
            document.body.classList.add('dark-mode');
        }
    }

    // Drawer toggle (if implemented, currently not in use)
    const drawerToggle = document.getElementById('drawerToggle');
    const drawer = document.getElementById('drawer');
    if (drawerToggle && drawer) {
        drawerToggle.addEventListener('click', function() {
            drawer.classList.toggle('active');
        });
    }
});

function loadSalesChart() {
    const salesLabels = JSON.parse(document.getElementById('salesLabels').textContent);
    const salesData = JSON.parse(document.getElementById('salesData').textContent);

    const ctx = document.getElementById('salesChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: salesLabels,
            datasets: [{
                label: 'Sales ($)',
                data: salesData,
                borderColor: '#007bff',
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                fill: true,
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Date'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: 'Sales Amount ($)'
                    },
                    beginAtZero: true
                }
            }
        }
    });
}