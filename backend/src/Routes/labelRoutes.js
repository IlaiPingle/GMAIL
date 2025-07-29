const express = require('express');
const router = express.Router();
const labelController = require('../Controllers/labelController');
const authMiddleware = require('../Middleware/authMiddleware');
// Label management routes
router.post('/api/labels', authMiddleware, labelController.createLabel); // Create a new label
router.get('/api/labels', authMiddleware, labelController.getUserLabels); // Get all labels for a user
router.get('/api/labels/mails', authMiddleware, labelController.getMailsByLabel); // Get mails by label
router.delete('/api/labels/mails/:id', authMiddleware, labelController.removeLabelFromMail); // Remove a label from mails
router.post('/api/labels/mails/:id', authMiddleware, labelController.addLabelToMail); // Add a label to a mail
router.get('/api/labels/:id', authMiddleware, labelController.getLabelById); // Get a specific label by ID
router.patch('/api/labels/:id', authMiddleware, labelController.updateLabel); // Update a specific label by ID
router.delete('/api/labels/:id', authMiddleware, labelController.deleteLabel); // Delete a specific label by ID
module.exports = router;