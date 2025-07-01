const LabelModel = require('../Models/labelModel');
const EmailModel = require('../Models/emailModel');
const Users = require('../Models/userModel');
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
        const user = Users.findUserById(userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' }); 
        }
        const UsersLables = user.labels;
        if(UsersLables.has(lableName)) {
            return res.status(400).json({ message: 'Label already exists' });
        }   
        const newLabel = LabelModel.createLabel(lableName);
        UsersLables.set(lableName, newLabel);
        return res.status(201).json(newLabel);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while creating the label' });
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
        const user = Users.findUserById(userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const labels =  Array.from(user.labels.keys());
        return res.status(200).json(labels);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while retrieving labels' });
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
        
        const user = Users.findUserById(userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        const label = user.labels.get(labelName);
        
        if (!label) {
            return res.status(404).json({ message: 'Label not found' });
        }
        
        return res.status(200).json(label);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while retrieving the label' });
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
        
        if (!userId || !labelName) {
            return res.status(400).json({ message: 'User ID and label ID are required' });
        }
        const user = Users.findUserById(userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        if (!user.labels.has(labelName)) {
            return res.status(404).json({ message: 'Label not found' });
        }
        if (user.labels.has(newName)) {
            return res.status(400).json({ message: 'Label with this name already exists' });
        }
        const OldLabel = user.labels.get(labelName);
        OldLabel.labelName = newName;
        user.labels.delete(labelName);
        user.labels.set(newName, OldLabel);
        const updatedLabel = user.labels.get(newName);
        return res.status(201).json(updatedLabel);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while updating the label' });
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
        const user = Users.findUserById(userId);
        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }
        if (!user.labels.has(labelName)) {
            return res.status(404).json({ message: 'Label not found' });
        }
        user.labels.delete(labelName);
        return res.status(204).end();
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while deleting the label' });
    }
}


module.exports = {
    createLabel,
    getUserLabels,
    getLabelById,
    updateLabel,
    deleteLabel,
};