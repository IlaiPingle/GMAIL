const Users = require('../Models/userModel');

/**
* Create a new label for a user
*/
function createLabel(userId, labelName) {
    const user = getUserOrThrow(userId); // Ensure user exists
    if(user.labels.has(labelName)) { // Check if label already exists
        const error = new Error('Label already exists');
        error.status = 400;
        throw error;
    }
    user.labels.set(labelName,{mailIds: new Set()});
}

/**
* Get all labels for a user
*/
function getUserLabels(userId) {
    const user = getUserOrThrow(userId);
    return Array.from(user.labels.keys()).filter(label => !Users.isSystemLabel(label));
}

/**
* Get a specific label by name
*/
function getLabelByName(userId, labelName) {
    const user = getUserOrThrow(userId);
    const label = user.labels.get(labelName);
    
    if (!label) {
        const error = new Error('Label not found');
        error.status = 404;
        throw error;
    }
    
    return label;
}

/**
* Update a label name
*/
function updateLabel(userId, labelName, newName) {
    const user = getUserOrThrow(userId);
    
    if (!user.labels.has(labelName)) {
        const error = new Error('Label not found');
        error.status = 404;
        throw error;
    }
    
    if (user.labels.has(newName)) {
        const error = new Error('Label with this name already exists');
        error.status = 400;
        throw error;
    }
    
    const labelData = user.labels.get(labelName);
    user.labels.set(newName, labelData);
    user.labels.delete(labelName);
    const mailsIds = labelData.mailIds;
    for (const mailId of mailsIds) {
        const mail = user.mails.get(mailId);
        if (mail) {
            mail.labels = mail.labels.map(label => label === labelName ? newName : label);
        }
    }
    return true;
}

/**
* Delete a label
*/
function deleteLabel(userId, labelName) {
    const user = getUserOrThrow(userId);
    
    if (!user.labels.has(labelName)) {
        const error = new Error('Label not found');
        error.status = 404;
        throw error;
    }
    const mailIds = user.labels.get(labelName).mailIds;
    user.labels.delete(labelName);
    for (const mailId of mailIds) {
        const mail = user.mails.get(mailId);
        if (mail) {
            mail.labels = mail.labels.filter(label => label !== labelName);
        }
    }
    return true;
}

function addLabelToMail(userId, mailId, labelName) {
    const user = getUserOrThrow(userId);
    const mail = user.mails.get(mailId);
    if (!mail) {
        const error = new Error('Mail not found');
        error.status = 404;         
        throw error;
    }
    if (!user.labels.has(labelName)) {
        const error = new Error('Label not found');
        error.status = 404;
        throw error;
    }
    if (!mail.labels.includes(labelName)) {
        mail.labels.push(labelName);
        user.labels.get(labelName).mailIds.add(mailId);
    }
    return true;
}
function removeLabelFromMail(userId,mailId,labelName){
    const user = getUserOrThrow(userId);
    const mail = user.mails.get(mailId);
    if(!mail){
        const error = new Error('Mail not found');
        error.status = 404;
        throw error;    
    }
    mail.labels = mail.labels.filter(label => label !== labelName);

    if(user.labels.has(labelName)){
        user.labels.get(labelName).mailIds.delete(mailId);
    }
}
function getMailsByLabel(userId, labelName) {
    const user = getUserOrThrow(userId);
    
    const label = user.labels.get(labelName);
    if (!label) {
        const error = new Error('Label not found');
        error.status = 404;
        throw error;
    }
    if (label.mailIds.size === 0) {
        return []; // Return empty array if no mails are associated with the label
    }
    return Array.from(label.mailIds).map(mailId => user.mails.get(mailId));
}






/**
* Helper function to get a user or throw an error
*/
function getUserOrThrow(userId) {
    if (!userId) {
        const error = new Error('User ID is required');
        error.status = 400;
        throw error;
    }
    
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    return user;
}



module.exports = {
    createLabel,
    getUserLabels,
    getLabelByName,
    updateLabel,
    deleteLabel,
    addLabelToMail,
    getMailsByLabel,
    removeLabelFromMail,
};