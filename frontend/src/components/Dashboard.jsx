import React, { useState, useEffect, useCallback, useMemo } from 'react';
import {
  fetchDashboard,
  fetchHabits,
  logHabitCheckIn,
  deleteHabit,
} from '../api';
import {
  FlameIcon,
  TargetIcon,
  PlusIcon,
  CheckIcon,
  ChartIcon,
  EditIcon,
  TrashIcon,
} from './Icons';

/**
 * Dashboard Component - Minimalist Dark Habit Architecture
 *
 * Faithfully matches the user's reference design with responsive desktop layout:
 * - "Good afternoon, {username}" personal header
 * - Plan label: "Habit streak plan: daily consistency & streak focus"
 * - "Today: {Date}" with interactive 7-day horizontal calendar strip
 * - "Progress: 13%" hero card with mint bar & stats
 * - "Numbers" 2-column metric cards with mini sparklines
 * - Clean habits checklist with 1-tap completion stamps
 * - Responsive 2-column grid on desktop, 1-column on mobile
 * - Sleek frosted floating bottom dock on mobile
 *
 * @param {object} props
 * @param {object|null} props.user - Authenticated user details.
 * @param {function} props.onOpenNewHabit - Opens habit creation modal.
 * @param {function} props.onOpenEditHabit - Opens habit edit modal.
 * @param {function} props.onOpenStats - Opens habit analytics modal.
 * @param {function} props.showToast - Toast feedback callback.
 */
