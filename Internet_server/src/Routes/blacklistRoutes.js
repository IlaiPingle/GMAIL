const express = require('express');
const router = express.Router();
const blacklistController = require('../Controllers/blacklistController');

router.post('/api/blacklist', blacklistController.addURL);
router.delete('/api/blacklist/:id', blacklistController.removeURL);
router.get('/api/blacklist', blacklistController.checkURL);

module.exports = router;