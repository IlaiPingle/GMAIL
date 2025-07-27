import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import MailItem from "../components/mailItem/MailItem";
import Client from "../services/Client";
import "./MailList.css";

function MailList() {
    const [mails, setMails] = useState([]);
    const [loading, setLoading] = useState(true);
    const { boxType } = useParams();

    const fetchMails = () => {
        setLoading(true);
        
        console.log('MailList - Fetching mails for label:', boxType);

        Client.getMailsByLabel(boxType)
            .then(mails => {
                console.log('MailList - Received mails:', mails);
                setMails(mails);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error loading mails:", error);
                setMails([]);
                setLoading(false);
            });
    };

    useEffect(() => {
        if (boxType) {
            fetchMails();
        }
    }, [boxType]);

    if (loading) {
        return <p>Loading...</p>
    }
    if(!mails.length) {
        return <p>No mails found.</p>
    }
    return (
      <div className="mail-list">
        <div className="mail-list-header">
          <h2>{boxType}</h2>
        </div>
        {mails.map((mail) => (
          <MailItem
            key={mail.id}
            mail={mail}
            onDeleted={() => {
              setMails(prev => prev.filter((m) => m.id !== mail.id));
            }}
          />
        ))}
      </div>
    );
}

export default MailList;
