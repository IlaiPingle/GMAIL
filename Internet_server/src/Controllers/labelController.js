const LabelService = require('../services/labelService');

/**
 * Create a new label
 */
function createLabel(req, res) {
    try {
        const userId = parseInt(req.header("user-id"), 10);  
        const {lableName} = req.body;
        
        if (!userId || !lableName) {
            return res.status(400).json({ message: 'User ID and label name are required' });
        }
        
        const newLabel = LabelService.createLabel(userId, lableName);
        return res.status(201).json(newLabel);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while creating the label' });
    }
}

/**
 * Get all labels for a user
 */
function getUserLabels(req, res) {
    try {
        const userId = parseInt(req.header("user-id"), 10);
        
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        
        const labels = LabelService.getUserLabels(userId);
        return res.status(200).json(labels);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving labels' });
    }
}

/**
 * Get a specific label by ID
 */
function getLabelById(req, res) {
    try {
        const userId = parseInt(req.header("user-id"), 10);
        const labelName = req.params.id;
        
        if (!userId || !labelName) {
            return res.status(400).json({ message: 'User ID and label ID are required' });
        }
        
        const label = LabelService.getLabelByName(userId, labelName);
        return res.status(200).json(label);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving the label' });
    }
}

/**
 * Update a label
 */
function updateLabel(req, res) {
    try {
        const userId = parseInt(req.header("user-id"), 10);
        const labelName = req.params.id;
        const {newName} = req.body;
        
        if (!userId || !labelName || !newName) {
            return res.status(400).json({ message: 'User ID, label ID, and new name are required' });
        }
        
        const updatedLabel = LabelService.updateLabel(userId, labelName, newName);
        return res.status(201).json(updatedLabel);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while updating the label' });
    }
}

/**
 * Delete a label
 */
function deleteLabel(req, res) {
    try {
        const userId = parseInt(req.header("user-id"), 10);
        const labelName = req.params.id;
        
        if (!userId || !labelName) {
            return res.status(400).json({ message: 'User ID and label ID are required' });
        }
        
        LabelService.deleteLabel(userId, labelName);
        return res.status(204).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while deleting the label' });
    }
}

module.exports = {
    createLabel,
    getUserLabels,
    getLabelById,
    updateLabel,
    deleteLabel
};