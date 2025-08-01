import React, { useState, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";

import MailItem from "../components/mailItem/MailItem";
import Client from "../services/Client";
import IconButton from "../components/common/IconButton";
import "./MailList.css";


function MailList() {
    const [mails, setMails] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedMail, setSelectedMail] = useState(new Set());
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
            setMails(mails || []);
            setSelectedMail(new Set()); 
        } catch(error) {
            console.error("Error loading mails:", error);
            setMails([]);
            setSelectedMail(new Set());
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (boxType || searchQuery) {
            fetchMails();
        }
    }, [boxType, searchQuery]);
    const toggleSelectMail = (mailId) => {
        setSelectedMail((prevSelected) => {
            const newSelected = new Set(prevSelected);
            if (newSelected.has(mailId)) {
                newSelected.delete(mailId);
            } else {
                newSelected.add(mailId);
            }
            return newSelected;
        });
    };

    const allSelected = mails.length > 0 && selectedMail.size === mails.length;
    
    const toggleSelectAll = () => {
      allSelected ? setSelectedMail(new Set()) : setSelectedMail(new Set(mails.map(mail => mail.id)));
    };

    const moveSelectedToBin = async () => {
        try {
          await Promise.all(
            Array.from(selectedMail).map(async (mailId) => {
              if (boxType !== 'bin' && boxType !== 'spam') {
                await Client.removeLabelFromMail(boxType, mailId);
                await Client.addLabelToMail("bin", mailId);
              } else {
                await Client.deleteMail(mailId);
              }
            })
          );
          setMails((prevMails) => prevMails.filter((mail) => !selectedMail.has(mail.id)));
          setSelectedMail(new Set());
        } catch (error) {
          console.error("Error Removing mails:", error);
        }
      };
      const moveSelectedToSpam = async () => {
        try {
          const mailsById = new Map(mails.map(mail => [mail.id, mail]));
          await Promise.all(
            Array.from(selectedMail).map(async (mailId) => {
              await Client.removeLabelFromMail(boxType, mailId);
              await Client.reportSpam(mailsById.get(mailId));
            })
          );
          setMails((prevMails) => prevMails.filter((mail) => !selectedMail.has(mail.id)));
          setSelectedMail(new Set());
        } catch (error) {
          console.error("Error Reporting mails:", error);
        }
      };
      const moveSelectedTo = async (labelName) => {
        try {
          await Promise.all(
            Array.from(selectedMail).map(async (mailId) => {
              await Client.removeLabelFromMail(boxType, mailId);
              await Client.addLabelToMail(labelName, mailId);
            })
          );
          setMails((prevMails) => prevMails.filter((mail) => !selectedMail.has(mail.id)));
          setSelectedMail(new Set());
        } catch (error) {
          console.error("Error Moving mails:", error);
        }
      };
    const refreshMailList = () => {
        fetchMails();
    };
    // Determine header title
    const headerTitle = searchQuery ? `Search Results for: "${searchQuery}"` : boxType;
    
    return (
      <div className="mail-list">
        <div className="mail-list-header">
          <span className="mail-list-header-left">
          <input
            type="checkbox"
            className="select-all-checkbox"
            checked={allSelected}
            onChange={toggleSelectAll}
            disabled={loading || mails.length === 0}
          />
          <IconButton className="icon-mail-list" onClick={refreshMailList}>refresh</IconButton>
        </span>
          <div className="selection-actions">
              <IconButton 
                className="icon-mail-list"
                onClick={moveSelectedToBin}
                disabled={loading}>delete
              </IconButton>
              <IconButton 
                className="icon-mail-list"
                onClick={moveSelectedToSpam}
                disabled={loading}>report
              </IconButton>
              <IconButton 
                className="icon-mail-list"
                onClick={moveSelectedTo}
                disabled={loading}>drive_file_move
              </IconButton>
          </div>
          <h2>{headerTitle}</h2>
       </div>
       <div className="mail-list-body">
        { loading ? (
          <p>Loading mails...</p>
        ) : mails.length === 0 ? (
          <p>No mails found.</p>
        ) : (
        mails.map((mail) => (
          <MailItem
            key={mail.id}
            mail={mail}
            checked={selectedMail.has(mail.id)}
            onToggleSelect={()=> toggleSelectMail(mail.id)}
            onDeleted={() => {
              setMails(prev => prev.filter((m) => m.id !== mail.id)); 
            }}
          />
        ))
        )}
      </div>
    </div>
    );
}

export default MailList;
