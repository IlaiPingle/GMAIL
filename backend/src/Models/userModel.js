const mongoose = require('mongoose');

const labelSchema = new mongoose.Schema({
	name: {
		type: String,
		required: true,
	},
	mailIds: [{
		type: mongoose.Schema.Types.ObjectId,
		ref: 'Email'
	}]
});

const userSchema = new mongoose.Schema({
	username: {
		type: String,
		required: true,
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
	/*mails: [{
		type: mongoose.Schema.Types.ObjectId,
		ref: 'Email'
	}],*/
	labels: [labelSchema]
});

// System labels are predefined labels that are used in the application.
const SYSTEM_LABELS = ['inbox', 'starred', 'snoozed', 'important', 'chats', 'sent', 'drafts', 'bin', 'spam', 'all', 'scheduled', 'unread'];

userSchema.statics.createUser = async function (username, password, first_name, sur_name, picture) {
	const newUser = new this({
		username,
		password,
		first_name,
		sur_name,
		picture,
		labels: SYSTEM_LABELS.map(labelName => ({
			name: labelName,
			mailIds: []
		}))
	});
	return await newUser.save();
}

// This function find the user by its username and returns the user object.

userSchema.statics.findUserByUsername = function (username) {
	return this.findOne({ username });
}
userSchema.statics.isSystemLabel = function (labelName) {
	return SYSTEM_LABELS.includes(labelName.toLowerCase());
}

const User = mongoose.model('User', userSchema);

module.exports = {
	User,
	SYSTEM_LABELS,
	createUser: User.createUser.bind(User),
	findUserById: (id) => User.findById(id),
	findUserByUsername: User.findUserByUsername.bind(User),
	isSystemLabel: User.isSystemLabel.bind(User)
}
