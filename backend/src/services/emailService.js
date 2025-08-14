const { findUserByUsername, findUserById } = require('../services/userService');
const Email = require('../Models/emailModel');
const { sendCommand } = require('./blacklistClient');
const mongoose = require('mongoose');

/**
* Send a new email with security validation
*/
async function sendNewMail(userId, mailId, receiver, subject, body) {
	const senderUser = await findUserById(userId);
	const receiverUser = await findUserByUsername(receiver);
	if (!receiverUser) {
		const error = new Error('Receiver not found');
		error.status = 404;
		throw error;
	}
	// Get the sender's draft mail
	const senderMail = await Email.findOneAndUpdate(
		{ owner: userId, _id: mailId }, 
		{
			$set: {
				sender: senderUser.username,
				receiver: receiver,           
				subject: subject ?? '',
				body: body ?? '',
			},
			$addToSet: { labels: { $each: ['sent', 'all'] } },
		},
		{ new: true, lean: true }  
	);
	if (!senderMail) {
		const error = new Error('Draft mail not found');
		error.status = 404;
		throw error;
	}
	
	
	// Check security before sending to recipient
	const isBlacklisted = await validateEmailSecurity(senderMail.sender, subject, body);
	
	// Create new mail for receiver
	await Email.create({
		owner: receiverUser._id,
		sender: senderUser.username,
		receiver: receiver,
		subject: subject ?? "",
		body: body ?? "",
		labels: [isBlacklisted ? "spam" : "inbox", "all", "unread"],
	});
	const { _id, ...rest } = senderMail;
	return { id: _id.toString(), ...rest };
}

/**
* Get user's emails sorted by date
*/
async function getUserMails(userId) {
	const emails = await Email.find({ owner: userId }).sort({ createdAt: -1 }).lean();
	const mailsOut = emails.map(({ _id, ...rest }) => ({
		id: _id.toString(),
		...rest
	}));
	return mailsOut;
}

/**
* Get specific email by ID
*/
async function getMailById(userId, mailId) {
	const mail = await Email.findOneAndUpdate({ owner: userId, _id: mailId },{ $pull: {labels: 'unread'} }, { new: true , timestamps: false });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	const { _id, ...rest } = mail;
	return { id: _id.toString(), ...rest };
}

/**
* Remove email by ID
*/
async function removeMail(userId, mailId) {
	const mail = await Email.findOne({ owner: userId, _id: mailId });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	await Email.deleteOne({ _id: mail._id });
	return true;
}

/**
* Search emails by term
*/
async function searchMails(userId, searchTerm) {
	const emails = await Email.find({
		owner: userId,
		$or: [
			{ sender: { $regex: searchTerm, $options: 'i' } },
			{ receiver: { $regex: searchTerm, $options: 'i' } },
			{ subject: { $regex: searchTerm, $options: 'i' } },
			{ body: { $regex: searchTerm, $options: 'i' } }
		]
	}).sort({ createdAt: -1 }).lean();
	return emails.map(({ _id, ...rest }) => ({
		id: _id.toString(),
		...rest
	}));
}

/**
* Update email content
*/
async function updateMail(userId, mailId, receiver, subject, body) {
	const mail = await Email.findOne({ owner: userId, _id: mailId });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	
	if (receiver !== undefined) mail.receiver = receiver;
	if (subject !== undefined) mail.subject = subject;
	if (body !== undefined) mail.body = body;

	const savedMail = await mail.save();
	const { _id, ...rest } = savedMail.toObject();
	return { id: _id.toString(), ...rest };
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
async function createNewMail(userId ,sender, receiver, subject, body) {
	const newMail = new Email({
		owner: userId,
		sender: sender || '',
		receiver: receiver || '',
		subject: subject || '',
		body: body || '',
		labels: ['drafts', 'all'],
	});
	const savedMail = await newMail.save();
	const { _id, ...rest } = savedMail.toObject();
	return { id: _id.toString(), ...rest };
}


module.exports = {
	sendNewMail,
	getUserMails,
	getMailById,
	removeMail,
	searchMails,
	updateMail,
	createNewMail,
};