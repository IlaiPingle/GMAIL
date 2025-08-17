const LabelService = require('../services/labelService');
const mongoose = require('mongoose');

/**
* Create a new label
*/
async function createLabel(req, res) {
	try {
		const userId = req.userId
		const { labelName } = req.body;

		if (!userId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID and label name are required' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		const newLabel = await LabelService.createLabel(userId, labelName);
		res.set('Location', `/api/labels/${encodeURIComponent(newLabel.name)}`);
		return res.status(201).json(newLabel);
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while creating the label' });
	}
}

/**
* Get all labels for a user
*/
async function getUserLabels(req, res) {
	try {
		const userId = req.userId; // Get user ID from JWT token
		const labels = await LabelService.getUserLabels(userId);
		return res.status(200).json(labels);
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving labels' });
	}
}

/**
* Get a specific label by ID
*/
async function getLabelById(req, res) {
	try {
		const userId = req.userId;
		const labelName = req.params.id;

		if (!userId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID and label ID are required' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		const label = await LabelService.getLabelByName(userId, labelName);
		return res.status(200).json(label);
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving the label' });
	}
}

/**
* Update a label
*/
async function updateLabel(req, res) {
	try {
		const userId = req.userId;
		const labelName = req.params.id;
		const { newName } = req.body;

		if (!userId || typeof labelName !== 'string' || typeof newName !== 'string' || !/[^\s]/.test(newName)) {
			return res.status(400).json({ message: 'User ID, label ID, and new name are required' });
		}
		if (newName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		await LabelService.updateLabel(userId, labelName, newName);
		return res.status(204).end();
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while updating the label' });
	}
}

/**
* Delete a label
*/
async function deleteLabel(req, res) {
	try {
		const userId = req.userId;
		const labelName = req.params.id;

		if (!userId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID and label ID are required' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		await LabelService.deleteLabel(userId, labelName);
		return res.status(204).end();
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while deleting the label' });
	}
}

/**
 * Get mails by label
 * @param {*} req - The request object containing user ID and label name.
 * @param {*} res - The response object to send the result.
 * @returns {Object} - An array of mails associated with the label or an error message.
 */
async function getMailsByLabel(req, res) {
	try {
		const userId = req.userId;
		const labelName = req.query.label;
		if (!userId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID and label name are required' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		const mails = await LabelService.getMailsByLabel(userId, labelName);
		return res.status(200).json(mails);
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while retrieving mails by label' });
	}
}

/**
 * Remove a label from a mail
 * @param {*} req - The request object containing user ID, mail ID, and label name.
 * @param {*} res - The response object to send the result.
 * @returns {Object} - A success status or an error message.
 */
async function removeLabelFromMail(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		const { labelName } = req.body;

		if (!userId || !mailId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID, mail ID, and label name are required' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid Mail ID' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		await LabelService.removeLabelFromMail(userId, mailId, labelName);
		return res.status(204).end();
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while removing the label from mails' });
	}
};

/**
 * Add a label to a mail
 * @param {*} req - The request object containing user ID, mail ID, and label name.
 * @param {*} res - The response object to send the result.
 * @returns {Object} - A success status or an error message.
 */
async function addLabelToMail(req, res) {
	try {
		const userId = req.userId;
		const mailId = req.params.id;
		const { labelName } = req.body;

		if (!userId || !mailId || typeof labelName !== 'string' || !/[^\s]/.test(labelName)) {
			return res.status(400).json({ message: 'User ID, mail ID, and label name are required' });
		}
		if (!mongoose.isValidObjectId(mailId)) {
			return res.status(400).json({ message: 'Invalid Mail ID' });
		}
		if (labelName.length > 30) {
			return res.status(400).json({ message: 'Label name must be at most 30 characters long' });
		}
		await LabelService.addLabelToMail(userId, mailId, labelName);
		return res.status(204).end();
	} catch (error) {
		return res.status(error.status || 500).json({ message: error.message || 'An error occurred while adding the label to the mail' });
	}
}

module.exports = {
	createLabel,
	getUserLabels,
	getLabelById,
	updateLabel,
	deleteLabel,
	getMailsByLabel,
	removeLabelFromMail,
	addLabelToMail
};