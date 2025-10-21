// progress.js - Handles Chart.js rendering for exercise progress

document.addEventListener('DOMContentLoaded', function() {
    // Only render chart if we have progress data
    if (typeof window.progressData === 'undefined' || window.progressData === null) {
        console.log('No progress data available to chart');
        return;
    }

    const data = window.progressData;
    console.log('Progress data loaded:', data);

    // Check if we have data points
    if (!data.dataPoints || data.dataPoints.length === 0) {
        console.log('No data points to display');
        const canvas = document.getElementById('progressChart');
        if (canvas) {
            const ctx = canvas.getContext('2d');
            ctx.font = '20px Arial';
            ctx.fillStyle = '#6c757d';
            ctx.textAlign = 'center';
            ctx.fillText('No workout data found for this exercise in the selected time range',
                canvas.width / 2, canvas.height / 2);
        }
        return;
    }

    // Prepare data for Chart.js - convert dates to JavaScript Date objects
    const chartData = data.dataPoints.map(point => {
        console.log('Processing point:', point);
        return {
            x: new Date(point.date),
            y: parseFloat(point.weight),
            reps: point.reps,
            volume: parseFloat(point.volume)
        };
    });

    console.log('Chart data prepared:', chartData);

    // Get canvas context
    const ctx = document.getElementById('progressChart');
    if (!ctx) {
        console.error('Canvas element not found');
        return;
    }

    // Create the chart
    const chart = new Chart(ctx, {
        type: 'scatter',
        data: {
            datasets: [{
                label: 'Weight (lbs)',
                data: chartData,
                backgroundColor: 'rgba(54, 162, 235, 0.8)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 2,
                pointRadius: 8,
                pointHoverRadius: 10,
                showLine: true,
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            aspectRatio: 2.5,
            plugins: {
                title: {
                    display: false
                },
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14,
                        weight: 'bold'
                    },
                    bodyFont: {
                        size: 13
                    },
                    callbacks: {
                        title: function(context) {
                            const date = context[0].parsed.x;
                            return new Date(date).toLocaleDateString('en-US', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric'
                            });
                        },
                        label: function(context) {
                            const point = context.raw;
                            return [
                                `Weight: ${point.y} lbs`,
                                `Reps: ${point.reps}`,
                                `Volume: ${point.volume.toFixed(0)} lbs`
                            ];
                        }
                    }
                }
            },
            scales: {
                x: {
                    type: 'time',
                    time: {
                        unit: 'day',
                        displayFormats: {
                            day: 'MMM dd',
                            week: 'MMM dd',
                            month: 'MMM yyyy'
                        }
                    },
                    title: {
                        display: true,
                        text: 'Date',
                        font: {
                            size: 14,
                            weight: 'bold'
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 0, 0, 0.1)'
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: 'Weight (lbs)',
                        font: {
                            size: 14,
                            weight: 'bold'
                        }
                    },
                    grid: {
                        display: true,
                        color: 'rgba(0, 0, 0, 0.1)'
                    },
                    ticks: {
                        callback: function(value) {
                            return value + ' lbs';
                        }
                    }
                }
            },
            interaction: {
                mode: 'nearest',
                intersect: true
            }
        }
    });

    console.log('Chart created:', chart);
    console.log('Chart rendered successfully');
});