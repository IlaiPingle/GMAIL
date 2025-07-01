const Users = require('../Models/userModel');
const { sendCommand } = require('./blacklistClient');

let nextEmailId = 1;
/**
* Send a new email with security validation
*/

async function sendNewMail(userId, receiver, subject, body) {
    // Find users
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    const receiverUser = Users.findUserByUsername(receiver);
    if (!receiverUser) {
        const error = new Error('Receiver not found');
        error.status = 404;
        throw error;
    }
    
    // Security check
    const isBlacklisted = await validateEmailSecurity(subject, body);
    if (isBlacklisted) {
        const error = new Error('Mail contains blacklisted content');
        error.status = 400;
        throw error;
    }
    
    // Create and send email
    const senderMail = createNewMail(user.username, receiver, subject, body);
    const receiverMail = createNewMail(user.username, receiver, subject, body);
    user.inbox.push(senderMail);
    receiverUser.inbox.push(receiverMail);
    
    return newMail;
}

/**
* Get user's emails sorted by date
*/
function getUserMails(userId) {
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    const lastMails = user.inbox.slice();
    lastMails.sort((a, b) => new Date(b.dateCreated) - new Date(a.dateCreated));
    return lastMails.slice(0, 50);
}

/**
* Get specific email by ID
*/
function getMailById(userId, mailId) {
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    const mail = user.inbox.find(mail => mail.id === mailId);
    if (!mail) {
        const error = new Error('Mail not found');
        error.status = 404;
        throw error;
    }
    
    return mail;
}

/**
* Remove email by ID
*/
function removeMail(userId, mailId) {
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    const mailIndex = user.inbox.findIndex(mail => mail.id === mailId);
    if (mailIndex === -1) {
        const error = new Error('Mail not found');
        error.status = 404;
        throw error;
    }
    
    user.inbox.splice(mailIndex, 1);
    return true;
}

/**
* Search emails by term
*/
function searchMails(userId, searchTerm) {
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    return user.inbox.filter(mail =>
        Object.values(mail).some(value =>
            value.toString().toLowerCase().includes(searchTerm.toLowerCase())
        )
    );
}

/**
* Update email content
*/
function updateMail(userId, mailId, subject, body) {
    const user = Users.findUserById(userId);
    if (!user) {
        const error = new Error('User not found');
        error.status = 404;
        throw error;
    }
    
    const mail = user.inbox.find(mail => mail.id === mailId);
    if (!mail) {
        const error = new Error('Mail not found');
        error.status = 404;
        throw error;
    }
    
    if (subject !== undefined) mail.subject = subject;
    if (body !== undefined) mail.body = body;
    
    return mail;
}

/**
* Validate email security (internal helper)
*/
async function validateEmailSecurity(subject, body) {
    const text = `${subject || ''} ${body || ''}`;
    const urlRegex = /^https?:\/\/[^\s]+$/;
    const words = text.split(/\s+/);
    
    for (const word of words) {
        if (!urlRegex.test(word)) continue;
        
        try {
            const response = await sendCommand('GET', word);
            if (response.startsWith("200") && response.includes("True")) {
                return true; // blacklisted content found
            }   
        } catch (error) {
            const validationError = new Error('An error occurred while validating the mail');
            validationError.status = 500;
            throw validationError;
        }
    }
    return false; // no blacklisted content found
}

function createNewMail(sender, receiver, subject, body) {
    const newMail = {
        id: nextEmailId++,
        sender,
        receiver,
        subject,
        body,
        dateCreated: new Date().toISOString()
    };
    return newMail;
}

module.exports = {
    sendNewMail,
    getUserMails,
    getMailById,
    removeMail,
    searchMails,
    updateMail
};