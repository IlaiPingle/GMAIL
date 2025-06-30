
/**
* Creates a new label for a user
* @param {string} lableName - The name of the label to create
* @returns {Object} The created label object
*/
function createLabel(lableName) {
    const newLabel = {
        lableName,
        mails : [],
        dateCreated: new Date()
    };
    return newLabel;
}

/**
* Gets all labels for a user
* @param {number} userId - The ID of the user
* @returns {Array} Array of label objects
*/
function getUserLabels(userId) {
    return LabelsRepository.get(userId) || [];
}

/**
* Finds a label by its ID for a specific user
* @param {number} userId - The ID of the user
* @param {number} labelId - The ID of the label to find
* @returns {Object|null} The label object if found, or null
*/
function findLabelById(userId, labelId) {
    return (LabelsRepository.get(userId) || []).find(
        label => label.id === parseInt(labelId)
    );
}

/**
* Updates a label
* @param {number} userId - The ID of the user
* @param {number} labelId - The ID of the label to update
* @param {Object} updates - Object with properties to update
* @returns {Object|null} The updated label object or null if not found
*/
function updateLabel(userId, labelId, updates) {
    const label = findLabelById(userId, labelId);
    if (!label) return null;
    
    if (updates.name) label.name = updates.name;
    if (updates.color) label.color = updates.color;
    
    return label;
}

/**
* Deletes a label
* @param {number} userId - The ID of the user
* @param {number} labelId - The ID of the label to delete
* @returns {boolean} True if deleted, false if not found
*/
function deleteLabel(userId, labelId) {
    const userLabels = LabelsRepository.get(userId) || [];
    const labelIndex = userLabels.findIndex(label => label.id === parseInt(labelId));
    
    if (labelIndex !== -1) {
        userLabels.splice(labelIndex, 1);
        return true;
    }
    return false;
}

/**
* Add a label to an email
* @param {number} userId - The ID of the user
* @param {number} emailId - The ID of the email
* @param {number} labelId - The ID of the label to add
* @returns {boolean} True if successful, false otherwise
*/
/*function addLabelToEmail(userId, emailId, labelId) {
// This function requires integration with your emailModel
// Implementation depends on how emails are structured
return true;
}*/

/**
* Remove a label from an email
* @param {number} userId - The ID of the user
* @param {number} emailId - The ID of the email
* @param {number} labelId - The ID of the label to remove
* @returns {boolean} True if successful, false otherwise
*/
/*function removeLabelFromEmail(userId, emailId, labelId) {
// This function requires integration with your emailModel
// Implementation depends on how emails are structured
return true;
}*/

module.exports = {
    createLabel,
    getUserLabels,
    findLabelById,
    updateLabel,
    deleteLabel,
    //addLabelToEmail,
    //removeLabelFromEmail
};