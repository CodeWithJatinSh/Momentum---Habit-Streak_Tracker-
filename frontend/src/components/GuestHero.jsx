import React from 'react';
import { FlameIcon, TargetIcon } from './Icons';

/**
 * GuestHero Component - Minimalist Dark Welcome
 *
 * Clean, distraction-free hero welcoming visitors to Momentum:
 * - High contrast dark typography
 * - Wide, responsive preview dashboard showcase
 * - 1-click registration & login buttons
 *
 * @param {object} props
 * @param {function} props.onOpenAuth - Opens authentication modal.
 */
export default function GuestHero({ onOpenAuth }) {
  return (
    <section className="guest-dark-hero">
      {/* Eyebrow Label */}
      <div className="guest-hero-pill">
        <FlameIcon size={14} />
        <span>Momentum Habit Tracker</span>
      </div>

      {/* Main Headline */}
      <h1 className="guest-hero-title">
        Consistency over intensity.
      </h1>

      {/* Subtitle */}
      <p className="guest-hero-sub">
        Track your daily routines, protect your streak momentum, and achieve your goals with a clean, distraction-free interface.
      </p>

      {/* Action Buttons */}
      <div className="guest-hero-ctas">
        <button
          type="button"
          className="btn-primary-dark"
          onClick={() => onOpenAuth('register')}
        >
          Start Tracking Free
        </button>
        <button
          type="button"
          className="btn-secondary-dark"
          onClick={() => onOpenAuth('login')}
        >
          Sign In
        </button>
      </div>

      {/* Feature Highlights Pills */}
      <div className="guest-highlights-row">
        <span className="highlight-pill">✓ 1-Tap Daily Check-in</span>
        <span className="highlight-pill">✓ Visual Streak Compounding</span>
        <span className="highlight-pill">✓ Zero Bloat & Distractions</span>
      </div>

      {/* Wide Live Preview Dashboard Showcase */}
      <div className="guest-preview-box">
        {/* Mock Calendar Strip */}
        <div className="preview-calendar-strip">
          {[
            { day: 'MON', num: '21', dots: 2 },
            { day: 'TUE', num: '22', dots: 2 },
            { day: 'WED', num: '23', dots: 3 },
            { day: 'THU', num: '24', dots: 2 },
            { day: 'FRI', num: '25', dots: 3 },
            { day: 'SAT', num: '26', dots: 3, isToday: true },
            { day: 'SUN', num: '27', dots: 0 },
          ].map((d) => (
            <div
              key={d.day}
              className={`calendar-day-card ${d.isToday ? 'is-today' : ''}`}
            >
              <span className="day-abbr">{d.day}</span>
              <span className="day-num">{d.num}</span>
              <div className="day-dots">
                <span className={`day-dot ${d.dots >= 1 ? 'dot-active' : ''}`}></span>
                <span className={`day-dot ${d.dots >= 2 ? 'dot-active' : ''}`}></span>
                <span className={`day-dot ${d.dots >= 3 ? 'dot-active' : ''}`}></span>
              </div>
            </div>
          ))}
        </div>

        {/* 2-Column Preview Grid */}
        <div className="guest-preview-grid">
          {/* Progress Card Preview */}
          <div className="card-progress-hero" style={{ margin: 0 }}>
            <div className="progress-hero-top">
              <div className="progress-title-block">
                <h3>Today's Routine</h3>
                <p>Simple 1-tap check-ins that protect your daily streak.</p>
              </div>
              <span className="progress-hero-percent">80%</span>
            </div>

            <div className="progress-track-wrap">
              <div className="progress-fill-bar" style={{ width: '80%' }}></div>
            </div>

            <div className="progress-stats-footer">
              <div className="stat-metric-item">
                <span className="stat-val-text color-mint">4 of 5</span>
                <span className="stat-sub-text">habits done</span>
              </div>
              <div className="stat-metric-item">
                <span className="stat-val-text">14d</span>
                <span className="stat-sub-text">active streak</span>
              </div>
              <div className="stat-metric-item">
                <span className="stat-val-text">92%</span>
                <span className="stat-sub-text">7-day average</span>
              </div>
            </div>
          </div>

          {/* Numbers Metric Cards Preview */}
          <div className="preview-numbers-grid">
            {/* Card 1: Active Habit */}
            <div className="number-card" style={{ margin: 0 }}>
              <div>
                <div className="number-card-header">
                  <TargetIcon size={16} className="card-icon-mint" />
                  <span className="card-title-text">Daily Workout</span>
                </div>
                <div className="card-accent-line line-mint"></div>

                <div className="card-val-row">
                  <span className="card-big-num mint-num">14</span>
                  <span className="card-percent-tag">66%</span>
                </div>
                <p className="card-goal-sub">of 21 day goal</p>
              </div>

              <div className="card-sparkline-row">
                <div className="sparkline-text-wrap">
                  <span className="sparkline-label">active days</span>
                  <span className="sparkline-sub">last 7 days</span>
                </div>
                <div className="sparkline-bars">
                  <span className="spark-bar bar-mint" style={{ height: '8px' }}></span>
                  <span className="spark-bar bar-mint" style={{ height: '12px' }}></span>
                  <span className="spark-bar bar-mint" style={{ height: '10px' }}></span>
                  <span className="spark-bar bar-mint" style={{ height: '16px' }}></span>
                  <span className="spark-bar bar-mint" style={{ height: '14px' }}></span>
                  <span className="spark-bar bar-mint" style={{ height: '18px' }}></span>
                </div>
              </div>
            </div>

            {/* Card 2: Momentum Record */}
            <div className="number-card" style={{ margin: 0 }}>
              <div>
                <div className="number-card-header">
                  <FlameIcon size={16} className="card-icon-amber" />
                  <span className="card-title-text">Momentum Record</span>
                </div>
                <div className="card-accent-line line-amber"></div>

                <div className="card-val-row">
                  <span className="card-big-num amber-num">28</span>
                  <span className="card-percent-tag">Peak</span>
                </div>
                <p className="card-goal-sub">best all-time streak</p>
              </div>

              <div className="card-sparkline-row">
                <div className="sparkline-text-wrap">
                  <span className="sparkline-label">peak streak</span>
                  <span className="sparkline-sub">all habits</span>
                </div>
                <div className="sparkline-bars">
                  <span className="spark-bar bar-amber" style={{ height: '6px' }}></span>
                  <span className="spark-bar bar-amber" style={{ height: '10px' }}></span>
                  <span className="spark-bar bar-amber" style={{ height: '12px' }}></span>
                  <span className="spark-bar bar-amber" style={{ height: '15px' }}></span>
                  <span className="spark-bar bar-amber" style={{ height: '18px' }}></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
