const LabelService = require('../services/labelService');

/**
* Create a new label
*/
function createLabel(req, res) {
    try {
        const userId = req.userId 
        const {labelName} = req.body;
        
        if (!userId || !labelName) {
            return res.status(400).json({ message: 'User ID and label name are required' });
        }
        
        const newLabel = LabelService.createLabel(userId, labelName);
        res.set('Location', `/api/labels/${labelName}`);
        return res.status(201).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while creating the label' });
    }
}

/**
* Get all labels for a user
*/
function getUserLabels(req, res) {
    try {
        const userId = req.userId; // Get user ID from JWT token

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
        const userId = req.userId;
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
        const userId = req.userId;
        const labelName = req.params.id;
        const {newName} = req.body;
        
        if (!userId || !labelName || !newName) {
            return res.status(400).json({ message: 'User ID, label ID, and new name are required' });
        }
        
        LabelService.updateLabel(userId, labelName, newName);
        return res.status(204).json();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while updating the label' });
    }
}

/**
* Delete a label
*/
function deleteLabel(req, res) {
    try {
        const userId = req.userId;
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

function getMailsByLabel(req, res) {
    try {
        const userId = req.userId;
        const labelName = req.query.label;
        if (!userId || !labelName) {
            return res.status(400).json({ message: 'User ID and label name are required' });
        }
        const mails = LabelService.getMailsByLabel(userId, labelName);
        return res.status(200).json(mails);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving mails by label' });
    }
}
 function removeLabelFromMail(req, res) {
    try {
        const userId = req.userId;
        const mailId = parseInt(req.params.id , 10);
        const labelName = req.body.labelName;

        if (!userId || !mailId || !labelName) {
            return res.status(400).json({ message: 'User ID, mail ID, and label name are required' });
        }

        LabelService.removeLabelfromMail(userId, mailId, labelName);
        return res.status(204).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while removing the label from mails' });
    }
};
function addLabelToMail(req, res) {
    try {
        const userId = req.userId;
        const mailId = parseInt(req.params.id, 10);
        const labelName = req.body.labelName;

        if (!userId || !mailId || !labelName) {
            return res.status(400).json({ message: 'User ID, mail ID, and label name are required' });
        }

        LabelService.addLabelToMail(userId, mailId, labelName);
        return res.status(204).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message || 'An error occurred while adding the label to the mail' });
    }
}

module.exports = {
    createLabel,
    getUserLabels,
    getLabelById,
    updateLabel,
    deleteLabel,
    getMailsByLabel,
    removeLabelFromMail,
    addLabelToMail
};