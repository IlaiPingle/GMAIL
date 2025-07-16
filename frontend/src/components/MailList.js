import React from "react";

import MailItem from "../components/mailItem/MailItem";
import Client from "../services/Client";

class MailList extends React.Component {
        state = {
            mails: [],
            loading: true,
        };

    componentDidMount() {
        const {boxType} = this.props;
    
        Client.getMailsByType(boxType)
            .then(mails => this.setState({ mails, loading: false }))
            .catch(error => {
                console.error("Error loading mails:", error);
                this.setState({ loading: false });
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
            {mails.map(mail => (
                <MailItem key={mail.id} mail={mail} />
            ))}
        </div>
     );
    }
}
export default MailList;
