const EmailService = require('../services/emailService');
/**
* Send a new email
* @param {*} req - The request object containing user ID, receiver, subject, and body.
* @param {*} res - The response object to send the result.
* @returns  {Object} - The created email object or an error message.
*/
async function sendNewMail(req, res) {
    try{
        const userId = req.userId; 
        const mailId = Number(req.params.id);
        const {receiver, subject, body} = req.body;
        
        if (!userId || !receiver || !mailId ) {
            return  res.status(400).json({ message: 'missing required fields' });
        }
        const newMail = await EmailService.sendNewMail(userId, mailId, receiver, subject, body);
        res.set('Location', `/api/mails/${newMail.id}`);
        return res.status(201).json(newMail);
    }
    catch (error) {
        return res.status(error.status || 500).json({ message: error.message  });
    }
}
/**
* Get the last 50 mails from the user's inbox
* @param {*} req - The request object containing user ID.
* @param {*} res - The response object to send the result.
* @returns {Object} - An array of the last 50 mails or an error message.
*/
function getMails(req, res) {
    try{
        const userId = req.userId;
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const mails = EmailService.getUserMails(userId);
        return res.status(200).json(mails);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}

/**
* Get a specific mail by its ID
* @param {*} req - The request object containing user ID and mail ID.
* @param {*} res - The response object to send the result.
* @returns {Object} - The mail object if found or an error message.
*/
function getMailById(req, res) {
    try {
        const userId = req.userId;
        const mailId = Number(req.params.id);
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        const mail = EmailService.getMailById(userId, mailId);
        return res.status(200).json(mail);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}
/**
* Remove a mail by its ID
* @param {*} req - The request object containing user ID and mail ID.
* @param {*} res - The response object to send the result.
* @returns {Object} - A success status or an error message.
*/
function removeMail(req, res) {
    try {
        const userId = req.userId;
        const mailId = Number(req.params.id);
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        EmailService.removeMail(userId, mailId);
        return res.status(204).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}
/**
* Search for mails in the user's inbox based on a search term
* @param {*} req - The request object containing user ID and search term.
* @param {*} res - The response object to send the result.
* @returns {Object} - An array of mails that match the search term or an error message.
*/
function findInMails(req, res) {
    try {
        const userId = req.userId;
        const searchTerm = req.query.q;
        if (!userId || !searchTerm) {
            return res.status(400).json({ message: 'User ID and search term are required' });
        }
        const foundMails = EmailService.searchMails(userId, searchTerm);
        return res.status(200).json(foundMails);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}
/**
* Update a mail by its ID
* @param {*} req - The request object containing user ID, mail ID, subject, and body.
* @param {*} res - The response object to send the result.
* @returns {Object} - The updated mail object or an error message.
*/
function updatemail(req, res) {
    try {
        const userId = req.userId;
        const mailId = Number(req.params.id); 
        const { receiver , subject, body } = req.body;

        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        EmailService.updateMail(userId,  mailId, receiver ,subject, body);
        return res.status(204).end();
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}

function createNewDraft(req, res) {
    try {
        const userId = req.userId;
        const { subject, body, receiver } = req.body;

        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const user = EmailService.getUserOrThrow(userId);
        const newDraft = EmailService.createNewMail(user.username, receiver, subject, body);
        user.mails.set(newDraft.id, newDraft);
        newDraft.labels.push('drafts', 'all');
        user.labels.get('drafts').mailIds.add(newDraft.id);
        user.labels.get('all').mailIds.add(newDraft.id);
        return res.status(201).json(newDraft);
    } catch (error) {
        return res.status(error.status || 500).json({ message: error.message });
    }
}

module.exports = {
    sendNewMail,
    updatemail,
    getMails,
    createNewDraft,
    getMailById,
    removeMail,
    findInMails
}