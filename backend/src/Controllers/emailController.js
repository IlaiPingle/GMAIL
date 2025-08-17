const mongoose = require('mongoose');
const EmailService = require('../services/emailService');
const UserService = require('../services/userService');
/**
* Send a new email
* @param {*} req - The request object containing user ID, receiver, subject, and body.
* @param {*} res - The response object to send the result.
* @returns  {Object} - The created email object or an error message.
*/
async function sendNewMail(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		let { receiver, subject, body } = req.body;

		if (!userId || !receiver || !mailId) {
			return res.status(400).json({ message: 'missing required fields' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid mail ID' });
		}
		if (typeof receiver !== 'string' || !/[^\s]/.test(receiver) || receiver.length < 3 || receiver.length > 50) {
			return res.status(400).json({ message: 'Invalid receiver' });
		}
		if (typeof subject !== 'string' && subject !== undefined) {
			return res.status(400).json({ message: 'Invalid subject' });
		}
		if (typeof body !== 'string' && body !== undefined) {
			return res.status(400).json({ message: 'Invalid body' });
		}
		if (subject !== undefined && subject.length > 100) {
			return res.status(400).json({ message: 'Subject must be at most 100 characters long' });
		}
		if (body !== undefined && body.length > 500) {
			return res.status(400).json({ message: 'Body must be at most 500 characters long' });
		}
		const draftMail = await EmailService.getMailById(userId, mailId).catch(() => null);
		const finalSubject = subject !== undefined ? subject : (draftMail?.subject ?? '');
		const finalBody = body !== undefined ? body : (draftMail?.body ?? '');
		if (typeof finalSubject !== 'string' || typeof finalBody !== 'string') {
			return res.status(400).json({ message: 'Invalid draft content' });
		}
		if (finalSubject.length > 100) {
			return res.status(400).json({ message: 'Subject must be at most 100 characters long' });
		}
		if (finalBody.length > 500) {
			return res.status(400).json({ message: 'Body must be at most 500 characters long' });
		}	
		const newMail = await EmailService.sendNewMail(userId, mailId, receiver, finalSubject, finalBody);
		if (typeof res.location === 'function') {
			res.location(`/api/mails/${newMail.id}`);
		}
		return res.status(201).json(newMail);
	} catch (error) {
		console.error('Error sending new mail:', error);
		return res.status(error.status || 500).json({ message: error.message || 'Internal Server Error' });
	}
}
/**
* Get the last 50 mails from the user's inbox
* @param {*} req - The request object containing user ID.
* @param {*} res - The response object to send the result.
* @returns {Object} - An array of the last 50 mails or an error message.
*/
async function getMails(req, res) {
	try {
		const userId = req.userId;
		if (!userId) {
			return res.status(400).json({ message: 'User ID is required' });
		}
		const mails = await EmailService.getUserMails(userId);
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
async function getMailById(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		if (!userId || !mailId) {
			return res.status(400).json({ message: 'User ID and Mail ID are required' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid Mail ID' });
		}
		const mail = await EmailService.getMailById(userId, mailId);
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
async function removeMail(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		if (!userId || !mailId) {
			return res.status(400).json({ message: 'User ID and Mail ID are required' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid Mail ID' });
		}
		await EmailService.removeMail(userId, mailId);
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
async function findInMails(req, res) {
	try {
		const userId = req.userId;
		const searchTerm = req.query.q;
		if (!userId || typeof searchTerm !== 'string' || !/[^\s]/.test(searchTerm)) {
			return res.status(400).json({ message: 'User ID and search term are required' });
		}
		if (searchTerm.length > 200) {
			return res.status(400).json({ message: 'Invalid search term' });
		}
		const foundMails = await EmailService.searchMails(userId, searchTerm);
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
async function updatemail(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		const { receiver, subject, body } = req.body;

		if (!userId || !mailId) {
			return res.status(400).json({ message: 'User ID and Mail ID are required' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid Mail ID' });
		}
		if (receiver !== undefined) {
			if (typeof receiver !== 'string') {
				return res.status(400).json({ message: 'Invalid receiver' });
			}
			if (!/[^\s]/.test(receiver) && receiver !== '') {
				return res.status(400).json({ message: 'Invalid receiver' });
			}
			if ( receiver.length > 50) {
				return res.status(400).json({ message: 'Receiver must be at most 50 characters long' });
			}
		}
		if (subject !== undefined) {
			if (typeof subject !== 'string') {
				return res.status(400).json({ message: 'Invalid subject' });
			}
			if (subject.length > 100) {
				return res.status(400).json({ message: 'Subject must be at most 100 characters long' });
			}
		}
		if (body !== undefined) {
			if (typeof body !== 'string') {
				return res.status(400).json({ message: 'Invalid body' });
			}
			if (body.length > 500) {
				return res.status(400).json({ message: 'Body must be at most 500 characters long' });
			}
		}
		await EmailService.updateMail(userId, mailId, receiver, subject, body);
		return res.status(204).end();
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message });
	}
}

/**
 * Create a new email draft
 * @param {*} req - The request object containing user ID, subject, body, and receiver.
 * @param {*} res - The response object to send the result.
 * @returns {Object} - The created draft email object or an error message.
 */
async function createNewDraft(req, res) {
	try {
		const userId = req.userId;
		const { subject, body, receiver } = req.body;

		if (!userId) {
			return res.status(400).json({ message: 'User ID is required' });
		}
		if (receiver !== undefined) {
			if (typeof receiver !== 'string') {
				return res.status(400).json({ message: 'Invalid receiver' });
			}
			if (!/[^\s]/.test(receiver) && receiver !== '' || receiver.length > 50) {
				return res.status(400).json({ message: 'Invalid receiver' });
			}
		}
		if (subject !== undefined) {
			if (typeof subject !== 'string') {
				return res.status(400).json({ message: 'Invalid subject' });
			}
			if (subject.length > 100) {
				return res.status(400).json({ message: 'Subject must be at most 100 characters long' });
			}
		}
		if (body !== undefined) {
			if (typeof body !== 'string') {
				return res.status(400).json({ message: 'Invalid body' });
			}
			if (body.length > 500) {
				return res.status(400).json({ message: 'Body must be at most 500 characters long' });
			}
		}
		const user = await UserService.findUserById(userId);
		if (!user) {
			return res.status(404).json({ message: 'User not found' });
		}
		const newDraft = await EmailService.createNewMail(userId, user.username, receiver, subject, body);
		return res.status(201).json(newDraft);
	} catch (error) {
		console.error('Error creating new draft:', error);
		return res.status(error.status || 500).json({ message: error.message || 'Internal Server Error' });
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