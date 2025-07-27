import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import './CreateLabel.css';

function CreateLabel() {
    const [labelName, setLabelName] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const response = await fetch('http://localhost:8080/api/labels', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'user-id': '2', // Replace with dynamic user ID as needed
                },
                body: JSON.stringify({ labelName }),
            });
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create label');
            }
            navigate('/'); // Redirect to inbox after creation
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div className="create-label-overlay">
            <div className="create-label-container">
                <h2>New label</h2>
                <p>Please enter a new label name:</p>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Label Name"
                        value={labelName}
                        onChange={(e) => setLabelName(e.target.value)}
                        required
                    />
                    {error && <div className="error">{error}</div>}
                    <div className="create-label-actions">
                        <button type="button" className="cancel-btn" onClick={() => navigate('/')}>Cancel</button>
                        <button type="submit" className="create-btn" disabled={!labelName}>Create</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateLabel;
