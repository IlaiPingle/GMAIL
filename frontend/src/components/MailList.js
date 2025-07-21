import React from "react";

import MailItem from "../components/mailItem/MailItem";
import Client from "../services/Client";
import "./MailList.css";
class MailList extends React.Component {
        state = {
            mails: [],
            loading: true,
        };

    componentDidMount() {
        this.fetchMails();
    }

    componentDidUpdate(prevProps) {
        if (prevProps.label !== this.props.label) {
            this.fetchMails();
        }
    }

    fetchMails = () => {
        const { label } = this.props;
        this.setState({ loading: true });
        
        console.log('MailList - Fetching mails for label:', label);
        
        Client.getMailsByLabel(label)
            .then(mails => {
                console.log('MailList - Received mails:', mails);
                this.setState({ mails, loading: false });
            })
            .catch(error => {
                console.error("Error loading mails:", error);
                this.setState({ mails: [], loading: false });
            });
    }
     render(){
        const { mails, loading } = this.state;
        if (loading) {
            return <p>Loading...</p>
        }
        if(!mails.length) {
            return <p>No mails found.</p>
        }
        return (
          <div className="mail-list">
            <div className="mail-list-header">
              <h2>{this.props.label}</h2>
            </div>
            {mails.map((mail) => (
              <MailItem
                key={mail.id}
                mail={mail}
                onDeleted={() => {
                  this.setState((prev) => ({
                    mails: prev.mails.filter((m) => m.id !== mail.id),
                  }));
                }}
              />
            ))}
          </div>
        );
    }
}
export default MailList;
