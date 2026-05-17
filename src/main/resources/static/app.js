// ======================================================
// AI Expense Tracker — Shared App Utilities
// ======================================================

const API = 'http://localhost:8080/api';

// ── Session helpers ────────────────────────────────────
const session = {
  get userId()  { return sessionStorage.getItem('userId'); },
  get email()   { return sessionStorage.getItem('email'); },
  get token()   { return sessionStorage.getItem('token'); },
  clear() { sessionStorage.clear(); },
  isLoggedIn() { return !!this.userId; }
};

// ── Guard — redirect to login if not authenticated ─────
function requireAuth() {
  if (!session.isLoggedIn()) {
    window.location.href = '/';
  }
}

// ── Populate sidebar user info + avatar ────────────────
function initSidebar() {
  requireAuth();

  // Email
  const emailEl = document.getElementById('sidebar-email');
  if (emailEl) emailEl.textContent = session.email || 'User';

  // Display name (prefer saved name from profile)
  const nameEl = document.getElementById('sidebar-name');
  if (nameEl) {
    const savedName = localStorage.getItem('displayName');
    nameEl.textContent = savedName || (session.email || 'User').split('@')[0];
  }

  // Avatar — show photo if available, otherwise emoji
  const avatarEl = document.getElementById('sidebar-avatar');
  if (avatarEl) {
    const photo = localStorage.getItem('profilePhoto');
    if (photo) {
      avatarEl.innerHTML =
        `<img src="${photo}" style="width:100%;height:100%;object-fit:cover;border-radius:50%" alt="Avatar"/>`;
    } else {
      avatarEl.textContent = '👤';
    }
  }
}

// ── Logout ─────────────────────────────────────────────
function logout() {
  session.clear();
  // Keep profile photo/name in localStorage (opt-in UX)
  window.location.href = '/';
}

// ── Toast notification ─────────────────────────────────
function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = (type === 'success' ? '✅ ' : '❌ ') + msg;
  t.className = `toast ${type}`;
  t.style.display = 'block';
  setTimeout(() => { t.style.display = 'none'; }, 3500);
}

// ── Format currency ────────────────────────────────────
function fmtCurrency(n) {
  return '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// ── Format date ────────────────────────────────────────
function fmtDate(d) {
  if (!d) return '—';
  return new Date(d).toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' });
}

// ── Category badge class ───────────────────────────────
function categoryBadge(cat) {
  const map = {
    'Food': 'badge-food', 'Dining': 'badge-dining', 'Restaurants': 'badge-dining',
    'Transport': 'badge-transport', 'Shopping': 'badge-shopping',
    'Health': 'badge-health', 'Utilities': 'badge-utilities',
    'Entertainment': 'badge-entertainment', 'Other': 'badge-other'
  };
  const cls = map[cat] || 'badge-other';
  return `<span class="badge ${cls}">${cat}</span>`;
}

// ── Fetch wrapper ──────────────────────────────────────
async function apiFetch(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (session.token) headers['Authorization'] = `Bearer ${session.token}`;
  const res = await fetch(API + path, { headers, ...options });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  return res.json().catch(() => null);
}

// ── Get current month/year ─────────────────────────────
function currentMonth() { return new Date().getMonth() + 1; }
function currentYear()  { return new Date().getFullYear(); }

// ── Month names ────────────────────────────────────────
const MONTHS = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
