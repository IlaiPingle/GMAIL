const express = require('express');
const router = express.Router();
const blacklistController = require('../Controllers/blacklistController');
const authMiddleware = require('../Middleware/authMiddleware');
router.post('/api/blacklist', authMiddleware, blacklistController.addURL);
router.delete('/api/blacklist/:id', authMiddleware, blacklistController.removeURL);

module.exports = router;