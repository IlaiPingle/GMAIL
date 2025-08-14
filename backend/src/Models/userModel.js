const mongoose = require('mongoose');
const { Schema } = mongoose;

const labelSchema = new Schema({
	name: {
		type: String,
		required: true,
	}
});

const userSchema = new Schema({
	username: {
		type: String,
		required: true,
		index: true,
		unique: true
	},
	password: {
		type: String,
		required: true
	},
	first_name: {
		type: String,
		required: true
	},
	sur_name: {
		type: String,
		required: true
	},
	picture: String,
	labels: {
		type: [labelSchema],
		default: []
	}
});

// System labels are predefined labels that are used in the application.
const SYSTEM_LABELS = ['inbox', 'starred', 'snoozed', 'important', 'chats', 'sent', 'drafts', 'bin', 'spam', 'all', 'scheduled', 'unread'];




const User = mongoose.model('User', userSchema);

module.exports = {
	User,
	SYSTEM_LABELS
};
