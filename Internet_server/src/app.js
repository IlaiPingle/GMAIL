const express = require('express')
const bodyparser = require('body-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')

const app = express()

app.use(bodyparser.json())

app.use(usersRoutes)
app.use(blacklistRoutes)

module.exports = app