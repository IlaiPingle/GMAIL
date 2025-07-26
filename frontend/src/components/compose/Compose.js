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
            isDrafted: false,
            error: null
        };
        this.mailId = null; 
    }
    /**
     * Fetches the draft mail if a draftId is provided in the props.
     * this can only happen when the user clicks on a draft mail to edit it.
     */
    async componentDidMount() {
        const { draftId } = this.props;
        if (draftId) {
            try {
                const draft = await Client.getMailById(draftId);
                this.setState({
                    receiver: draft.receiver || '',
                    subject: draft.subject || '',
                    body: draft.body || '',
                    isDrafted: true
                });
                this.mailId = draft.id; 
            } catch (error) {
                this.setState({ error: 'Failed to load draft' });
            }
        }
    }
    /**
    * Handles changes to the input fields and saves the draft if necessary.
    * this function is called and updates the state with the new value,
    * at the first change of any field.
    */
    handleFieldChange = async (field, value) => {
        this.setState({ [field]: value });
        if(!this.state.isDrafted && (this.state.receiver || this.state.subject || this.state.body)) {
            try {
                const response = await Client.createDraft({
                    receiver: field === 'receiver' ? value : this.state.receiver,
                    subject: field === 'subject' ? value : this.state.subject,
                    body: field === 'body' ? value : this.state.body
                });
                this.setState({ isDrafted: true });
                this.mailId = response.id;
                console.log('Draft created successfully:', response.id);
            } catch (error) {
                this.setState({ error: 'Failed to create draft' });
            }
        }
    };
    /**
     * Saves the draft mail if it exists.
     * this function is called when the user closes the compose window.
     */
    saveDraft = async () => {
        if (this.state.isDrafted && this.mailId) {
            try {
                console.log('Updating draft:', this.mailId);
                await Client.updateMail(this.mailId, {
                    receiver: this.state.receiver,
                    subject: this.state.subject,
                    body: this.state.body
                });
            } catch (error) {
                this.setState({ error: 'Failed to save draft' });
            }
        }
    };
    /**
     * Handles the form submission to send the email.
     * Validates the receiver field and sends the email using the Client service.
     */
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
            await Client.removeLabelFromMail('drafts', this.mailId);

            const response = await Client.sendMail(this.mailId, {
                receiver,
                subject,
                body
            });
            
            console.log('Email sent successfully:', response);
            
            navigate(window.location.pathname);
        } catch (error) {
            console.error('Error sending email:', error);
            this.setState({ error: error.message || 'Failed to send email' });
        }
    };


    render() {
        const { receiver, subject , body , error } = this.state;
        const { navigate } = this.props;
        
        return (
            <div className="compose-container">
                <div className="compose-header">
                    <span>New Message</span>
                    <button className="close-btn" onClick={ async () => {
                        await this.saveDraft();
                        navigate(window.location.pathname);
                    }}>×</button>
                </div>
                <form className="compose-form" onSubmit={this.handleSubmit}>
                    <input
                        type="text"
                        placeholder="To"
                        value={receiver}
                        onChange={(e) => this.handleFieldChange('receiver', e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Subject"
                        value={subject}
                        onChange={(e) => this.handleFieldChange('subject', e.target.value)}
                    />
                    <textarea
                        placeholder="Message"
                        value={body}
                        onChange={(e) => this.handleFieldChange('body', e.target.value)}
                    />
                    {error && <div className="error">{error}</div>}
                    <button type="submit" className="send-btn">Send</button>
                </form>
            </div>
        );
    }
}

export default withNavigation(Compose);
