import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './LabelMenu.css';
import Client from '../../services/Client';

function LabelMenu({ mail, onClose, show }) {
  const [labels, setLabels] = useState([]);
  const [labelSearchTerm, setLabelSearchTerm] = useState('');
  const [mailLabels, setMailLabels] = useState(mail?.labels || []);
  const navigate = useNavigate();

  // Update local state when mail prop changes
  useEffect(() => {
    setMailLabels(mail?.labels || []);
  }, [mail]);

  const fetchLabels = async () => {
    try {
      const response = await Client.getLabels();
      setLabels(response);
    } catch (err) {
      console.error('Error fetching labels:', err);
    }
  };

  const handleLabelToggle = async (label) => {
    try {
      if (mailLabels.includes(label)) {
        // Remove label if already applied
        await Client.removeLabelFromMail(label, mail.id);
        // Update local state
        const newLabels = mailLabels.filter(l => l !== label);
        setMailLabels(newLabels);
        // Update the original mail object
        mail.labels = newLabels;
      } else {
        // Add label if not applied
        await Client.addLabelToMail(label, mail.id);
        // Update local state
        const newLabels = [...mailLabels, label];
        setMailLabels(newLabels);
        // Update the original mail object
        mail.labels = newLabels;
      }
    } catch (err) {
      console.error('Error toggling label:', err);
    }
  };

  const filteredLabels = labels.filter(label => 
    label.toLowerCase().includes(labelSearchTerm.toLowerCase())
  );

  const handleCreateNewLabel = () => {
    onClose();
    navigate('/create-label');
  };

  useEffect(() => {
    if (show) {
      fetchLabels();
      setLabelSearchTerm(''); // Reset search term when opening
    }
  }, [show]);

  // Refetch labels when window gains focus (useful when returning from create-label page)
  useEffect(() => {
    const handleFocus = () => {
      if (show) {
        fetchLabels();
      }
    };

    window.addEventListener('focus', handleFocus);
    return () => {
      window.removeEventListener('focus', handleFocus);
    };
  }, [show]);

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (show && !event.target.closest('.label-menu-container')) {
        onClose();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [show, onClose]);

  if (!show) {
    return null;
  }

  return (
    <div className="label-dropdown">
      <div className="label-dropdown-header">
        <span>Label as:</span>
        <button className="close-dropdown" onClick={onClose}>×</button>
      </div>
      <div className="label-search">
        <input 
          type="text" 
          placeholder="Search labels" 
          value={labelSearchTerm}
          onChange={(e) => setLabelSearchTerm(e.target.value)}
        />
      </div>
      <div className="label-list">
        {filteredLabels.map((label) => (
          <div 
            key={label} 
            className="label-item"
            onClick={() => handleLabelToggle(label)}
          >
            <input 
              type="checkbox" 
              checked={mailLabels.includes(label)}
              readOnly
            />
            <span>{label}</span>
          </div>
        ))}
        {filteredLabels.length === 0 && (
          <div className="label-item no-labels">
            <span>No labels found</span>
          </div>
        )}
      </div>
      <div className="label-actions">
        <button 
          className="create-new-label"
          onClick={handleCreateNewLabel}
        >
          Create new
        </button>
        <button className="manage-labels">Manage labels</button>
      </div>
    </div>
  );
}

export default LabelMenu;
