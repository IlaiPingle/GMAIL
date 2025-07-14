import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from 'react-router-dom';
import MailItem from "../components/mail/mail";
import Compose from '../components/compose/Compose';
import CreateLabel from '../components/createLabel/CreateLabel';
import "./InboxPage.css";

export default function InboxPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const showCompose = location.state?.compose;
  const showCreateLabel = location.state?.newLabel;

  const [mails, setMails] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log("Attempting to fetch from API...");
    fetch("http://localhost:8080/api/mails", {
      headers: {
        "Content-Type": "application/json",
        "user-id": "2",
      },
    })
      .then((res) => {
        console.log("API Response status:", res.status);
        console.log("API Response headers:", res.headers);
        if (!res.ok) {
          throw new Error(`API returned status ${res.status}: ${res.statusText}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("API data received:", data);
        // התאמת המבנה מה-API למבנה שהקומפוננט מצפה
        const adaptedMails = data.map(mail => ({
          id: mail.id,
          from: mail.sender,
          to: mail.receiver,
          subject: mail.subject,
          body: mail.body,
          date: mail.dateCreated,
          isRead: Math.random() > 0.5, // רנדומלי לדוגמה
          isStarred: Math.random() > 0.7 // רנדומלי לדוגמה
        }));
        setMails(adaptedMails);
      })
      .catch((err) => {
        console.error("API Error:", err.message);
        console.log("Using dummy data instead");
        setMails("dummyMails");
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="inbox-page loading">Loading...</div>;
  }

  return (
    <div className="inbox-page">
      <div className="inbox-header">
        <h2>Inbox</h2>
        {showCreateLabel && <CreateLabel />}
      </div>
      <div className="inbox-content">
        {mails.length === 0 ? (
          <p className="no-mails">No mails found.</p>
        ) : (
          mails.map((mail) => <MailItem key={mail.id} mail={mail} />)
        )}
      </div>
      {showCompose && <Compose />}
    </div>
  );
}
