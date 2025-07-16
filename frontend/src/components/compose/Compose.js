import React, { Component } from 'react';
import { useNavigate } from 'react-router-dom';
import './Compose.css';

// HOC for navigation hooks in class component
function withNavigation(Component) {
    return function ComponentWithNavigation(props) {
        const navigate = useNavigate();
        return <Component {...props} navigate={navigate} />;
    };
}

class Compose extends Component {
    constructor(props) {
        super(props);
        this.state = {
            receiver: '',
            subject: '',
            body: '',
            error: null
        };
    }

    handleSubmit = async (e) => {
        e.preventDefault();
        const { receiver, subject, body } = this.state;
        const { navigate } = this.props;
        
        try {
            const res = await fetch('http://localhost:8080/api/mails', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'user-id': '2', // Replace with dynamic user ID as needed
                },
                body: JSON.stringify({ receiver, subject, body }),
            });
            if (!res.ok) throw new Error('Failed to send mail');
            navigate('/');
        } catch (err) {
            this.setState({ error: err.message });
        }
    };

    render() {
        const { receiver, subject, body, error } = this.state;
        const { navigate } = this.props;
        
        return (
            <div className="compose-container">
                <div className="compose-header">
                    <span>New Message</span>
                    <button className="close-btn" onClick={() => navigate('/')}>×</button>
                </div>
                <form className="compose-form" onSubmit={this.handleSubmit}>
                    <input
                        type="text"
                        placeholder="To"
                        value={receiver}
                        onChange={(e) => this.setState({ receiver: e.target.value })}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Subject"
                        value={subject}
                        onChange={(e) => this.setState({ subject: e.target.value })}
                    />
                    <textarea
                        placeholder="Message"
                        value={body}
                        onChange={(e) => this.setState({ body: e.target.value })}
                    />
                    {error && <div className="error">{error}</div>}
                    <button type="submit" className="send-btn">Send</button>
                </form>
            </div>
        );
    }
}

export default withNavigation(Compose);
