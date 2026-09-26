import React, { useState, useEffect } from 'react';
import { fetchHabitStats, fetchHabitLogs } from '../api';
import { FlameIcon, TrophyIcon, TargetIcon, ChartIcon } from './Icons';

/**
 * StatsModal Component - Minimalist Habit Analytics
 *
 * Clean modal overlay displaying:
 * - Habit title and cadence
 * - 4 minimalist KPI metric tiles
 * - Historical check-in log list
 *
 * @param {object} props
 * @param {boolean} props.isOpen - Controls modal visibility.
 * @param {object|null} props.habit - Selected habit object.
 * @param {function} props.onClose - Closes modal.
 */
export default function StatsModal({ isOpen, habit, onClose }) {
  const [stats, setStats] = useState(null);
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch stats and logs when modal opens
  useEffect(() => {
    if (!isOpen || !habit) return;

    let isMounted = true;
    setLoading(true);

    async function loadStatsAndLogs() {
      try {
        const [statsData, logsData] = await Promise.all([
          fetchHabitStats(habit.id || habit.habitId),
          fetchHabitLogs(habit.id || habit.habitId),
        ]);

        if (isMounted) {
          setStats(statsData);
          setLogs(logsData);
        }
      } catch (err) {
        console.error('Failed to load habit stats:', err);
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    loadStatsAndLogs();

    return () => {
      isMounted = false;
    };
  }, [isOpen, habit]);

  if (!isOpen || !habit) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card modal-md" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="modal-header">
          <div>
            <div className="modal-title-row">
              <h3 className="modal-title">{habit.title}</h3>
              <span className="cadence-tag">{habit.frequency.toLowerCase()}</span>
            </div>
            {habit.description && (
              <p className="modal-desc">{habit.description}</p>
            )}
          </div>
          <button type="button" className="btn-close-modal" onClick={onClose}>
            &times;
          </button>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <span>Loading analytics...</span>
          </div>
        ) : (
          <div className="modal-body">
            {/* Minimal Stat Cards */}
            <div className="modal-stats-grid">
              <div className="modal-stat-box">
                <div className="modal-stat-label">
                  <span>Current Streak</span>
                  <FlameIcon size={13} className="streak-flame" />
                </div>
                <div className="modal-stat-val">
                  {stats ? stats.currentStreak : 0} <span className="modal-stat-unit">days</span>
                </div>
              </div>

              <div className="modal-stat-box">
                <div className="modal-stat-label">
                  <span>Longest Streak</span>
                  <TrophyIcon size={13} className="trophy-accent" />
                </div>
                <div className="modal-stat-val">
                  {stats ? stats.longestStreak : 0} <span className="modal-stat-unit">days</span>
                </div>
              </div>

              <div className="modal-stat-box">
                <div className="modal-stat-label">
                  <span>Total Completed</span>
                  <TargetIcon size={13} />
                </div>
                <div className="modal-stat-val">
                  {stats ? stats.totalCompletions : 0} <span className="modal-stat-unit">times</span>
                </div>
              </div>

              <div className="modal-stat-box">
                <div className="modal-stat-label">
                  <span>Success Rate</span>
                  <ChartIcon size={13} />
                </div>
                <div className="modal-stat-val">
                  {stats ? `${stats.completionRate}%` : '0%'}
                </div>
              </div>
            </div>

            {/* Check-In History List */}
            <div className="modal-history-section">
              <h4 className="modal-section-title">Check-in History</h4>
              {logs.length === 0 ? (
                <p className="empty-text">No check-in logs recorded yet.</p>
              ) : (
                <div className="timeline-list">
                  {logs.map((log) => {
                    let badgeClass = 'timeline-badge-done';
                    if (log.status === 'MISSED') badgeClass = 'timeline-badge-missed';
                    if (log.status === 'RECOVERED') badgeClass = 'timeline-badge-recovered';

                    return (
                      <div key={log.id} className="timeline-item">
                        <div className="timeline-left">
                          <span className={`timeline-badge ${badgeClass}`}>{log.status}</span>
                          <span className="timeline-date">{log.date}</span>
                        </div>
                        {log.notes && (
                          <span className="timeline-notes">{log.notes}</span>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
