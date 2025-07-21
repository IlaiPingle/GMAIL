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
        return <Component {...props} navigate={navigate} location={location} />;
    };
}

class MailItem extends Component {
    constructor(props) {
        super(props);
        const isStarred = props.mail.labels.includes('starred');
        this.state = {
            isStarred
        };
    }
    moveToTrash = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        const { mail, location ,onDeleted} = this.props;
        const currentPath = location.pathname;
        const currentLabel = currentPath.split('/')[1]; 
        try {
            if (currentLabel === "bin") {
                 await Client.deleteMail(mail.id);
            }
            await Client.removeLabelFromMail(currentLabel,mail.id); // Assuming 'trash' is the label for deleted mails
            console.log('Mail moved to trash:', mail.id);
            await Client.addLabelToMail("bin", mail.id);
            if (onDeleted) {
                onDeleted(mail);
            }
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
    handleStarClicked = async (e) => {
        e.stopPropagation(); // Prevent the click from navigating to the mail detail
        const { mail } = this.props;
        try {
            if (this.state.isStarred) {
                await Client.removeLabelFromMail('starred', mail.id);
                mail.labels = mail.labels.filter(label => label !== 'starred');
            } else {
                await Client.addLabelToMail("starred", mail.id);
                mail.labels.push('starred');
            }
            this.setState(prevState => ({isStarred : !prevState.isStarred}));       
        } catch (error) {
            console.error('Error updating star status:', error);
        }
    };
    render() {
        const { mail } = this.props;
        return (
          <div
            className={`mail-item ${mail.unread ? "unread" : "read"}`}
            onClick={this.handleClick}
          >
            <div className="mail-checkbox">
              <input type="checkbox" onClick={(e) => e.stopPropagation()} />
            </div>
            <button className="star-button" onClick={this.handleStarClicked}>
                <span className={`material-symbols-outlined star-icon ${this.state.isStarred ? 'starred' : ''}`}>
                    star
                </span>
            </button>
            <div className="mail-sender">{mail.sender}</div>

            <div className="mail-content">
              <span className="mail-subject">{mail.subject}</span>
              <span className="mail-preview">
                {mail.body ? ` - ${mail.body.substring(0, 100)}...` : ""}
              </span>
            </div>
            <IconButton onClick={this.moveToTrash}>Delete</IconButton>

            <div className="mail-date">
              {this.formatDate(mail.date || new Date())}
            </div>
          </div>
        );
    }
}

export default withNavigation(MailItem);
