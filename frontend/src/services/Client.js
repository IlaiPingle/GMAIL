const API_URL = 'http://localhost:8080/api';

const defaultHeaders = () => ({
	'Content-Type': 'application/json',
});

const handleResponse = async (response) => {
    if (!response.ok) {
        let errorMessage = 'Request failed';
        try {
            const errorData = await response.json();
            errorMessage = errorData.message || errorMessage;
        } catch (e) {}
        throw new Error(errorMessage);    
    }
    if (response.status === 204) {
        return null; // No content
    }
    return response.json();
}

const getAllMails = async () => {
	const url = `${API_URL}/mails`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' }); // Include cookies for session management
	return handleResponse(response);
}

const getMailsByLabel = async (label) => {
	const url = `${API_URL}/labels/mails?label=${encodeURIComponent(label)}`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' }); // Include cookies for session management
	return handleResponse(response);
}
const getMailById = async (id) => {
	const url = `${API_URL}/mails/${id}`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' }); // Include cookies for session management
	return handleResponse(response);
}
const searchMails = async (query) => {
	const url = `${API_URL}/mails/search/${encodeURIComponent(query)}`;
	const response = await fetch(url, { headers: defaultHeaders(), credentials: 'include' }); // Include cookies for session management
	return handleResponse(response);
}
const sendMail = async (mailId, mailData) => {
    const url = `${API_URL}/mails/${mailId}`;
    const response = await fetch(url, {
        method: 'POST',
        headers: defaultHeaders(),
        body: JSON.stringify(mailData),
    });
    return handleResponse(response);
}

const updateMail = async (id ,updatedData) => {
    const url = `${API_URL}/mails/${id}`;
    const response = await fetch(url, {
        method: 'PATCH',
        headers: defaultHeaders(),
        body: JSON.stringify(updatedData),
    });
    return handleResponse(response);
}

const deleteMail = async (id) => {
	const url = `${API_URL}/mails/${id}`;
	const response = await fetch(url, {
		method: 'DELETE',
		headers: defaultHeaders(),
	});
	return handleResponse(response);
}

const createLabel = async (labelName) => {
	const url = `${API_URL}/labels`;
	const response = await fetch(url, {
		method: 'POST',
		headers: defaultHeaders(),
		body: JSON.stringify({ labelName }),
	});
	return handleResponse(response);
}

const getLabels = async () => {
	const url = `${API_URL}/labels`;
	const response = await fetch(url, { headers: defaultHeaders() });
	return handleResponse(response);
}

const deleteLabel = async (labelName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, {
		method: 'DELETE',
		headers: defaultHeaders(),
	});
	return handleResponse(response);
}
const updateLabel = async (labelName, newLabelName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, {
		method: 'PATCH',
		headers: defaultHeaders(),
		body: JSON.stringify({ newLabelName }),
	});
	return handleResponse(response);
}

const getLabelByName = async (labelName) => {
	const url = `${API_URL}/labels/${encodeURIComponent(labelName)}`;
	const response = await fetch(url, { headers: defaultHeaders() });
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
        body: JSON.stringify({ labelName }),
    });
    return handleResponse(response);
}
const createDraft = async (draftData) => {
    const url = `${API_URL}/mails`;
    const response = await fetch(url, {
        method: 'POST',
        headers: defaultHeaders(),
        body: JSON.stringify(draftData),
    });
    return handleResponse(response);
}

const login = async (username, password) => {
	const url = `${API_URL}/tokens`;
	try {
		const response = await fetch(url, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			credentials: 'include', // Include cookies for session management
			body: JSON.stringify({ username, password }),
		});
		if (!response.ok) {
			const errorData = await response.json();
			throw new Error(errorData.message || 'Login failed');
		}
		const data = await response.json();
		return {
			username: data.username,
			id: data.id,
		}; // Return username and id
	} catch (error) {
		console.error('Login error: ', error);
		throw error; // Propagate the error
	}
}

const logout = async () => {
		const url = `${API_URL}/tokens/`;
		await fetch(url, {
			method: 'DELETE',
			credentials: 'include',
		});
	}

	export default {
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
		login,
		logout,
	};
