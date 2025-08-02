import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './mailPage.css';
import Client from '../services/Client';
import IconButton from '../components/common/IconButton';
import LabelMenu from '../components/labelMenu/LabelMenu';

function MailPage() {
  const [mail, setMail] = useState(null);
  const [error, setError] = useState(null);
  const { mailId } = useParams();
  const [showLabelMenu, setShowLabelMenu] = useState(false);

  const fetchMail = async (id) => {
    try {
      const response = await Client.getMailById(id);
      console.log('Mail data received:', response); // Debug log
      console.log('Mail labels:', response.labels); // Debug labels specifically
      console.log('Is spam?', response.labels && response.labels.includes('spam')); // Debug spam check
      setMail(response);
      setError(null);
    } catch (err) {
      setError(err.message);
      setMail(null);
    }
  };
  const moveToTrash = async () => {
    const previousPath = window.location.pathname.split('/')[1]; // Extract label from URL path
    console.log('Previous path:', previousPath); // Debug log
    if (previousPath === 'bin' || previousPath === 'spam') {
      await Client.deleteMail(mail.id);
    } else {
      await Client.removeLabelFromMail(previousPath, mail.id);
      await Client.addLabelToMail('bin', mail.id);
    }
    window.history.back();
  };

  const toggleLabelMenu = () => {
    setShowLabelMenu(!showLabelMenu);
  };

  const reportSpam = async () => {
    try {
      await Client.addLabelToMail('spam', mail.id);
      await Client.reportSpam({
        mailData: {
          sender: mail.sender, 
          subject: mail.subject, 
          body: mail.body
        }
      });
      // Refresh mail data to show the updated labels
      fetchMail(mail.id);
      alert('Spam reported successfully');
    } catch (error) {
      console.error('Error reporting spam:', error);
      alert('Failed to report spam');
    }
  };
  const markAsUnread = async () => {
    try {
      await Client.addLabelToMail('unread', mail.id);
    } catch (error) {
      console.error('Error marking mail as unread:', error);
      alert('Failed to mark mail as unread');
    }
  };

  useEffect(() => {
    if (mailId) {
      fetchMail(mailId);
    }
  }, [mailId]);

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!mail) {
    return <div className="loading">Loading...</div>;
  }

  // Check if mail is marked as spam
  const isSpam = mail.labels && mail.labels.includes('spam');

  return (
    <div className={`mail-page ${isSpam ? 'spam-mail' : ''}`}>
      <div className="mail-header">
        <div className="mail-header-left">
          <IconButton children="arrow_back" onClick={() => window.history.back()} />
          <IconButton children="report" onClick={reportSpam} />
          <IconButton children="delete" onClick={moveToTrash} />
        </div>
        <div className='mail-header-middle'>
          <IconButton children="mark_email_unread" onClick={markAsUnread} />
          <div className="label-menu-container">
            <IconButton children="label" onClick={toggleLabelMenu} />
            <LabelMenu 
              mail={mail}
              show={showLabelMenu}
              onClose={() => setShowLabelMenu(false)}
            />
          </div>
        </div>
      </div>
      <div className='mail-page-content'>
        {isSpam && (
          <div className="spam-warning">
            <span className="material-symbols-outlined">warning</span>
            <span>This message has been identified as spam</span>
          </div>
        )}
        <h1>{mail.subject || 'No Subject'}</h1>
        <p><strong>From:</strong> {mail.sender || 'Unknown Sender'}</p>
        <p><strong>To:</strong> {mail.receiver || 'Unknown Recipient'}</p>
        <p><strong>Date:</strong> {new Date(mail.dateCreated || mail.date || mail.Date || mail.timestamp).toLocaleString()}</p>
        <div className="mail-body">
          {mail.body || 'No content'}
        </div>
      </div>
    </div>
  );
}

export default MailPage;