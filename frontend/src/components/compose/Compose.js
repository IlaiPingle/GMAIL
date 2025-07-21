import React, { Component } from 'react';
import { useNavigate } from 'react-router-dom';
import './Compose.css';
import Client from '../../services/Client';

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
        
        // Validation
        if (!receiver.trim()) {
            this.setState({ error: 'Receiver email is required' });
            return;
        }
        
        try {
            this.setState({ error: null }); // Clear previous errors
            
            console.log('Sending email with data:', { receiver, subject, body });
            
            const response = await Client.sendMail({
                receiver,
                subject,
                body
            });
            
            console.log('Email sent successfully:', response);
            
            // If successful, navigate back to inbox
            navigate('/');
        } catch (error) {
            console.error('Error sending email:', error);
            this.setState({ error: error.message || 'Failed to send email' });
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
