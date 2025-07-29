const express = require('express')
const router = express.Router()
const emailController = require('../Controllers/emailController')
const authMiddleware = require('../Middleware/authMiddleware');

router.get('/api/mails', authMiddleware, emailController.getMails);
router.post('/api/mails', authMiddleware, emailController.createNewDraft);
router.get('/api/mails/search', authMiddleware, emailController.findInMails);
router.post('/api/mails/:id', authMiddleware, emailController.sendNewMail);
router.get('/api/mails/:id', authMiddleware, emailController.getMailById);
router.delete('/api/mails/:id', authMiddleware, emailController.removeMail);
router.patch('/api/mails/:id', authMiddleware, emailController.updatemail);
module.exports = router