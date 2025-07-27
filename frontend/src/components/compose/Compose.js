import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import './Compose.css';
import Client from '../../services/Client';

function Compose({ draftId }) {
    const [receiver, setReceiver] = useState('');
    const [subject, setSubject] = useState('');
    const [body, setBody] = useState('');
    const [isDrafted, setIsDrafted] = useState(false);
    const [error, setError] = useState(null);
    const mailIdRef = useRef(null);
    const navigate = useNavigate();

    /**
     * Fetches the draft mail if a draftId is provided in the props.
     * this can only happen when the user clicks on a draft mail to edit it.
     */
    useEffect(() => {
        const loadDraft = async () => {
            if (draftId) {
                try {
                    const draft = await Client.getMailById(draftId);
                    setReceiver(draft.receiver || '');
                    setSubject(draft.subject || '');
                    setBody(draft.body || '');
                    setIsDrafted(true);
                    mailIdRef.current = draft.id;
                } catch (error) {
                    setError('Failed to load draft');
                }
            }
        };
        
        loadDraft();
    }, [draftId]);

    /**
    * Handles changes to the input fields and saves the draft if necessary.
    * this function is called and updates the state with the new value,
    * at the first change of any field.
    */
    const handleFieldChange = async (field, value) => {
        // Update the appropriate state
        if (field === 'receiver') setReceiver(value);
        else if (field === 'subject') setSubject(value);
        else if (field === 'body') setBody(value);

        if (!isDrafted && (receiver || subject || body || value)) {
            try {
                const response = await Client.createDraft({
                    receiver: field === 'receiver' ? value : receiver,
                    subject: field === 'subject' ? value : subject,
                    body: field === 'body' ? value : body
                });
                setIsDrafted(true);
                mailIdRef.current = response.id;
                console.log('Draft created successfully:', response.id);
            } catch (error) {
                setError('Failed to create draft');
            }
        }
    };

    /**
     * Saves the draft mail if it exists.
     * this function is called when the user closes the compose window.
     */
    const saveDraft = async () => {
        if (isDrafted && mailIdRef.current) {
            try {
                console.log('Updating draft:', mailIdRef.current);
                await Client.updateMail(mailIdRef.current, {
                    receiver,
                    subject,
                    body
                });
            } catch (error) {
                setError('Failed to save draft');
            }
        }
    };

    /**
     * Handles the form submission to send the email.
     * Validates the receiver field and sends the email using the Client service.
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Validation
        if (!receiver.trim()) {
            setError('Receiver email is required');
            return;
        }
        
        try {
            setError(null); // Clear previous errors
            
            console.log('Sending email with data:', { receiver, subject, body });
            await Client.removeLabelFromMail('drafts', mailIdRef.current);

            const response = await Client.sendMail(mailIdRef.current, {
                receiver,
                subject,
                body
            });
            
            console.log('Email sent successfully:', response);
            
            navigate(window.location.pathname);
        } catch (error) {
            console.error('Error sending email:', error);
            setError(error.message || 'Failed to send email');
        }
    };

    return (
        <div className="compose-container">
            <div className="compose-header">
                <span>New Message</span>
                <button className="close-btn" onClick={async () => {
                    await saveDraft();
                    navigate(window.location.pathname);
                }}>×</button>
            </div>
            <form className="compose-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="To"
                    value={receiver}
                    onChange={(e) => handleFieldChange('receiver', e.target.value)}
                    required
                />
                <input
                    type="text"
                    placeholder="Subject"
                    value={subject}
                    onChange={(e) => handleFieldChange('subject', e.target.value)}
                />
                <textarea
                    placeholder="Message"
                    value={body}
                    onChange={(e) => handleFieldChange('body', e.target.value)}
                />
                {error && <div className="error">{error}</div>}
                <button type="submit" className="send-btn">Send</button>
            </form>
        </div>
    );
}

export default Compose;
