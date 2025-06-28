const EmailModel = require('../Models/emailModel')
const { isValidEmail } = require('../utils/emailValidator')
const Users = require('../Models/userModel');


async function sendNewMail(req, res) {
    const userId = req.headers.userId;
    const { sender, receiver, subject, body, link } = req.body;
    if (!userId || !sender || !receiver ) {
        return  res.status(400).json({ message: 'missing required fields' });
    }
    try {
        const await 
    }

/**
* Verify an email subscription
*/
exports.verifyEmail = (req, res) => {
    const id = parseInt(req.params.id, 10)
    
    // Attempt to verify
    const verified = EmailModel.verifyEmail(id)
    
    if (verified) {
        return res.status(200).json({ message: 'Email verified successfully' })
    } else {
        return res.status(404).json({ message: 'Email subscription not found' })
    }
}

/**
* Get all verified emails
*/
exports.getVerifiedEmails = (req, res) => {
    const verifiedEmails = EmailModel.getVerifiedEmails()
    return res.status(200).json(verifiedEmails)
}

function getMails(req, res) {
    try{
        const userId = req.headers.userId;
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const usersMails = EmailModel.getLastMails(userId);
        return res.status(200).json(usersMails);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while retrieving mails'});
    }
}
function getMailById(req, res) {
    try {
        const userId = req.headers.userId;
        const mailId = req.params.id;
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        const mail = EmailModel.findEmailById(userId, mailId);
        if (!mail) {
            return res.status(404).json({ message: 'Mail not found' });
        }
        return res.status(200).json(mail);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while retrieving the mail' });
    }
}
function removeMail(req, res) {
    try {
        const userId = req.headers.userId;
        const mailId = req.params.id;
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        const deleted = EmailModel.deleteMailById(userId, mailId);
        if (!deleted) {
            return res.status(404).json({ message: 'Mail not found' });
        }
        return res.status(204).end();
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while deleting the mail' });
    }
}

function findInMails(req, res) {
    try {
        const userId = req.headers.userId;
        const searchTerm = req.query.q;
        if (!userId || !searchTerm) {
            return res.status(400).json({ message: 'User ID and search term are required' });
        }
        const foundMails = EmailModel.searchMails(userId, searchTerm);
        return res.status(200).json(foundMails);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while searching for mails' });
    }
}

module.exports = {
    getMails,
    getMailById,
    removeMail,
    findInMails
}

        
        