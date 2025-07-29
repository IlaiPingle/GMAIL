const Users = require('../Models/userModel');
const { sendCommand } = require('./blacklistClient');

let nextEmailId = 1;
/**
* Send a new email with security validation
*/

async function sendNewMail(userId, mailId, receiver, subject, body) {
	const user = getUserOrThrow(userId);

    const receiverUser = Users.findUserByUsername(receiver);
    if (!receiverUser) {
        const error = new Error('Receiver not found');
        error.status = 404;
        throw error;
    }
        // Create mail for Sender:
    const senderMail = getMailById(userId, mailId);
    if (!senderMail) {
        const error = new Error('Draft mail not found');
        error.status = 404;
        throw error;
    }
    senderMail.sender = user.username;
    senderMail.receiver = receiver;
    senderMail.subject = subject;
    senderMail.body = body;
    senderMail.dateCreated = new Date().toISOString();
    senderMail.unread = true;

    senderMail.labels.push('sent', 'all');
    user.labels.get('sent').mailIds.add(senderMail.id);
    user.labels.get('unread').mailIds.add(senderMail.id);
    user.labels.get('all').mailIds.add(senderMail.id);
    // Create mail for Receiver:
    const receiverMail = createNewMail(user.username, receiver, subject, body);
    // Security check
    const isBlacklisted = await validateEmailSecurity(user.username, subject, body);

    receiverMail.labels.push(isBlacklisted ? 'spam': 'inbox', 'all');
    receiverUser.mails.set(receiverMail.id, receiverMail);
    // Add mail to system labels
    receiverUser.labels.get(isBlacklisted ? 'spam': 'inbox').mailIds.add(receiverMail.id);
    receiverUser.labels.get('unread').mailIds.add(receiverMail.id);
    receiverUser.labels.get('all').mailIds.add(receiverMail.id);
    return senderMail;
}

/**
* Get user's emails sorted by date
*/
function getUserMails(userId) {
    const user = getUserOrThrow(userId);

    const lastMails = Array.from(user.mails.values()).slice()
    lastMails.sort((a, b) => new Date(b.dateCreated) - new Date(a.dateCreated));
    return lastMails.slice(0, 50);
}

/**
* Get specific email by ID
*/
function getMailById(userId, mailId) {
    const user = getUserOrThrow(userId);
    const mail = user.mails.get(mailId);
    if (!mail){
        const error = new Error('Mail not found');
        error.status = 404;        
        throw error;
    }
    mail.labels = mail.labels.filter(label => label !== 'unread');
    user.labels.get('unread').mailIds.delete(mailId);
    return mail;
}

/**
* Remove email by ID
*/
function removeMail(userId, mailId) {
    const user = getUserOrThrow(userId);
    const mail = user.mails.get(mailId);
    
    if (mail) {
        // Remove mail from all labels
        mail.labels.forEach(labelName => {
            if (user.labels.has(labelName)) {
                user.labels.get(labelName).mailIds.delete(mailId);
            }
        });
    }
    
    user.mails.delete(mailId);
    return true;
}

/**
* Search emails by term
*/
function searchMails(userId, searchTerm) {
    const user = getUserOrThrow(userId);

    return Array.from(user.mails.values()).filter(mail =>
        Object.values(mail).some(value =>
            value.toString().toLowerCase().includes(searchTerm.toLowerCase())
        )
    );
}

/**
* Update email content
*/
function updateMail(userId, mailId ,receiver, subject, body) {
    const user = getUserOrThrow(userId);

    const mail = user.mails.get(mailId);
    if (!mail) {
        const error = new Error('Mail not found');
        error.status = 404;
        throw error;
    }
    if (receiver !== undefined) mail.receiver = receiver;
    if (subject !== undefined) mail.subject = subject;
    if (body !== undefined) mail.body = body;
    
    return mail;
}

/**
* Validate email security (internal helper)
*/
async function validateEmailSecurity(sender, subject, body) {
    const text = `${sender || ''} ${subject || ''} ${body || ''}`;
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
        receiver: receiver || '',
        subject: subject || '',
        body: body || '',
        dateCreated: new Date().toISOString(),
        labels: ['unread']
    };
    return newMail;
}

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
    sendNewMail,
    getUserMails,
    getMailById,
    removeMail,
    searchMails,
    updateMail,
    createNewMail,
    getUserOrThrow,
};