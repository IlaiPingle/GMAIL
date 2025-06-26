const { sendCommand } = require('../services/blacklistClient');

async function addURL(req, res) {
    try {
        const { url } = req.body;
        if (!url) {
            return res.status(400).json({ error: 'Bad Request' });
        }
        const response = await sendCommand('POST', url);
        if (response === "201 Created") {
            return res.status(201).json({message : response});
        }
        else if (response === "404 Not Found") {
            return res.status(404).json({error: response});
        }
        return res.status(400).json({ error: response });
    }
    catch (error) {
        return res.status(500).json();
    }
}
async function removeURL(req, res) {
    try {
        const url = req.params.id;
        if (!url) {
            return res.status(400).json({ error: 'URL is required' });
        }
        const response = await sendCommand('DELETE', url);
        if (response === "204 No Content") {
            return res.status(204).json({ message: response });
        } else if (response === "404 Not Found") {
            return res.status(404).json({ error: response });
        }
        return res.status(400).json({ error: response });
    }
    catch (error) {
        return res.status(500).json();
    }
}

async function checkURL(req, res){
    try {
        const { url } = req.body;
        if (!url) {
            return res.status(400).json({ error: 'URL is required' });
        }
        const response = await sendCommand('GET', url);
        if ( response.startsWith("200 OK") ) {
            if (response.includes("False")){
                return res.status(200).json({ isBlacklisted: false });
            }
            return res.status(200).json({ isBlacklisted: true });
        }
        if (response === "404 Not Found") {
            return res.status(404).json({ error: response });
        }
        return res.status(400).json({ error: response });
    }
    catch (error) {
        return res.status(500).json();
    }
}
module.exports = {
    addURL,
    removeURL,
    checkURL
};      