const Users = require('../Models/userModel');
const Email = require('../Models/emailModel');
const { sendCommand } = require('./blacklistClient');
const mongoose = require('mongoose');
// Counter for email IDs (stored in MongoDB in a real application)
let nextEmailId = 1;
/**
* Send a new email with security validation
*/
async function sendNewMail(userId, mailId, receiver, subject, body) {
	const user = await getUserOrThrow(userId);

	const receiverUser = await Users.findUserByUsername(receiver);
	if (!receiverUser) {
		const error = new Error('Receiver not found');
		error.status = 404;
		throw error;
	}
	// Get the sender's draft mail
	const senderMail = await Email.findOne({ id: mailId, user: user._id });
	if (!senderMail) {
		const error = new Error('Draft mail not found');
		error.status = 404;
		throw error;
	}
	senderMail.sender = user.username;
	senderMail.receiver = receiver;
	senderMail.subject = subject;
	senderMail.body = body;
	senderMail.dateCreated = new Date();
	senderMail.labels = ['sent', 'all'];
	senderMail.unread = false;
	await senderMail.save();

	// Add mail ID to user's labels
	const sentLabel = user.labels.find(l => l.name === 'sent');
	const allLabel = user.labels.find(l => l.name === 'all');

	if (sentLabel && !sentLabel.mailIds.includes(senderMail._id)) {
		sentLabel.mailIds.push(senderMail._id);
	}
	if (allLabel && !allLabel.mailIds.includes(senderMail._id)) {
		allLabel.mailIds.push(senderMail._id);
	}
	await user.save();

	// Check security before sending to recipient
	const isBlacklisted = await validateEmailSecurity(user.username, subject, body);

	// Create new mail for receiver
	const receiverMail = new Email({
		id: await getNextEmailId(),
		sender: user.username,
		receiver: receiver,
		subject: subject,
		body: body,
		dateCreated: new Date(),
		labels: [isBlacklisted ? 'spam' : 'inbox', 'all', 'unread'],
		unread: true,
		user: receiverUser._id
	});
	await receiverMail.save();

	// Add mail to receiver's labels
	const labelType = isBlacklisted ? 'spam' : 'inbox';
	const receiverLabelInbox = receiverUser.labels.find(l => l.name === labelType);
	const receiverLabelAll = receiverUser.labels.find(l => l.name === 'all');
	const receiverLabelUnread = receiverUser.labels.find(l => l.name === 'unread');

	if (receiverLabelInbox && !receiverLabelInbox.mailIds.includes(receiverMail._id)) {
		receiverLabelInbox.mailIds.push(receiverMail._id);
	}
	if (receiverLabelAll && !receiverLabelAll.mailIds.includes(receiverMail._id)) {
		receiverLabelAll.mailIds.push(receiverMail._id);
	}
	if (receiverLabelUnread && !receiverLabelUnread.mailIds.includes(receiverMail._id)) {
		receiverLabelUnread.mailIds.push(receiverMail._id);
	}
	await receiverUser.save();

	return senderMail;
}

/**
* Get user's emails sorted by date
*/
async function getUserMails(userId) {
	const user = await getUserOrThrow(userId);

	const allMailIds = [];
	const allLabels = user.labels.filter(label => label.name !== 'unread');
	if (allLabels) {
		allMailIds.push(...allLabels.mailIds);
	}

	const emails = await Email.find({ _id: { $in: allMailIds } }).sort({ dateCreated: -1 }).limit(50);
	return emails;
}

/**
* Get specific email by ID
*/
async function getMailById(userId, mailId) {
	const user = await getUserOrThrow(userId);
	const mail = await Email.findOne({ id: mailId, user: user._id });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	if (mail.unread) {
		mail.unread = false; // Mark as read
		await mail.save();
		const unreadLabel = user.labels.find(label => label.name === 'unread');
		if (unreadLabel) {
			unreadLabel.mailIds = unreadLabel.mailIds.filter(id => id.toString() !== mail._id.toString());
			await user.save();
		}
	}
	return mail;
}

/**
* Remove email by ID
*/
async function removeMail(userId, mailId) {
	const user = await getUserOrThrow(userId);
	const mail = await Email.findOne({ id: mailId, user: user._id });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}

	// Remove mail ID from all labels
	for (const label of user.labels) {
		label.mailIds = label.mailIds.filter(
			id => id.toString() !== mail._id.toString()
		);
	}
	await user.save();

	// Delete the email
	await Email.deleteOne({ _id: mail._id });
	return true;
}

/**
* Search emails by term
*/
async function searchMails(userId, searchTerm) {
	const user = await getUserOrThrow(userId);

	// Get all mail IDs from user's 'all' label
	const allMailIds = [];
	const allLabel = user.labels.find(l => l.name === 'all');
	if (allLabel) {
		allMailIds.push(...allLabel.mailIds);
	}

	// Search in all fields
	const emails = await Email.find({
		_id: { $in: allMailIds },
		$or: [
			{ sender: { $regex: searchTerm, $options: 'i' } },
			{ receiver: { $regex: searchTerm, $options: 'i' } },
			{ subject: { $regex: searchTerm, $options: 'i' } },
			{ body: { $regex: searchTerm, $options: 'i' } }
		]
	});

	return emails;
}

/**
* Update email content
*/
async function updateMail(userId, mailId, receiver, subject, body) {
	const user = await getUserOrThrow(userId);

	const mail = await Email.findOne({ id: mailId, user: user._id });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}

	if (receiver !== undefined) mail.receiver = receiver;
	if (subject !== undefined) mail.subject = subject;
	if (body !== undefined) mail.body = body;

	await mail.save();
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

/**
 * Crreate a new mail draft
 */
async function createNewMail(sender, receiver, subject, body) {
	// Get the next available email ID
	const emailId = await getNextEmailId();

	const user = await Users.findUserByUsername(sender);
	if (!user) {
		const error = new Error('User not found');
		error.status = 404;
		throw error;
	}

	const newMail = new Email({
		id: emailId,
		sender: sender || '',
		receiver: receiver || '',
		subject: subject || '',
		body: body || '',
		dateCreated: new Date(),
		labels: ['drafts', 'all'],
		unread: true,
		user: user._id
	});

	await newMail.save();

	// Add to user's drafts and all labels
	const draftsLabel = user.labels.find(l => l.name === 'drafts');
	const allLabel = user.labels.find(l => l.name === 'all');

	if (draftsLabel) draftsLabel.mailIds.push(newMail._id);
	if (allLabel) allLabel.mailIds.push(newMail._id);

	await user.save();

	return newMail;
}

// Helper function to get the next email ID
async function getNextEmailId() {
	// Find the highest ID and increment
	const highestEmail = await Email.findOne().sort('-id');
	if (highestEmail) {
		return highestEmail.id + 1;
	}
	return 1; // Start at 1 if no emails exist
}

/**
 * Helper function to get user or throw error
 */
async function getUserOrThrow(userId) {
	if (!userId) {
		const error = new Error('User ID is required');
		error.status = 400;
		throw error;
	}
	try {
		const user = await Users.findUserById(userId);
		if (!user) {
			const error = new Error('User not found');
			error.status = 404;
			throw error;
		}
		return user;
	} catch (error) {
		if (error.kind === 'ObjectId') {
			const notFoundError = new Error('Invalid user ID format');
			notFoundError.status = 400;
			throw notFoundError;
		}
		throw error; // Re-throw other errors
	}
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