export default function Dashboard({
  user,
  onOpenNewHabit,
  onOpenEditHabit,
  onOpenStats,
  showToast,
}) {
  const [dashboardData, setDashboardData] = useState(null);
  const [allHabits, setAllHabits] = useState([]);
  const [filter, setFilter] = useState('all'); // 'all' | 'pending' | 'completed'
  const [selectedDayOffset, setSelectedDayOffset] = useState(0); // 0 = today
  const [isLoading, setIsLoading] = useState(true);

  /**
   * Refreshes metrics from backend API.
   */
  const loadData = useCallback(async () => {
    try {
      const [dash, habits] = await Promise.all([
        fetchDashboard(),
        fetchHabits(),
      ]);
      setDashboardData(dash);
      setAllHabits(habits || []);
    } catch (err) {
      showToast(err.message || 'Failed to load habit data', 'error');
    } finally {
      setIsLoading(false);
    }
  }, [showToast]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  /**
   * Generates dynamic 7-day calendar strip (past 5 days, today, tomorrow)
   */
  const calendarDays = useMemo(() => {
    const days = [];
    const today = new Date();

    for (let i = -5; i <= 1; i++) {
      const d = new Date(today);
      d.setDate(today.getDate() + i);

      const dayAbbr = d.toLocaleDateString('en-US', { weekday: 'short' }).toUpperCase();
      const dayNum = d.getDate();
      const isToday = i === 0;

      days.push({
        offset: i,
        dayAbbr,
        dayNum,
        isToday,
        dateStr: d.toISOString().split('T')[0],
      });
    }
    return days;
  }, []);

  /**
   * Determines time-based greeting
   */
  const greeting = useMemo(() => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning,';
    if (hour < 18) return 'Good afternoon,';
    return 'Good evening,';
  }, []);

  /**
   * Formatted today's date: "March 25, 2026"
   */
  const todayFormatted = useMemo(() => {
    return new Date().toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric',
    });
  }, []);

  /**
   * Handles 1-tap check-in on a habit
   */
  const handleCheckIn = async (habitId, currentStatus) => {
    const todayStr = new Date().toISOString().split('T')[0];
    const newStatus = currentStatus === 'DONE' ? 'MISSED' : 'DONE';

    try {
      await logHabitCheckIn(habitId, todayStr, newStatus);
      showToast(newStatus === 'DONE' ? 'Habit completed! Streak compounding.' : 'Marked as missed', 'success');
      loadData();
    } catch (err) {
      showToast(err.message || 'Action failed', 'error');
    }
  };

  /**
   * Deletes habit with confirmation
   */
  const handleDelete = async (habitId) => {
    if (!window.confirm('Delete this habit and all its logs?')) return;
    try {
      await deleteHabit(habitId);
      showToast('Habit deleted', 'info');
      loadData();
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  if (isLoading && !dashboardData) {
    return (
      <div className="dark-dashboard">
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginTop: '4rem' }}>
          Loading your momentum...
        </p>
      </div>
    );
  }

  // Filter habits for the list
  const filteredHabits = (dashboardData?.todayHabits || []).filter((h) => {
    if (filter === 'pending') return h.todayStatus === 'PENDING';
    if (filter === 'completed') return h.todayStatus === 'DONE' || h.todayStatus === 'RECOVERED';
    return true;
  });

  const completionPercent = dashboardData?.todayCompletionRate || 0;
  const topHabit = allHabits.length > 0
    ? [...allHabits].sort((a, b) => b.currentStreak - a.currentStreak)[0]
    : null;

  return (
    <div className="dark-dashboard">
      {/* ==================== 1. Greeting Header ==================== */}
      <header className="dashboard-hero-header">
        <div className="hero-header-left">
          <span className="hero-greeting-sub">{greeting}</span>
          <h1 className="hero-user-name">
            {user?.username || 'Dmitry'}
          </h1>
          <div className="hero-plan-badge">
            <span className="plan-label">Habit streak plan:</span>
            <span className="plan-desc">daily consistency with streak focus</span>
          </div>
        </div>

        <div className="hero-header-right">
          <div className="today-header-date">
            <span className="today-label-small">Today</span>
            <span className="today-date-text">{todayFormatted}</span>
          </div>

          <button
            type="button"
            className="btn-header-add"
            onClick={onOpenNewHabit}
          >
            <PlusIcon size={14} />
            <span>New Habit</span>
          </button>

          <button
            type="button"
            className="btn-pulse-action"
            onClick={onOpenNewHabit}
            title="Create New Habit"
          >
            <FlameIcon size={18} />
          </button>
        </div>
      </header>

      {/* ==================== 2. 7-Day Calendar Strip ==================== */}
      <section className="calendar-strip-section">
        <div className="calendar-strip">
          {calendarDays.map((d) => (
            <div
              key={d.offset}
              className={`calendar-day-card ${d.isToday ? 'is-today' : ''} ${selectedDayOffset === d.offset ? 'selected-day' : ''}`}
              onClick={() => setSelectedDayOffset(d.offset)}
            >
              <span className="day-abbr">{d.dayAbbr}</span>
              <span className="day-num">{d.dayNum}</span>
              <div className="day-dots">
                <span className={`day-dot ${d.isToday && completionPercent > 0 ? 'dot-active' : ''}`}></span>
                <span className={`day-dot ${d.isToday && completionPercent >= 50 ? 'dot-active' : ''}`}></span>
                <span className={`day-dot ${d.isToday && completionPercent === 100 ? 'dot-active' : ''}`}></span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ==================== 3. Responsive 2-Column Grid ==================== */}
      <div className="dashboard-grid">
        {/* Left Column (Main): Progress Hero & Habits Checklist */}
        <div className="dashboard-main-col">
          {/* Big Progress Hero Card */}
          <section className="card-progress-hero">
            <div className="progress-hero-top">
              <div className="progress-title-block">
                <h3>Progress:</h3>
                <p>Score based on habits, streak velocity, and daily check-ins.</p>
              </div>
              <span className="progress-hero-percent">{completionPercent}%</span>
            </div>

            {/* Progress Track */}
            <div className="progress-track-wrap">
              <div
                className="progress-fill-bar"
                style={{ width: `${Math.min(100, Math.max(0, completionPercent))}%` }}
              ></div>
            </div>

            {/* Footer Metrics */}
            <div className="progress-stats-footer">
              <div className="stat-metric-item">
                <span className="stat-val-text color-mint">{completionPercent}%</span>
                <span className="stat-sub-text">completed today</span>
              </div>
              <div className="stat-metric-item">
                <span className="stat-val-text">
                  {dashboardData?.completedToday || 0} / {dashboardData?.activeHabits || 0}
                </span>
                <span className="stat-sub-text">habits logged</span>
              </div>
              <div className="stat-metric-item">
                <span className="stat-val-text">
                  {dashboardData?.longestActiveStreak || 0}d
                </span>
                <span className="stat-sub-text">longest streak</span>
              </div>
            </div>
          </section>

          {/* Habits Daily Checklist */}
          <section className="habits-section">
            <div className="section-heading-habits">
              <div className="heading-title-wrap">
                <h3>Habits</h3>
                <span className="habits-counter-pill">
                  {dashboardData?.completedToday || 0} of {dashboardData?.activeHabits || 0}
                </span>
              </div>

              <div className="habits-filter-tabs">
                {['all', 'pending', 'completed'].map((f) => (
                  <button
                    key={f}
                    type="button"
                    className={`filter-chip ${filter === f ? 'active' : ''}`}
                    onClick={() => setFilter(f)}
                  >
                    {f.charAt(0).toUpperCase() + f.slice(1)}
                  </button>
                ))}
              </div>
            </div>

            {filteredHabits.length === 0 ? (
              <div className="empty-habits-panel">
                <h4 className="empty-title">No habits to show</h4>
                <p className="empty-sub">
                  {allHabits.length === 0
                    ? 'Create your first daily habit to start building momentum.'
                    : 'All habits completed for this filter!'}
                </p>
                <button
                  type="button"
                  className="btn-primary-dark"
                  onClick={onOpenNewHabit}
                >
                  + Add a Habit
                </button>
              </div>
            ) : (
              <div className="habits-dark-list">
                {filteredHabits.map((item) => {
                  const isDone = item.todayStatus === 'DONE';
                  const isRecovered = item.todayStatus === 'RECOVERED';
                  const isCompleted = isDone || isRecovered;

                  return (
                    <div
                      key={item.habitId}
                      className={`habit-dark-card ${isCompleted ? 'is-completed' : ''}`}
                    >
                      <div className="habit-left-group">
                        {/* Clean Check Stamp */}
                        <button
                          type="button"
                          className={`btn-check-stamp ${isDone ? 'stamp-active' : ''}`}
                          onClick={() => handleCheckIn(item.habitId, item.todayStatus)}
                          title={isDone ? 'Mark as missed' : 'Complete today'}
                        >
                          {isDone && <CheckIcon size={14} />}
                        </button>

                        <div className="habit-details-wrap">
                          <div className="habit-title-line">
                            <span
                              className="habit-name-text"
                              onClick={() => onOpenStats(item)}
                              title="View stats"
                            >
                              {item.title}
                            </span>
                            <span className="cadence-subtle-tag">
                              {item.frequency.toLowerCase()}
                            </span>
                          </div>
                          {item.notes && (
                            <span className="habit-desc-text">{item.notes}</span>
                          )}
                        </div>
                      </div>

                      <div className="habit-right-group">
                        <div className="streak-pill-dark">
                          <FlameIcon size={13} />
                          <span>{item.currentStreak}d</span>
                        </div>

                        <div className="habit-row-actions">
                          <button
                            type="button"
                            className="btn-row-action"
                            onClick={() => onOpenStats(item)}
                            title="View Analytics"
                          >
                            <ChartIcon size={13} />
                          </button>
                          <button
                            type="button"
                            className="btn-row-action"
                            onClick={() => onOpenEditHabit(item)}
                            title="Edit Habit"
                          >
                            <EditIcon size={13} />
                          </button>
                          <button
                            type="button"
                            className="btn-row-action danger"
                            onClick={() => handleDelete(item.habitId)}
                            title="Delete Habit"
                          >
                            <TrashIcon size={13} />
                          </button>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </section>
        </div>

        {/* Right Column (Sidebar): Numbers & Insights */}
        <aside className="dashboard-side-col">
          <div className="sidebar-sticky-wrap">
            <h3 className="section-heading-numbers">Numbers & Insights</h3>

            <div className="numbers-grid">
              {/* Card 1: Active Habit / Streak Velocity */}
              <div className="number-card">
                <div>
                  <div className="number-card-header">
                    <TargetIcon size={16} className="card-icon-mint" />
                    <span className="card-title-text">
                      {topHabit ? topHabit.title : 'Active Streak'}
                    </span>
                  </div>
                  <div className="card-accent-line line-mint"></div>

                  <div className="card-val-row">
                    <span className="card-big-num mint-num">
                      {topHabit ? topHabit.currentStreak : dashboardData?.longestActiveStreak || 0}
                    </span>
                    <span className="card-percent-tag">
                      {topHabit ? `${Math.min(100, topHabit.currentStreak * 10)}%` : '25%'}
                    </span>
                  </div>
                  <p className="card-goal-sub">
                    of {topHabit?.longestStreak ? `${topHabit.longestStreak}d record` : '21 day goal'}
                  </p>
                </div>

                <div className="card-sparkline-row">
                  <div className="sparkline-text-wrap">
                    <span className="sparkline-label">active days</span>
                    <span className="sparkline-sub">last 7 days</span>
                  </div>

                  {/* Sparkline Bar Chart Graphic */}
                  <div className="sparkline-bars">
                    <span className="spark-bar bar-mint" style={{ height: '6px' }}></span>
                    <span className="spark-bar bar-mint" style={{ height: '10px' }}></span>
                    <span className="spark-bar bar-mint" style={{ height: '8px' }}></span>
                    <span className="spark-bar bar-mint" style={{ height: '14px' }}></span>
                    <span className="spark-bar bar-mint" style={{ height: '12px' }}></span>
                    <span className="spark-bar bar-mint" style={{ height: '18px' }}></span>
                  </div>
                </div>
              </div>

              {/* Card 2: Total Completions / Momentum */}
              <div className="number-card">
                <div>
                  <div className="number-card-header">
                    <FlameIcon size={16} className="card-icon-amber" />
                    <span className="card-title-text">Momentum</span>
                  </div>
                  <div className="card-accent-line line-amber"></div>

                  <div className="card-val-row">
                    <span className="card-big-num amber-num">
                      {dashboardData?.bestAllTimeStreak || 0}
                    </span>
                    <span className="card-percent-tag">
                      {dashboardData?.activeHabits ? `${dashboardData.activeHabits} active` : '13%'}
                    </span>
                  </div>
                  <p className="card-goal-sub">
                    best all-time record
                  </p>
                </div>

                <div className="card-sparkline-row">
                  <div className="sparkline-text-wrap">
                    <span className="sparkline-label">peak streak</span>
                    <span className="sparkline-sub">all habits</span>
                  </div>

                  {/* Sparkline Bar Chart Graphic */}
                  <div className="sparkline-bars">
                    <span className="spark-bar bar-amber" style={{ height: '4px' }}></span>
                    <span className="spark-bar bar-amber" style={{ height: '6px' }}></span>
                    <span className="spark-bar bar-amber" style={{ height: '9px' }}></span>
                    <span className="spark-bar bar-amber" style={{ height: '13px' }}></span>
                    <span className="spark-bar bar-amber" style={{ height: '16px' }}></span>
                  </div>
                </div>
              </div>
            </div>

            {/* Consistency & Compounding Card */}
            <div className="consistency-card">
              <div className="consistency-header">
                <div className="consistency-icon-wrap">
                  <FlameIcon size={16} />
                </div>
                <div>
                  <h4 className="consistency-title">Streak Compounding</h4>
                  <span className="consistency-sub">Consistency over intensity</span>
                </div>
              </div>
              <p className="consistency-body">
                {completionPercent === 100
                  ? '🔥 All scheduled habits completed today! Your momentum is compounding.'
                  : completionPercent > 0
                  ? `⚡ ${completionPercent}% complete today. Check off remaining habits to maintain your daily streak.`
                  : '🌱 Ready to build momentum? Check off your first habit to keep the streak alive.'}
              </p>
              <div className="consistency-meta">
                <div className="meta-stat">
                  <span className="meta-val">{allHabits.length}</span>
                  <span className="meta-lbl">Total Habits</span>
                </div>
                <div className="meta-stat">
                  <span className="meta-val">{dashboardData?.bestAllTimeStreak || 0}d</span>
                  <span className="meta-lbl">Peak Streak</span>
                </div>
                <div className="meta-stat">
                  <span className="meta-val">{dashboardData?.longestActiveStreak || 0}d</span>
                  <span className="meta-lbl">Active Best</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>

      {/* ==================== 4. Frosted Floating Bottom Dock (Mobile Friendly) ==================== */}
      <aside className="frosted-bottom-dock">
        <div className="dock-pill-bar">
          <button type="button" className="dock-item-btn active">
            <span style={{ fontSize: '1rem' }}>🏠</span>
            <span>Today</span>
          </button>

          <button
            type="button"
            className="btn-dock-add"
            onClick={onOpenNewHabit}
            title="Create New Habit"
          >
            <PlusIcon size={16} />
          </button>

          <button
            type="button"
            className="dock-item-btn"
            onClick={() => onOpenStats(allHabits[0] || null)}
            title="Analytics"
          >
            <span style={{ fontSize: '1rem' }}>📊</span>
            <span>Stats</span>
          </button>
        </div>
      </aside>
    </div>
  );
}
