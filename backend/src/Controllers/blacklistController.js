const { sendCommand } = require('../services/blacklistClient');
const { findUserById} = require('../Models/userModel');


/**
 * Add a URL to the blacklist
 * @param {*} req - The request object containing user ID and URL to be added.
 * @param {*} res - The response object to send the result.
 * @returns {Object} - A success message or an error message.
 */
async function addURL(req, res) {
    try {
        const userId = req.userId;
        if (findUserById(Number(userId)) === undefined) {
            return res.status(404).json({ error: 'User not found' });
        }
        const { url } = req.body;
        if (!url) {
            return res.status(400).json({ error: 'NO URL provided!' });
        }
        const response = await sendCommand('POST', url);
        if (response === "201") {
            const newURL = encodeURIComponent(url);
            res.set('Location', `/api/blacklist/${newURL}`);
            return res.status(201).end();
        }
        else if (response === "500") {
            return res.status(500).json({error: "Internal Server Error (blacklist service)"});
        }
        else if (response === "404") {
            return res.status(404).json({ error: "Not Found (blacklist service)" });
        }
        return res.status(400).json({ error: "Bad Request (blacklist service)"});
    }
    catch (error) {
        return res.status(500).json({ error: "Web server ERROR!" });
    }
}
/**
 * Remove a URL from the blacklist
 * @param {*} req - The request object containing user ID and URL to be removed.
 * @param {*} res - The response object to send the result.
 * @returns  {Object} - A success status or an error message.
 */
async function removeURL(req, res) {
    try {
        const userId = req.userId;
        if (findUserById(Number(userId)) === undefined) {
            return res.status(404).json({ error: "User not found" });
        }
        const url = decodeURIComponent(req.params.id);
        if (!url) {
            return res.status(400).json({ error: 'URL is required' });
        }
        const response = await sendCommand('DELETE', url);
        if (response === "204") {
            return res.status(204).end();
        }
        else if (response === "404") {
            return res.status(404).json({error: "Not Found (blacklist service)"});
        }
        return res.status(400).json({ error: "Bad Request (blacklist service)" });
    }
    catch (error) {
        return res.status(500).json( { error: "Internal Server Error" });
    }
}


module.exports = {
    addURL,
    removeURL,
};      