const express = require('express')
var router = express.Router()
const authMiddleware = require('../Middleware/authMiddleware');

const userController = require('../Controllers/userController')

router.get('/api/users/me', authMiddleware, userController.isSignedIn);
router.post('/api/users', userController.registerUser);
router.get('/api/users/:id', userController.getUser);
router.post('/api/tokens', userController.loginUser);
router.delete('/api/tokens', authMiddleware, userController.logoutUser);
module.exports = router;
