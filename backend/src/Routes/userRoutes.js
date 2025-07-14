const express = require('express')
var router = express.Router()

const userController = require('../Controllers/userController')

router.post('/api/users', userController.registerUser)
router.get('/api/users/:id', userController.getUser)
router.post('/api/tokens', userController.loginUser)

module.exports = router
