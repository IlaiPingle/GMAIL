const send = require('send');
const EmailModel = require('../Models/emailModel')
const Users = require('../Models/userModel');
const { sendCommand } = require('../services/blacklistClient');


async function sendNewMail(req, res) {
    const userId = Number(req.header("user-id"));
    const {receiver, subject, body} = req.body;
    if (!userId || !receiver ) {
        return  res.status(400).json({ message: 'missing required fields' });
    }
    const user = Users.findUserById(userId);
    if (!user) {
        return res.status(404).json({ message: 'User not found' });
    }
    const receiverUser = Users.findUserByUsername(receiver);
    if (!receiverUser) {
        return res.status(404).json({ message: 'Receiver not found' });
    }
    try {
        const text = `${subject} ${body}`;
        const urlRegex = /^https?:\/\/[^\s]+$/ ;
        const words = text.split(/\s+/);
        for (const word of words) {
            if (!urlRegex.test(word)) continue;
            try {
                const response = await sendCommand('GET', word);
                if (response.startsWith("200") && response.includes("True")){
                    return res.status(400).json({ message: 'Mail contains blacklisted content' });                       
                }
            } catch (error) {
                return res.status(500).json({ message: 'An error occurred while validating the mail' });
            }
        }
        const newMail = EmailModel.createNewMail(user.username , receiver, subject, body);
        user.inbox.push(newMail);
        receiverUser.inbox.push(newMail);
        return res.status(201).json(newMail);
    }
    catch (error) {
        return res.status(500).json({ message: 'An error occurred while sending the mail' });
    }
}

function getMails(req, res) {
    try{
        const userId = Number(req.header("user-id"));
        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const usersInbox = (Users.findUserById(userId)).inbox;
        const lastMails = usersInbox.slice(); 
        lastMails.sort((a, b) => new Date(b.date) - new Date(a.date));
        lastMails.slice(0, 50);
        return res.status(200).json(lastMails);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while retrieving mails'});
    }
}
function getMailById(req, res) {
    try {
        const userId = Number(req.header("user-id"));
        const mailId = Number(req.params.id);
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        const usersInbox = (Users.findUserById(userId)).inbox;
        const mail = usersInbox.find(mail => mail.id === mailId);
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
        const userId = Number(req.header("user-id"));
        const mailId = Number(req.params.id);
        if (!userId || !mailId) {
            return res.status(400).json({ message: 'User ID and Mail ID are required' });
        }
        const usersInbox = (Users.findUserById(userId)).inbox;
        const MailIndex = usersInbox.findIndex(mail => mail.id === mailId);
        if (MailIndex === -1) {
            return res.status(404).json({ message: 'Mail not found' });
        }
        usersInbox.splice(MailIndex ,1);
        return res.status(204).end();
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while deleting the mail' });
    }
}

function findInMails(req, res) {
    try {
        const userId = Number(req.header("user-id"));
        const searchTerm = req.query.q;
        if (!userId || !searchTerm) {
            return res.status(400).json({ message: 'User ID and search term are required' });
        }
        const usersInbox = (Users.findUserById(userId)).inbox;
        const foundMails = usersInbox.filter(mail =>Object.values(mail).some(value =>
            value.toString().toLowerCase().includes(searchTerm.toLowerCase())
        ));
        return res.status(200).json(foundMails);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while searching for mails' });
    }
}
function updatemail(req, res) {
    try {
        const userId = Number(req.header("user-id"));
        const mailId = Number(req.params.id);
        const { subject, body } = req.body;
        const usersInbox = (Users.findUserById(userId)).inbox;
        if(!usersInbox){
            return res.status(404).json({ message: 'USER not found' });
        }
        const mail = usersInbox.find(mail => mail.id === mailId);
        if (!mail) {
            return res.status(404).json({ message: 'Mail not found' });
        }
        mail.subject = subject;
        mail.body = body;
        return res.status(201).json(mail);
    } catch (error) {
        return res.status(500).json({ message: 'An error occurred while updating the mail' });
    }
}
module.exports = {
    sendNewMail,
    updatemail,
    getMails,
    getMailById,
    removeMail,
    findInMails
}