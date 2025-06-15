const express = require('express')
var router = express.Router()

const userController = require('../Controllers/userController')
router.post('/', userController.registerUser)

module.exports = router
