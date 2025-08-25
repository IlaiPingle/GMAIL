const express = require('express')
var router = express.Router()
const authMiddleware = require('../Middleware/authMiddleware');
const userController = require('../Controllers/userController')
const multer = require ('multer');
const path = require('path');

const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null,  'uploads/');
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname))
  }
})

const upload = multer({ 
	storage: storage,
	 limits: { fileSize: 5 * 1024 * 1024 } // 5MB limit
	})

router.get('/api/users/me', authMiddleware, userController.isSignedIn);
router.post('/api/users', upload.single('picture'), userController.registerUser);
router.get('/api/users/:id', userController.getUser);
router.post('/api/tokens', userController.loginUser);
router.delete('/api/tokens', authMiddleware, userController.logoutUser);
module.exports = router;
