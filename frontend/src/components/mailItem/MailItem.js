import React, { Component } from "react";
import { useNavigate } from "react-router-dom";
import "./MailItem.css";
import IconButton from "../common/IconButton";

// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        return <Component {...props} navigate={navigate} />;
    };
}

class MailItem extends Component {
    moveToTrash = async (e) => {
        e.stopPropagation();
        const { mail } = this.props;
        
        try {
            const res = await fetch(`http://localhost:8080/api/mails/${mail.id}`, {
                method: 'DELETE',
                headers: { 'user-id': '2' }
            });
            if (!res.ok) {
                console.error('Failed to delete mail', res.status);
            } else {
                // Refresh inbox after delete
                window.location.reload();
            }
        } catch (err) {
            console.error('Error deleting mail', err);
        }
    };

    handleClick = () => {
        const { mail, navigate } = this.props;
        navigate(`/mail/${mail.id}`); // מעבר לעמוד צפייה במייל
    };

    // פונקציה לעיצוב התאריך
    formatDate = (dateString) => {
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

    render() {
        const { mail } = this.props;
        
        return (
            <div className={`mail-item ${mail.isRead ? 'read' : 'unread'}`} onClick={this.handleClick}>
                <div className="mail-checkbox">
                    <input type="checkbox" onClick={(e) => e.stopPropagation()} />
                </div>
                
                <div className="mail-star">
                    <i className={`bi ${mail.isStarred ? 'bi-star-fill' : 'bi-star'}`}></i>
                </div>
                
                <div className="mail-sender">
                    {mail.from}
                </div>
                
                <div className="mail-content">
                    <span className="mail-subject">
                        {mail.subject}
                    </span>
                    <span className="mail-preview">
                        {mail.body ? ` - ${mail.body.substring(0, 100)}...` : ''}
                    </span>
                </div> 
                <IconButton
                    iconType="material" 
                    onClick={this.moveToTrash}>
                    Delete
                </IconButton>
                
                <div className="mail-date">
                    {this.formatDate(mail.date || new Date())}
                </div>
            </div>
        );
    }
}

export default withNavigation(MailItem);
