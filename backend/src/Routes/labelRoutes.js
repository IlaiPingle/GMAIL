const express = require('express');
const router = express.Router();
const labelController = require('../Controllers/labelController');

// Label management routes
router.post('/api/labels', labelController.createLabel); // Create a new label
router.get('/api/labels', labelController.getUserLabels); // Get all labels for a user
router.get('/api/labels/:id', labelController.getLabelById); // Get a specific label by ID
router.patch('/api/labels/:id', labelController.updateLabel); // Update a specific label by ID
router.delete('/api/labels/:id', labelController.deleteLabel); // Delete a specific label by ID

module.exports = router;