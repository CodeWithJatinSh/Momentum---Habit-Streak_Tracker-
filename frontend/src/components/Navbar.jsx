import React from 'react';
import { FlameIcon } from './Icons';

/**
 * Navbar Component - Minimalist Dark Header
 *
 * Clean, distraction-free top navigation bar:
 * - Brand label with clean fire icon
 * - User status and sign in/out links
 *
 * @param {object} props
 * @param {object|null} props.user - Logged in user or null.
 * @param {function} props.onOpenAuth - Callback to open auth dialog.
 * @param {function} props.onLogout - Callback to sign out.
 */
export default function Navbar({ user, onOpenAuth, onLogout, onOpenNewHabit }) {
  return (
    <header className="navbar">
      <div className="nav-container">
        {/* Brand */}
        <div className="brand">
          <div className="brand-icon-pill">
            <FlameIcon size={16} />
          </div>
          <span className="brand-name">momentum</span>
        </div>

        {/* Actions */}
        <div className="nav-actions">
          {user ? (
            <>
              {onOpenNewHabit && (
                <button
                  type="button"
                  className="btn-nav-action-desktop"
                  onClick={onOpenNewHabit}
                  title="Create New Habit"
                >
                  + New Habit
                </button>
              )}
              <div className="user-chip">
                <span className="user-avatar-dot"></span>
                <span className="user-name">{user.username}</span>
                <button
                  type="button"
                  className="btn-nav-link"
                  onClick={onLogout}
                  title="Sign Out"
                  style={{ marginLeft: '0.4rem', padding: '0.15rem 0.5rem' }}
                >
                  Sign out
                </button>
              </div>
            </>
          ) : (
            <>
              <button
                type="button"
                className="btn-nav-link"
                onClick={() => onOpenAuth('login')}
              >
                Sign In
              </button>
              <button
                type="button"
                className="btn-nav-primary"
                onClick={() => onOpenAuth('register')}
              >
                Get Started
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
