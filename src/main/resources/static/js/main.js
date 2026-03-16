// Live clock
function updateClock() {
  const now = new Date();
  const el = document.getElementById('clock');
  if (el) el.textContent = now.toLocaleTimeString('en-IN', {
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  });
}
updateClock();
setInterval(updateClock, 1000);
