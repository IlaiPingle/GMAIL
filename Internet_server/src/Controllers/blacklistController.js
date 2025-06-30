const { sendCommand } = require('../services/blacklistClient');
const { findUserById} = require('../Models/userModel');
async function addURL(req, res) {
    try {
        const userId = req.header('user-id');
        if (findUserById(Number(userId)) === undefined) {
            return res.status(404).json({ error: 'User not found' });
        }
        const { url } = req.body;
        if (!url) {
            return res.status(400).json({ error: 'Bad Request (empty URL)' });
        }
        const response = await sendCommand('POST', url);
        if (response === "201") {
            return res.status(201).json({ message: "URL added to blacklist" });
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
        return res.status(500).json({ error: "Internal Server Error" });
    }
}
async function removeURL(req, res) {
    try {
        const userId = req.header("user-id");
        if (findUserById(Number(userId)) === undefined) {
            return res.status(404).json({ error: "User not found" });
        }
        const url = req.params.id;
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

async function checkURL(req, res){
    try {
        const { url } = req.body;
        if (!url) {
            return res.status(400).json({ error: 'URL is required' });
        }
        const response = await sendCommand('GET', url);
        if ( response.startsWith("200") ) {
            if (response.includes("False")){
                return res.status(200).json({ isBlacklisted: false });
            }
            return res.status(200).json({ isBlacklisted: true });
        }
        if (response === "404") {
            return res.status(404).json({ error: "Not Found" });
        }
        return res.status(400).json({ error: "Bad Request" });
    }
    catch (error) {
        return res.status(500).json({ error: "Internal Server Error" });
    }
}
module.exports = {
    addURL,
    removeURL,
    checkURL
};      