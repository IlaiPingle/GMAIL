const API_URL = 'http://localhost:8080/api';

const getUserId = () => localStorage.getItem('user-id') || '2'; // Default to '2' if not set

const defaultHeaders = () => ({
    'Content-Type': 'application/json',
    'user-id': getUserId(),
});

const handleResponse = async (response) => {
    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Request failed');
    }
    return response.json();
}

const getAllMails = async () => {
    const url = `${API_URL}/mails`;
    const response = await fetch(url, {headers: defaultHeaders()});
    return handleResponse(response);
}

const getMailsByLabel = async (label) => {
    const url = `${API_URL}/labels/mails?label=${encodeURIComponent(label)}`;
    const response = await fetch(url, {headers: defaultHeaders()});
    return handleResponse(response);
}
const getMailById = async (id) => {
    const url = `${API_URL}/mails/${id}`;
    const response = await fetch(url, {headers: defaultHeaders()});
    return handleResponse(response);
}
const searchMails = async (query) => {
    const url = `${API_URL}/mails/search/${encodeURIComponent(query)}`;
    const response = await fetch(url, {headers: defaultHeaders()});
    return handleResponse(response);
}
const sendMail = async (mailData) => {
    const url = `${API_URL}/mails`;
    const response = await fetch(url, {
        method: 'POST',
        headers: defaultHeaders(),
        body: JSON.stringify(mailData),
    });
    return handleResponse(response);
}

const updateMail = async (id,updatedData) => {
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
    const response = await fetch(url, {headers: defaultHeaders()});
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
    const response = await fetch(url, {headers: defaultHeaders()});
    return handleResponse(response);
}
const reportSpam = async (sender , subject , body) => {
    const url = `${API_URL}/blacklist`;
    const response = await fetch(url, {
        method: 'POST',
        headers: defaultHeaders(),
        body: JSON.stringify({ sender, subject, body }),
    });
    return handleResponse(response);
}
const removeLabelFromMail = async (labelName, mailId) => {
    const url = `${API_URL}/labels/mails/${mailId}`;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: defaultHeaders(),
        body: JSON.stringify({ labelName }),
    });
    return handleResponse(response);
}
const addLabelToMail = async (labelName, mailId) => {
    const url = `${API_URL}/labels/mails/${mailId}`;
    const response = await fetch(url, {
        method: 'POST',
        headers: defaultHeaders(),
        body: JSON.stringify({ labelName }),
    });
    return handleResponse(response);
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
};