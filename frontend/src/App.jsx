import React, { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import GuestHero from './components/GuestHero';
import Dashboard from './components/Dashboard';
import AuthModal from './components/AuthModal';
import HabitModal from './components/HabitModal';
import StatsModal from './components/StatsModal';
import { getCurrentUser, clearAuthSession } from './api';

/**
 * App Component - Root Application Shell
 *
 * Lightweight, minimalist dark habit streak tracker:
 * 1. Global Authentication State.
 * 2. Clean Dialogs (Auth, Habit Creation/Edit, Stats).
 * 3. High-performance toast alerts.
 */
export default function App() {
  const [user, setUser] = useState(getCurrentUser());
  const [authModalState, setAuthModalState] = useState({ isOpen: false, tab: 'login' });
  const [habitModalState, setHabitModalState] = useState({ isOpen: false, habitToEdit: null });
  const [statsModalState, setStatsModalState] = useState({ isOpen: false, habit: null });
  const [toasts, setToasts] = useState([]);
  const [refreshKey, setRefreshKey] = useState(0);

  /**
   * Displays temporary feedback notification
   */
  const showToast = (message, type = 'success') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3200);
  };

  /**
   * Listen for session expiration
   */
  useEffect(() => {
    const handleUnauthorized = () => {
      setUser(null);
      showToast('Session expired. Please sign in.', 'error');
      setAuthModalState({ isOpen: true, tab: 'login' });
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('auth:unauthorized', handleUnauthorized);
    };
  }, []);

  const handleAuthSuccess = (authData) => {
    const newUser = {
      id: authData.userId,
      username: authData.username,
      email: authData.email,
    };
    setUser(newUser);
    setAuthModalState({ isOpen: false, tab: 'login' });
    showToast(`Welcome back, ${newUser.username}!`);
  };

  const handleLogout = () => {
    clearAuthSession();
    setUser(null);
    showToast('Signed out.', 'info');
  };

  return (
    <div className="app-container">
      {/* Top Navbar */}
      <Navbar
        user={user}
        onOpenAuth={(tab) => setAuthModalState({ isOpen: true, tab })}
        onLogout={handleLogout}
        onOpenNewHabit={() => setHabitModalState({ isOpen: true, habitToEdit: null })}
      />

      {/* Main Content */}
      <main className="main-content">
        {user ? (
          <Dashboard
            key={refreshKey}
            user={user}
            onOpenNewHabit={() => setHabitModalState({ isOpen: true, habitToEdit: null })}
            onOpenEditHabit={(habit) => setHabitModalState({ isOpen: true, habitToEdit: habit })}
            onOpenStats={(habit) => setStatsModalState({ isOpen: true, habit })}
            showToast={showToast}
          />
        ) : (
          <GuestHero
            onOpenAuth={(tab) => setAuthModalState({ isOpen: true, tab })}
          />
        )}
      </main>

      {/* ==================== Modals ==================== */}
      <AuthModal
        isOpen={authModalState.isOpen}
        initialTab={authModalState.tab}
        onClose={() => setAuthModalState({ isOpen: false, tab: 'login' })}
        onAuthSuccess={handleAuthSuccess}
      />

      <HabitModal
        isOpen={habitModalState.isOpen}
        habitToEdit={habitModalState.habitToEdit}
        onClose={() => setHabitModalState({ isOpen: false, habitToEdit: null })}
        onSaved={() => {
          setRefreshKey((k) => k + 1);
          showToast('Habit saved successfully!');
        }}
      />

      <StatsModal
        isOpen={statsModalState.isOpen}
        habit={statsModalState.habit}
        onClose={() => setStatsModalState({ isOpen: false, habit: null })}
      />

      {/* Toast Feedback */}
      <div className="toast-container">
        {toasts.map((t) => (
          <div key={t.id} className="toast">
            <span>{t.type === 'success' ? '✓' : 'ℹ'}</span>
            <span>{t.message}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
