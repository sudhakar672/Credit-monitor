// Dashboard charts — runs after Thymeleaf inlines npaLabels/npaData/expXxx

document.addEventListener('DOMContentLoaded', () => {

  // ── Stacked Bar — Exposure by Risk Band ──────────────────────
  const ctxExp = document.getElementById('chartExposure');
  if (ctxExp) {
    new Chart(ctxExp, {
      type: 'bar',
      data: {
        labels: expLabels,
        datasets: [
          { label: 'Prime',      data: expPrime, backgroundColor: '#C8842A', stack: 's' },
          { label: 'Near-prime', data: expNear,  backgroundColor: '#EF9F27', stack: 's' },
          { label: 'Subprime',   data: expSub,   backgroundColor: '#534AB7', stack: 's' },
          { label: 'High Risk',  data: expHigh,  backgroundColor: '#A32D2D', stack: 's' }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { mode: 'index' }
        },
        scales: {
          x: {
            stacked: true,
            grid: { display: false },
            ticks: { font: { size: 11 }, color: '#999', autoSkip: false, maxRotation: 0 }
          },
          y: {
            stacked: true,
            grid: { color: 'rgba(0,0,0,0.05)' },
            ticks: {
              font: { size: 11 }, color: '#999',
              callback: v => '₹' + (v / 1000).toFixed(0) + 'K Cr'
            }
          }
        }
      }
    });
  }

  // ── Line Chart — NPA Trend ────────────────────────────────────
  const ctxNpa = document.getElementById('chartNpa');
  if (ctxNpa && typeof npaLabels !== 'undefined') {
    new Chart(ctxNpa, {
      type: 'line',
      data: {
        labels: npaLabels,
        datasets: [{
          label: 'NPA %',
          data: npaData,
          borderColor: '#A32D2D',
          backgroundColor: 'rgba(163,45,45,0.07)',
          borderWidth: 2,
          pointRadius: 3,
          pointBackgroundColor: '#A32D2D',
          fill: true,
          tension: 0.35
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: {
            grid: { display: false },
            ticks: { font: { size: 11 }, color: '#999', autoSkip: false, maxRotation: 0 }
          },
          y: {
            min: 1.7, max: 2.7,
            grid: { color: 'rgba(0,0,0,0.05)' },
            ticks: {
              font: { size: 11 }, color: '#999',
              callback: v => v.toFixed(2) + '%'
            }
          }
        }
      }
    });
  }
});
