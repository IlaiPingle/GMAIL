const express = require('express')
const router = express.Router()
const emailController = require('../Controllers/emailController')


router.get('/api/mails', emailController.getMails);
router.post('/api/mails', emailController.sendNewMail);
router.get('/api/mails/search', emailController.findInMails);
router.get('/api/mails/:id', emailController.getMailById);
router.delete('/api/mails/:id', emailController.removeMail);
router.patch('/api/mails/:id', emailController.updatemail);


module.exports = router