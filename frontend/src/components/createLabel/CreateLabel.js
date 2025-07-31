import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import './CreateLabel.css';
import Client from '../../services/Client'; 
function CreateLabel() {
    const [labelName, setLabelName] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            await Client.createLabel(`${labelName}`);
            navigate('/'); 
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
