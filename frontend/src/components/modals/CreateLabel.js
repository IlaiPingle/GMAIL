import React, { useState } from "react";
import Client from '../../services/Client'; 
import './CreateLabel.css';

function CreateLabel({ onClose, labelToEdit,onCreate}) {
    const [labelName, setLabelName] = useState(labelToEdit || "");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const isEditing = Boolean(labelToEdit);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            if (isEditing) {
                await Client.updateLabel(labelToEdit, labelName);
            } else {
                await Client.createLabel(`${labelName}`);
            }
            window.dispatchEvent(new CustomEvent('label:created', { detail: labelName }));
            if (onCreate) {
                onCreate(labelName);
            }
        } catch (err) {
            setError(err.message);
            console.error("Error creating label:", err);
        } finally {
            setLoading(false);
            onClose();
        }
    };

    return (
        <div className="create-label-overlay">
            <div className="create-label-container" onClick={(e) => e.stopPropagation()}>
                <h2>{isEditing ? "Edit label" : "New label"}</h2>
                <p>{isEditing ? "Label name:" : "Please enter a new label name:"}</p>
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
                        <button type="button" className="cancel-btn" onClick={onClose}>Cancel</button>
                        <button type="submit" className="create-btn" disabled={!labelName || ((isEditing && labelName === labelToEdit) || loading)} >
                            {isEditing ? "Save" : "Create"}
                            </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateLabel;
