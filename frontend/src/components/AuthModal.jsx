import React, { useState } from 'react';
import { loginUser, registerUser } from '../api';

/**
 * AuthModal Component - Minimalist Authentication
 *
 * Clean dialog for sign in / registration:
 * - Segmented tabs for Sign In & Create Account
 * - Clean inputs with subtle focus states
 * - Error alert handling
 *
 * @param {object} props
 * @param {boolean} props.isOpen - Controls modal visibility.
 * @param {string} props.initialTab - 'login' or 'register'.
 * @param {function} props.onClose - Dismisses modal.
 * @param {function} props.onAuthSuccess - Callback on authentication success.
 */
export default function AuthModal({ isOpen, initialTab = 'login', onClose, onAuthSuccess }) {
  const [activeTab, setActiveTab] = useState(initialTab);
  const [loginIdentifier, setLoginIdentifier] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [regUsername, setRegUsername] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  React.useEffect(() => {
    if (isOpen) {
      setActiveTab(initialTab);
      setErrorMessage('');
    }
  }, [isOpen, initialTab]);

  if (!isOpen) return null;

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setIsLoading(true);

    try {
      const result = await loginUser(loginIdentifier.trim(), loginPassword);
      onAuthSuccess(result);
    } catch (err) {
      setErrorMessage(err.message || 'Login failed. Check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setIsLoading(true);

    try {
      const result = await registerUser(regUsername.trim(), regEmail.trim(), regPassword);
      onAuthSuccess(result);
    } catch (err) {
      setErrorMessage(err.message || 'Registration failed.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card modal-sm" onClick={(e) => e.stopPropagation()}>
        {/* Header with Tab Switcher */}
        <div className="modal-header">
          <div className="auth-tab-switch">
            <button
              type="button"
              className={`auth-tab-pill ${activeTab === 'login' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('login');
                setErrorMessage('');
              }}
            >
              Sign In
            </button>
            <button
              type="button"
              className={`auth-tab-pill ${activeTab === 'register' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('register');
                setErrorMessage('');
              }}
            >
              Create Account
            </button>
          </div>

          <button type="button" className="btn-close-modal" onClick={onClose}>
            &times;
          </button>
        </div>

        {errorMessage && <div className="form-error-banner">{errorMessage}</div>}

        {/* Login Form */}
        {activeTab === 'login' && (
          <form onSubmit={handleLoginSubmit} className="modal-form">
            <div className="form-group">
              <label className="form-label" htmlFor="loginIdInput">
                Username or Email
              </label>
              <input
                id="loginIdInput"
                type="text"
                className="form-input"
                placeholder="alex or alex@example.com"
                value={loginIdentifier}
                onChange={(e) => setLoginIdentifier(e.target.value)}
                required
                autoComplete="username"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="loginPassInput">
                Password
              </label>
              <input
                id="loginPassInput"
                type="password"
                className="form-input"
                placeholder="Enter password"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
            </div>

            <div className="modal-actions">
              <button
                type="submit"
                className="btn-submit btn-block"
                disabled={isLoading}
              >
                {isLoading ? 'Signing In...' : 'Sign In'}
              </button>
            </div>
          </form>
        )}

        {/* Register Form */}
        {activeTab === 'register' && (
          <form onSubmit={handleRegisterSubmit} className="modal-form">
            <div className="form-group">
              <label className="form-label" htmlFor="regUserInput">
                Username
              </label>
              <input
                id="regUserInput"
                type="text"
                className="form-input"
                placeholder="Choose username"
                value={regUsername}
                onChange={(e) => setRegUsername(e.target.value)}
                required
                minLength={3}
                maxLength={30}
                autoFocus
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="regEmailInput">
                Email
              </label>
              <input
                id="regEmailInput"
                type="email"
                className="form-input"
                placeholder="alex@example.com"
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="regPassInput">
                Password
              </label>
              <input
                id="regPassInput"
                type="password"
                className="form-input"
                placeholder="At least 8 characters"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
                required
                minLength={8}
              />
            </div>

            <div className="modal-actions">
              <button
                type="submit"
                className="btn-submit btn-block"
                disabled={isLoading}
              >
                {isLoading ? 'Creating Account...' : 'Create Account'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
