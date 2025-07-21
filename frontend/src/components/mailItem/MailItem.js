import React, { Component } from "react";
import { useNavigate , useLocation} from "react-router-dom";
import "./MailItem.css";
import IconButton from "../common/IconButton";
import Client from "../../services/Client"; 
// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        const location = useLocation();
        return <Component {...props} navigate={navigate} />;
    };
}

class MailItem extends Component {
    moveToTrash = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        const { mail, navigate } = this.props;
        // let pathname = location.pathname.split('/')[1];
        try {
            await Client.removeLabelFromMail(mail.id, 'bin'); // Assuming 'trash' is the label for deleted mails
            console.log('Mail moved to trash:', mail.id);
            navigate('/'); // Redirect to inbox after moving to trash
        } catch (error) {
            console.error('Error moving mail to trash:', error);
        }
    };

    handleClick = () => {
        const { mail, navigate } = this.props;
        navigate(`/mail/${mail.id}`);
        
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
            <div className={`mail-item ${mail.unread ? 'unread' : 'read'}`} onClick={this.handleClick}>
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
