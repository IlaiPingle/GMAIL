const { findUserByUsername, findUserById } = require('../services/userService');
const Email = require('../Models/emailModel');
const { sendCommand } = require('./blacklistClient');
const mongoose = require('mongoose');

/**
* Send a new email with security validation
*/
async function sendNewMail(userId, mailId, receiver, subject, body) {
	if (!mongoose.isValidObjectId(mailId)) {
		const error = new Error('Invalid mail ID');
		error.status = 400;
		throw error;
	}
	const senderUser = await findUserById(userId);
	if (!senderUser) {
		const e = new Error('Sender not found');
		e.status = 404;
		throw e;
	}
	if (typeof receiver !== 'string' || !/[^\s]/.test(receiver) || receiver.length < 3 || receiver.length > 50) {
		const error = new Error('Invalid receiver');
		error.status = 400;
		throw error;
	}
	const receiverUser = await findUserByUsername(receiver);
	if (!receiverUser) {
		const error = new Error('Receiver not found');
		error.status = 404;
		throw error;
	}
	const subj = String(subject ?? '');
	const bod = String(body ?? '');
	if (subj.length > 100) {
		const error = new Error('Subject must be at most 100 characters long');
		error.status = 400;
		throw error;
	}
	if (bod.length > 500) {
		const error = new Error('Body must be at most 500 characters long');
		error.status = 400;
		throw error;
	}
	let mail = await Email.findOne({ owner: userId, _id: mailId });
	if (!mail) {
		const error = new Error('Draft mail not found');
		error.status = 404;
		throw error;
	}
	mail.sender = senderUser.username;
	mail.receiver = receiver;
	mail.subject = subj;
	mail.body = bod;
	mail.labels = mail.labels.filter(label => label !== 'drafts');
	if (!mail.labels.includes('sent')) {
		// If not sent, mark as sent
		mail.labels.push('sent');
	}
	if (!mail.labels.includes('all')) {
		mail.labels.push('all');
	}
	await mail.save();

	// Check security before sending to recipient
	const isBlacklisted = await validateEmailSecurity(mail.sender, subject, body);

	// Create new mail for receiver
	await Email.create({
		owner: receiverUser._id,
		sender: senderUser.username,
		receiver: receiver,
		subject: subj,
		body: bod,
		labels: [isBlacklisted ? "spam" : "inbox", "all", "unread"],
	});
	const savedMail = await Email.findOne({ _id: mail._id }).lean();
	const { _id, ...rest } = savedMail;
	return { id: _id.toString(), ...rest };
}

/**
* Get user's emails sorted by date
*/
async function getUserMails(userId) {
	const emails = await Email.find({ owner: userId }).sort({ createdAt: -1 }).limit(50).lean();
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
	const mailObj = mail.toObject ? mail.toObject() : mail;
	const { _id, ...rest } = mailObj;
	return { id: _id.toString(), ...rest, body: rest.body || '' };
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
	const escape = (s) => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	const rx = new RegExp(escape(String(searchTerm)), 'i');
	const emails = await Email.find({
		owner: userId,
		$or: [
			{ sender: rx },
			{ receiver: rx },
			{ subject: rx },
			{ body: rx }
		]
	}).sort({ createdAt: -1 }).limit(50).lean();
	return emails.map(({ _id, ...rest }) => ({
		id: _id.toString(),
		...rest
	}));
}

/**
* Update email content
*/
async function updateMail(userId, mailId, receiver, subject, body) {
	if (!mongoose.isValidObjectId(mailId)) {
		const error = new Error('Invalid mail ID');
		error.status = 400;
		throw error;
	}
	const mail = await Email.findOne({ owner: userId, _id: mailId });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	
	if (receiver !== undefined) {
		if (typeof receiver !== 'string') {
			const error = new Error('Invalid receiver');
			error.status = 400;
			throw error;
		}
		if (receiver !== '' && !/[^\s]/.test(receiver)) {
			const error = new Error('Invalid receiver');
			error.status = 400;
			throw error;
		}
		if (receiver.length > 50) {
			const error = new Error('Receiver must be at most 50 characters long');
			error.status = 400;
			throw error;
		}
		if (receiver !== '') {
			const user = await findUserByUsername(receiver);
			if (!user) {
				const error = new Error('Receiver not found');
				error.status = 404;
				throw error;
			}
		}
		mail.receiver = receiver;
	}
	if (subject !== undefined) {
		if (typeof subject !== 'string') {
			const error = new Error('Invalid subject');
			error.status = 400;
			throw error;
		}
		if (subject.length > 100) {
			const error = new Error('Subject must be at most 100 characters long');
			error.status = 400;
			throw error;
		}
		mail.subject = subject;
	}
	if (body !== undefined) {
		if (typeof body !== 'string') {
			const error = new Error('Invalid body');
			error.status = 400;
			throw error;
		}
		if (body.length > 500) {
			const error = new Error('Body must be at most 500 characters long');
			error.status = 400;
			throw error;
		}
		mail.body = body;
	}

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
	let checked = 0;
	
	for (const word of words) {
		if (checked >= 20) break;
		if (!urlRegex.test(word)) continue;
		checked++;
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
	const subj = String(subject ?? '');
	const bod = String(body ?? '');
	if (subj.length > 100) {
		const error = new Error('Subject must be at most 100 characters long');
		error.status = 400;
		throw error;
	}
	if (bod.length > 500) {
		const error = new Error('Body must be at most 500 characters long');
		error.status = 400;
		throw error;
	}
	if (receiver !== undefined) {
		if (typeof receiver !== 'string') {
			const error = new Error('Invalid receiver');
			error.status = 400;
			throw error;
		}
		if (receiver !== '' && !/[^\s]/.test(receiver) || receiver.length > 50) {
			const error = new Error('Invalid receiver');
			error.status = 400;
			throw error;
		}
	}
	const newMail = new Email({
		owner: userId,
		sender: sender || '',
		receiver: receiver || '',
		subject: subj,
		body: bod,
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