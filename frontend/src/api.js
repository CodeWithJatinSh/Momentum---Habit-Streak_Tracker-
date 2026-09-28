/**
 * api.js - Centralized API Service Module
 *
 * This module handles all HTTP communications between the React frontend
 * and the Spring Boot backend REST endpoints.
 * It manages JWT authentication tokens in localStorage and attaches them
 * to outgoing request headers.
 */

// Key used to store and retrieve the JWT Bearer token in the browser's localStorage
const TOKEN_KEY = 'momentum_token';

// Key used to store basic user metadata (id, username, email)
const USER_KEY = 'momentum_user';

/**
 * Retrieves the currently saved JWT token from localStorage.
 * @returns {string|null} The token string or null if not logged in.
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/**
 * Retrieves the currently saved user object from localStorage.
 * @returns {object|null} The user object or null if not logged in.
 */
export function getCurrentUser() {
  const userStr = localStorage.getItem(USER_KEY);
  try {
    return userStr ? JSON.parse(userStr) : null;
  } catch {
    return null;
  }
}

/**
 * Persists the authentication session (token and user data) to localStorage.
 * @param {string} token - The signed JWT access token.
 * @param {object} user - The user details object (userId, username, email).
 */
export function saveAuthSession(token, user) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

/**
 * Clears the stored session on logout.
 */
export function clearAuthSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

// Base API URL: In local development, defaults to empty string (using Vite dev proxy).
// When deployed on Vercel, set VITE_API_BASE_URL to your backend host (e.g. https://momentum-api.onrender.com).
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/+$/, '');

/**
 * Generic fetch wrapper that automatically:
 * 1. Sets JSON Content-Type headers.
 * 2. Attaches Authorization Bearer token if present.
 * 3. Prepends configured VITE_API_BASE_URL for cross-origin deployments.
 * 4. Catches 401 Unauthorized errors and clears expired sessions.
 * 5. Extracts error messages returned by Spring Boot's GlobalExceptionHandler.
 *
 * @param {string} endpoint - The relative API path (e.g. '/api/dashboard').
 * @param {object} options - Standard fetch options (method, body, headers).
 * @returns {Promise<any>} The parsed JSON response.
 */
export async function apiRequest(endpoint, options = {}) {
  // Prepare headers object with standard JSON type
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  // Check if target is an unauthenticated auth endpoint (login/register)
  const isAuthEndpoint =
    endpoint.includes('/api/auth/login') ||
    endpoint.includes('/api/auth/register');

  // Attach the JWT authorization token only for protected endpoints
  const token = getToken();
  if (token && !isAuthEndpoint) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // Prepend API_BASE_URL if configured and endpoint is a relative path
  const url = endpoint.startsWith('http://') || endpoint.startsWith('https://')
    ? endpoint
    : `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;

  // Execute the HTTP request to the backend
  const response = await fetch(url, {
    ...options,
    headers,
  });

  // Handle 204 No Content (e.g. on DELETE requests)
  if (response.status === 204) {
    return null;
  }

  // Parse the JSON response body safely
  let data = null;
  try {
    data = await response.json();
  } catch {
    // Body is empty or non-JSON
  }

  // Handle 401 Unauthorized
  if (response.status === 401) {
    if (isAuthEndpoint) {
      // Bad credentials or invalid credentials during login/register
      const errorMsg =
        data?.message ||
        (data?.validationErrors
          ? Object.values(data.validationErrors).join(', ')
          : 'Invalid username/email or password');
      throw new Error(errorMsg);
    }

    // Protected endpoint failed because token is expired or revoked
    clearAuthSession();
    // Dispatch a custom event so the UI can redirect or show a login dialog
    window.dispatchEvent(new Event('auth:unauthorized'));
    throw new Error('Session expired. Please sign in again.');
  }

  // If response status is not 2xx, extract the error message and throw
  if (!response.ok) {
    const errorMsg =
      data?.message ||
      (data?.validationErrors ? Object.values(data.validationErrors).join(', ') : 'Request failed');
    throw new Error(errorMsg);
  }

  return data;
}

// ==========================================
// Authentication Endpoints
// ==========================================

/**
 * Logs in an existing user with username/email and password.
 */
export async function loginUser(usernameOrEmail, password) {
  const result = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ usernameOrEmail, password }),
  });
  // Save token and user info to localStorage
  saveAuthSession(result.token, {
    userId: result.userId,
    username: result.username,
    email: result.email,
  });
  return result;
}

/**
 * Registers a new user account.
 */
export async function registerUser(username, email, password) {
  const result = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  });
  // Save token and user info to localStorage
  saveAuthSession(result.token, {
    userId: result.userId,
    username: result.username,
    email: result.email,
  });
  return result;
}

// ==========================================
// Dashboard & Habit Endpoints
// ==========================================

/**
 * Fetches aggregated dashboard metrics, today's checklist, and recent activity.
 */
export async function fetchDashboard() {
  return apiRequest('/api/dashboard');
}

/**
 * Fetches all habits for the logged-in user.
 */
export async function fetchHabits(activeOnly = null) {
  const query = activeOnly !== null ? `?active=${activeOnly}` : '';
  return apiRequest(`/api/habits${query}`);
}


/**
 * Creates a new habit.
 */
export async function createHabit(habitData) {
  return apiRequest('/api/habits', {
    method: 'POST',
    body: JSON.stringify(habitData),
  });
}

/**
 * Updates an existing habit.
 */
export async function updateHabit(id, habitData) {
  return apiRequest(`/api/habits/${id}`, {
    method: 'PUT',
    body: JSON.stringify(habitData),
  });
}

/**
 * Toggles a habit's active/archived state.
 */
export async function toggleHabitStatus(id) {
  return apiRequest(`/api/habits/${id}/toggle-status`, {
    method: 'PATCH',
  });
}

/**
 * Deletes a habit and all associated check-in logs.
 */
export async function deleteHabit(id) {
  return apiRequest(`/api/habits/${id}`, {
    method: 'DELETE',
  });
}

// ==========================================
// Check-In Logs & Analytics Endpoints
// ==========================================

/**
 * Records or updates a daily check-in log for a habit.
 * @param {number} habitId - The ID of the habit.
 * @param {string} date - Date in YYYY-MM-DD format.
 * @param {string} status - 'DONE', 'MISSED', or 'RECOVERED'.
 * @param {string} notes - Optional personal notes.
 */
export async function logHabitCheckIn(habitId, date, status, notes = null) {
  return apiRequest(`/api/habits/${habitId}/logs`, {
    method: 'POST',
    body: JSON.stringify({ date, status, notes }),
  });
}

/**
 * Fetches performance analytics for a single habit.
 */
export async function fetchHabitStats(habitId) {
  return apiRequest(`/api/habits/${habitId}/stats`);
}

/**
 * Fetches check-in history logs for a single habit.
 */
export async function fetchHabitLogs(habitId) {
  return apiRequest(`/api/habits/${habitId}/logs`);
}
