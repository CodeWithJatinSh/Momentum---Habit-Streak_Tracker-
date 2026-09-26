import React, { useState, useEffect } from 'react';
import { createHabit, updateHabit } from '../api';

/**
 * HabitModal Component - Minimalist Habit Creation & Editing
 *
 * Clean dialog for defining:
 * - Habit Title
 * - Optional Description
 * - Cadence Frequency (DAILY, WEEKLY, MONTHLY)
 *
 * @param {object} props
 * @param {boolean} props.isOpen - Controls modal visibility.
 * @param {object|null} props.habitToEdit - Habit object if editing, or null if creating.
 * @param {function} props.onClose - Closes modal.
 * @param {function} props.onSaved - Callback on successful save.
 */
export default function HabitModal({ isOpen, habitToEdit, onClose, onSaved }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [frequency, setFrequency] = useState('DAILY');
  const [errorMessage, setErrorMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (habitToEdit) {
      setTitle(habitToEdit.title || '');
      setDescription(habitToEdit.description || '');
      setFrequency(habitToEdit.frequency || 'DAILY');
    } else {
      setTitle('');
      setDescription('');
      setFrequency('DAILY');
    }
    setErrorMessage('');
  }, [habitToEdit, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setIsSubmitting(true);

    const payload = {
      title: title.trim(),
      description: description.trim() || null,
      frequency,
    };

    try {
      if (habitToEdit && habitToEdit.id) {
        await updateHabit(habitToEdit.id, payload);
      } else {
        await createHabit(payload);
      }
      onSaved();
      onClose();
    } catch (err) {
      setErrorMessage(err.message || 'Failed to save habit');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-card modal-sm" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="modal-header">
          <div>
            <h3 className="modal-title">
              {habitToEdit ? 'Edit Habit' : 'New Habit'}
            </h3>
            <p className="modal-desc">
              Define the routine you want to track consistently.
            </p>
          </div>
          <button type="button" className="btn-close-modal" onClick={onClose}>
            &times;
          </button>
        </div>

        {errorMessage && <div className="form-error-banner">{errorMessage}</div>}

        <form onSubmit={handleSubmit} className="modal-form">
          <div className="form-group">
            <label className="form-label" htmlFor="habitTitle">
              Habit Name *
            </label>
            <input
              id="habitTitle"
              type="text"
              className="form-input"
              placeholder="e.g. Read 20 pages, Morning Run"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              maxLength={100}
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="habitDesc">
              Description (Optional)
            </label>
            <textarea
              id="habitDesc"
              className="form-input form-textarea"
              placeholder="Cue, motivation, or notes..."
              rows={2}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              maxLength={500}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Cadence *</label>
            <div className="cadence-selector">
              {['DAILY', 'WEEKLY', 'MONTHLY'].map((freq) => (
                <button
                  key={freq}
                  type="button"
                  className={`cadence-pill ${frequency === freq ? 'active' : ''}`}
                  onClick={() => setFrequency(freq)}
                >
                  {freq.charAt(0) + freq.slice(1).toLowerCase()}
                </button>
              ))}
            </div>
          </div>

          <div className="modal-actions">
            <button
              type="button"
              className="btn-cancel"
              onClick={onClose}
              disabled={isSubmitting}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-submit"
              disabled={isSubmitting || !title.trim()}
            >
              {isSubmitting ? 'Saving...' : habitToEdit ? 'Save Changes' : 'Create Habit'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
