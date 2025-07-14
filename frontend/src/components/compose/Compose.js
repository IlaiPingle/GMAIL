import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Compose.css';

const Compose = () => {
  const navigate = useNavigate();
  const [receiver, setReceiver] = useState('');
  const [subject, setSubject] = useState('');
  const [body, setBody] = useState('');
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch('http://localhost:8080/api/mails', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'user-id': '2', // Replace with dynamic user ID as needed
        },
        body: JSON.stringify({ receiver, subject, body }),
      });
      if (!res.ok) throw new Error('Failed to send mail');
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="compose-container">
      <div className="compose-header">
        <span>New Message</span>
        <button className="close-btn" onClick={() => navigate('/')}>×</button>
      </div>
      <form className="compose-form" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="To"
          value={receiver}
          onChange={(e) => setReceiver(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Subject"
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
        />
        <textarea
          placeholder="Message"
          value={body}
          onChange={(e) => setBody(e.target.value)}
        />
        {error && <div className="error">{error}</div>}
        <button type="submit" className="send-btn">Send</button>
      </form>
    </div>
  );
};

export default Compose;
