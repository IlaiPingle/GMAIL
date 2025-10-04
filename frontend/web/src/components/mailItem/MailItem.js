import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./MailItem.css";
import IconButton from "../common/IconButton";
import Client from "../../services/Client";


function MailItem({ mail, checked, onToggleSelect, onDeleted }) {
    const navigate = useNavigate();
    const location = useLocation();
    const [isStarred, setIsStarred] = useState(mail.labels.includes('starred'));
    

    const moveToTrash = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        const currentPath = location.pathname;
        const currentLabel = currentPath.split('/')[1]; 
        try {
            if (currentLabel === "bin") {
                const warn = window.confirm("Are you sure you want to permanently delete this mail?");
                if (!warn) return;
                await Client.deleteMail(mail.id);
            } else {
                await Client.removeLabelFromMail(currentLabel, mail.id);
                console.log('Mail moved to trash:', mail.id);
                await Client.addLabelToMail("bin", mail.id);
            }
            if (onDeleted) {
                onDeleted(mail);
            }
        } catch (error) {
            console.error('Error moving mail to trash:', error);
        }
    };
    const markAsUnread = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        try {
            if (mail.labels.includes('unread')) {
                return; // Already marked as unread
            }
            await Client.addLabelToMail('unread', mail.id);
            mail.labels.push('unread');
            alert('Mail marked as unread');
        } catch (error) {
            console.error('Error marking mail as unread:', error);
            alert('Failed to mark mail as unread');
        }
    }; 

    const handleClick = () => {
        const { pathname } = location;
        if (mail.labels.includes('drafts')) {
            navigate(`${pathname}?compose=${mail.id}`);
            return;
        }
        navigate(`${pathname}/${mail.id}`);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffTime = Math.abs(now - date);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays === 1) {
            return date.toLocaleTimeString('he-IL', { hour: '2-digit', minute: '2-digit' });
        } else if (diffDays <= 7) {
            return date.toLocaleDateString('he-IL', { day: 'numeric', month: 'short' });
        } else {
            return date.toLocaleDateString('he-IL', { day: 'numeric', month: 'short' });
        }
    };

    const handleStarClicked = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        try {
            if (isStarred) {
                await Client.removeLabelFromMail('starred', mail.id);
                mail.labels = mail.labels.filter(label => label !== 'starred');
            } else {
                await Client.addLabelToMail("starred", mail.id);
                mail.labels.push('starred');
            }
            setIsStarred(!isStarred);       
        } catch (error) {
            console.error('Error updating star status:', error);
        }
    };

    return (
      <div
        className={`mail-item ${mail.labels.includes("unread") ? "unread" : "read"}`}
        onClick={handleClick}
      >
        <div className="mail-checkbox" onClick={(e) => e.stopPropagation()}>
          <input type="checkbox" checked={checked} onChange={onToggleSelect} />
        </div>
        <IconButton
          className={`icon-mail-item ${isStarred ? "starred" : "unstarred"}`}
          onClick={handleStarClicked}
          children={isStarred ? "star" : "star_border"}
        />
        <div className="mail-sender">{mail.sender}</div>

        <div className="mail-content">
          <span className="mail-subject">{mail.subject}</span>
          <span className="mail-preview">
            {mail.body ? ` - ${mail.body.substring(0, 100)}...` : ""}
          </span>
        </div>
        <div className="mail-item-actions">
        <IconButton className="icon-mail-item" onClick={moveToTrash} children={"delete"} />
        <IconButton className="icon-mail-item" onClick={markAsUnread} children={"mark_email_unread"} />
        </div>
        <div className="mail-date">
          {formatDate(mail.date || new Date())}
        </div>
      </div>
    );
}

export default MailItem;
