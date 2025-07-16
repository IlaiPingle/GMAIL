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
const getMailsByType = async (type) => {
    const url = `${API_URL}/mails/${type}`;
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