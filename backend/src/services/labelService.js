const Users = require('../Models/userModel');
const Email = require('../Models/emailModel');

/**
* Create a new label for a user
*/
async function createLabel(userId, labelName) {
	const user = await getUserOrThrow(userId); // Ensure user exists
	if (user.labels.some(label => label.name === labelName)) { // Check if label already exists
		const error = new Error('Label already exists');
		error.status = 400;
		throw error;
	}
	user.labels.push({ name: labelName, mailIds: [] });
	await user.save(); // Save the user with the new label
	return { name: labelName }; // Return the created label
}

/**
* Get all labels for a user
*/
async function getUserLabels(userId) {
	const user = await getUserOrThrow(userId);
	return user.labels
		.filter(label => !Users.isSystemLabel(label.name)) // Filter out system labels
		.map(label => label.name); // Return only label names
}

/**
* Get a specific label by name
*/
async function getLabelByName(userId, labelName) {
	const user = await getUserOrThrow(userId);
	const label = user.labels.find(label => label.name === labelName);
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
async function updateLabel(userId, labelName, newName) {
	const user = await getUserOrThrow(userId);

	// Check if label exists
	const labelIndex = user.labels.findIndex(l => l.name === labelName);
	if (labelIndex === -1) {
		const error = new Error('Label not found');
		error.status = 404;
		throw error;
	}

	// Check if new label name already exists
	if (user.labels.some(l => l.name === newName)) {
		const error = new Error('Label with this name already exists');
		error.status = 400;
		throw error;
	}

	// Get mail IDs associated with this label
	const mailIds = user.labels[labelIndex].mailIds;

	// Update label name
	user.labels[labelIndex].name = newName;
	await user.save();

	// Update label name in emails
	for (const mailId of mailIds) {
		const mail = await Email.findById(mailId);
		if (mail) {
			mail.labels = mail.labels.map(label => label === labelName ? newName : label);
			await mail.save();
		}
	}

	return true;
}

/**
* Delete a label
*/
async function deleteLabel(userId, labelName) {
	const user = await getUserOrThrow(userId);
	const labelIndex = user.labels.findIndex(label => label.name === labelName);
	if (labelIndex === -1) {
		const error = new Error('Label not found');
		error.status = 404;
		throw error;
	}
	// Get mail IDs associated with this label
	const mailIds = user.labels[labelIndex].mailIds;
	user.labels.splice(labelIndex, 1); // Remove the label from the user's labels
	await user.save();

	// Remove the label from all associated mails
	for (const mailId of mailIds) {
		const mail = await Email.findById(mailId);
		if (mail) {
			mail.labels = mail.labels.filter(label => label !== labelName);
			await mail.save();
		}
	}
	return true;
}

async function addLabelToMail(userId, mailId, labelName) {
	const user = await getUserOrThrow(userId);
	const mail = await Email.findOne({ _id: mailId, user: user._id });
	// Check if mail exists
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	// Check if label exists
	const label = user.labels.find(label => label.name === labelName);
	if (!label) {
		const error = new Error('Label not found');
		error.status = 404;
		throw error;
	}
	// Add label to the mail if it doesn't already exist
	if (!mail.labels.includes(labelName)) {
		mail.labels.push(labelName);
		await mail.save();
	}
	// Add mail ID to the label's mailIds if it doesn't already exist
	if (!label.mailIds.some(id => id.toString() === mailId.toString())) {
		label.mailIds.push(mail._id);
		await user.save();
	}
	return true;
}
/**
 * Remove a label from a mail
 */
async function removeLabelFromMail(userId, mailId, labelName) {
	const user = await getUserOrThrow(userId);
	const mail = await Email.findOne({ id: mailId, userId: user._id });
	if (!mail) {
		const error = new Error('Mail not found');
		error.status = 404;
		throw error;
	}
	// Remove label from the mail
	mail.labels = mail.labels.filter(label => label !== labelName);
	await mail.save();

	const label = user.labels.find(label => label.name === labelName);
	if (label) {
		// Remove mail ID from the label's mailIds
		label.mailIds = label.mailIds.filter(id => id.toString() !== mailId.toString());
		await user.save();
	}
	return true;
}
/**
 * Get all mails associated with a specific label for a user
 */
async function getMailsByLabel(userId, labelName) {
	const user = await getUserOrThrow(userId);
	// Find the label by name
	const label = user.labels.find(label => label.name === labelName);
	if (!label) {
		const error = new Error('Label not found');
		error.status = 404;
		throw error;
	}
	// Fetch mails associated with the label
	const mails = await Email.find({ _id: { $in: label.mailIds } }).sort({ dateCreated: -1 });
	return mails;
}

/**
* Helper function to get a user or throw an error
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
			const notFoundError = new Error('User not found');
			notFoundError.status = 404;
			throw notFoundError;
		}
		throw error;
	}
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