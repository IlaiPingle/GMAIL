import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";

import MailItem from "../components/mailItem/MailItem";
import Client from "../services/Client";
import "./MailList.css";

function MailList() {
    const [mails, setMails] = useState([]);
    const [loading, setLoading] = useState(true);
    const { boxType } = useParams();
    const location = useLocation();
    
    // Extract search parameter from URL
    const searchParams = new URLSearchParams(location.search);
    const searchQuery = searchParams.get('search');

    const fetchMails = async () => {
        setLoading(true);
        
        try {
            let mails;
            if (searchQuery) {
                console.log('MailList - Searching for:', searchQuery);
                mails = await Client.searchMails(searchQuery);
            } else {
                console.log('MailList - Fetching mails for label:', boxType);
                mails = await Client.getMailsByLabel(boxType);
            }
            
            console.log('MailList - Received mails:', mails);
            setMails(mails);
        } catch(error) {
            console.error("Error loading mails:", error);
            setMails([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (boxType || searchQuery) {
            fetchMails();
        }
    }, [boxType, searchQuery]);

    if (loading) {
        return <p>Loading...</p>
    }
    if(!mails.length) {
        return <p>No mails found.</p>
    }
    
    // Determine header title
    const headerTitle = searchQuery ? `Search Results for: "${searchQuery}"` : boxType;
    
    return (
      <div className="mail-list">
        <div className="mail-list-header">
          <h2>{headerTitle}</h2>
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
