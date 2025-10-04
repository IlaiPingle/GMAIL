
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const defaultHeaders = () => ({
	'Content-Type': 'application/json',
});

const handleResponse = async (response) => {
	const contentType = response.headers.get('Content-Type');
	const text = await response.text();

	const parseBody = () => {
		if(!text) 	return null;
		if (contentType && contentType.includes('application/json')) {
			try {
				return JSON.parse(text);
			} catch (e) {
				return null;
			}
		}
		return text; 
	};
	const data = parseBody();
	if (!response.ok) {
		const message = (data && data.message) || response.statusText || 'request failed';
		throw new Error(message);
	}
	return data;
}

const getAllMails = async () => {
	const url = `${API_URL}/mails`;
	const response = await fetch(url, {
		headers: defaultHeaders(),
		credentials: 'include'
	});
	return handleResponse(response);
}

const getMailsByLabel = async (label) => {
	const url = `${API_URL}/labels/mails?label=${encodeURIComponent(label)}`;
	const response = await fetch(url, {
		headers: defaultHeaders(),
		credentials: 'include'
	});
	return handleResponse(response);
}
const getMailById = async (id) => {
	const url = `${API_URL}/mails/${id}`;
	const response = await fetch(url, {
		headers: defaultHeaders(),
		credentials: 'include'
	});
	return handleResponse(response);
}
const searchMails = async (query) => {
	const url = `${API_URL}/mails/search?q=${encodeURIComponent(query)}`;
	const response = await fetch(url, {
		headers: defaultHeaders(),
		credentials: 'include'
	});
	return handleResponse(response);
}
const sendMail = async (mailId, mailData) => {
	const url = `${API_URL}/mails/${mailId}`;
	const response = await fetch(url, {
		method: 'POST',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify(mailData),
	});
	return handleResponse(response);
}

const updateMail = async (id ,updatedData) => {
	const url = `${API_URL}/mails/${id}`;
	const response = await fetch(url, {
		method: 'PATCH',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify(updatedData),
	});
	return handleResponse(response);
}

const deleteMail = async (id) => {
	const url = `${API_URL}/mails/${id}`;
	const response = await fetch(url, {
		method: 'DELETE',
		headers: defaultHeaders(),
		credentials: 'include',
	});
	return handleResponse(response);
}

const createLabel = async (labelName) => {
	const url = `${API_URL}/labels`;
	const response = await fetch(url, {
		method: 'POST',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify({ labelName }),
	});
	return handleResponse(response);
}

const getLabels = async () => {
	const url = `${API_URL}/labels`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' });
	return handleResponse(response);
}

const deleteLabel = async (labelName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, {
		method: 'DELETE',
		headers: defaultHeaders(),
		credentials: 'include',
	});
	return handleResponse(response);
}
const updateLabel = async (labelName, newName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, {
		method: 'PATCH',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify({ newName }),
	});
	return handleResponse(response);
}

const getLabelByName = async (labelName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' });
	return handleResponse(response);
}
const reportSpam = async ({mailData}) => {
	const { sender, subject, body } = mailData;
	const url = `${API_URL}/blacklist`;
	const text = `${subject || ''} ${body || ''} sender: ${sender || ''}`;
	const urlRegex = /^https?:\/\/[^\s]+$/;
	const words = text.split(/\s+/);
	for (const word of words) {
		if (!urlRegex.test(word)) continue;
		const response = await fetch(url, {
			method: 'POST',
			headers: defaultHeaders(),
			credentials: 'include',
			body: JSON.stringify({url: word}),
		});
		await handleResponse(response);
	}
}
const removeLabelFromMail = async (labelName, mailId) => {
	if (!mailId) {
		throw new Error("Mail ID is missing");
	}
	const url = `${API_URL}/labels/mails/${mailId}`;
	const response = await fetch(url, {
		method: 'DELETE',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify({ labelName }),
	});
	return handleResponse(response);
}
const addLabelToMail = async (labelName, mailId) => {
	if (!mailId) {
		throw new Error("Mail ID is missing");
	}
	const url = `${API_URL}/labels/mails/${mailId}`;
	const response = await fetch(url, {
		method: 'POST',
		headers: defaultHeaders(),
		credentials: 'include',
		body: JSON.stringify({ labelName }),
	});
	return handleResponse(response);
}
const createDraft = async (draftData) => {
	const url = `${API_URL}/mails`;
	const response = await fetch(url, {
		method: 'POST',
		headers: defaultHeaders(),
		credentials: 'include', 
		body: JSON.stringify(draftData),
	});
	return handleResponse(response);
}

const login = async (username, password) => {
	const url = `${API_URL}/tokens`;
	const response = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		credentials: 'include', 
		body: JSON.stringify({ username, password }),
	});
	return await handleResponse(response);
}
const register = async (userData) => {
	const url = `${API_URL}/users`;
	const response = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(userData),
	});
	return await handleResponse(response);
}
const getCurrentUser = async () => {
	const url = `${API_URL}/users/me`;
	const response = await fetch(url, {
		headers: defaultHeaders(),
		credentials: 'include',
	});
	return handleResponse(response);
}

const logout = async () => {
	const url = `${API_URL}/tokens`;
	const response = await fetch(url, {
		method: 'DELETE',
		credentials: 'include',
	});
	return handleResponse(response);
}

const Client = {
	getAllMails,
	getMailsByLabel,
	getMailById,
	searchMails,
	sendMail,
	updateMail,
	deleteMail,
	createLabel,
	getLabels,
	deleteLabel,
	updateLabel,
	getLabelByName,
	addLabelToMail,
	removeLabelFromMail,
	reportSpam,
	createDraft,
	login,
	register,
	logout,
	getCurrentUser,
};

export default Client;
