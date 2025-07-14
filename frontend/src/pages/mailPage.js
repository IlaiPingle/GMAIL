import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './mailPage.css';

const MailPage = () => {
  const { id } = useParams(); // Get the mail ID from the URL
  const [mail, setMail] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch the mail details by ID
    const fetchMail = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/mails/${id}`, {
          headers: {
            'user-id': '2', // use the correct user ID for Alice
          },
        });
        if (!response.ok) {
          throw new Error('Failed to fetch mail');
        }
        const data = await response.json();
        setMail(data);
      } catch (err) {
        setError(err.message);
      }
    };

    fetchMail();
  }, [id]);

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  if (!mail) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="mail-page">
      <h1>{mail.subject}</h1>
      <p><strong>From:</strong> {mail.sender}</p>
      <p><strong>To:</strong> {mail.receiver}</p>
      <p><strong>Date:</strong> {new Date(mail.dateCreated).toLocaleString()}</p>
      <div className="mail-body">
        {mail.body}
      </div>
    </div>
  );
};

export default MailPage;