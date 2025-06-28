const MailsRepository = new Map();

// This variable is used to generate unique IDs for each email entry
let nextEmailId = 1

/**
 * This function creates a new email entry for a user.
 * @param {*} userId - the Id of the user who is creating the email.
 * @param {*} param1 - an object containing the email details.
 * @returns - {Object} - Returns the newly created email object.
 */
function createNewMail(userId , { sender , receiver , subject , body, link = [] }) {
	const newMail = {
		id: nextEmailId++,
		sender,
		receiver,
		subject,
		body,
		dateCreated: new Date(),
		link 
	}
	if (!MailsRepository.has(userId)) {
		MailsRepository.set(userId, []);
	}
	MailsRepository.get(userId).push(newMail);
	return newMail;
}
/**
 * this function finds an email by its ID for a specific user.
 * @param {*} userId - The ID of the user whose email is being searched.
 * @param {*} id - The ID of the email to be found.
 * @returns - {Object|null} - Returns the email object if found, or null if not found.
 */
function findEmailById(userId,id) {
	return (MailsRepository.get(userId) || []).find(mail=> mail.id === parseInt(id));
}

/**
 * this function retrieves the last 50 mails for a given user.
 * @param {*} userId - The ID of the user whose mails are being retrieved.
 * @returns - {Array} - An array of the last 50 mails for the user, sorted by dateCreated in descending order.
 */
function getLastMails(userId) {
	const userMails = (MailsRepository.get(userId) || []).slice();
	userMails.sort((a, b) => b.dateCreated - a.dateCreated);
	return userMails.slice(0,50);
}

/**
 * this function deletes a mail by its ID.
 * @param {*} userId - The ID of the user whose mail is being deleted.
 * @param {*} id - The ID of the mail to be deleted.
 * @returns - {boolean} - Returns true if the mail was found and deleted, false otherwise.
 */
function deleteMailById(userId, id) {
	const userMails = MailsRepository.get(userId) || [];
	const mailIndex = userMails.findIndex(mail => mail.id === parseInt(id)); 
	if (mailIndex != -1){
		userMails.splice(mailIndex, 1);
		return true;
	}
	return false;
}
/**
 * this function edits an existing mail by its ID.
 * It first retrieves the user's mails from the repository.
 * Then, it searches for the mail with the specified ID.
 * If found, it updates the mail with the provided updated fields.
 * 
 * @param {*} userId - The ID of the user whose mail is being edited.
 * @param {*} updatedfeilds  - An object containing the updated fields for the mail.
 * @returns  - The updated mail object if the mail was found and updated.
 * If the mail with the specified ID is not found, it returns null.
 */
function editMailById(userId, updatedfeilds){
	const userMails = MailsRepository.get(userId) || [];
	const mail = findEmailById(userId, updatedfeilds.id);
	if (mailIndex != -1) {
		Object.assign(mail, updatedfeilds);
		return mail;
	}
	return null;
}

/**
 * this function searches for mails by a query string.
 * It checks if any of the mail's properties contain the query string (case-insensitive).
 * @param {*} userId - The ID of the user whose mails are being searched.
 * @param {*} query  - The query string to search for in the mails.
 * @returns {Array}	 - An array of mails that match the query.
 * if no mails match, it returns an empty array.
 */
function searchMails(userId, query) {
	const userMails = MailsRepository.get(userId) || [];
	const filteredMails = userMails.filter(mail =>
		Object.values(mail).some(value => 
			String(value).toLowerCase().includes(query.toLowerCase())
		)
	);
	return filteredMails;
}

module.exports = {
	getLastMails,
	deleteMailById,
	createNewMail,
	findEmailById,
	editMailById,
	searchMails
}