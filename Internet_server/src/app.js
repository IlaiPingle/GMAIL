const express = require('express')
const bodyparser = require('body-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')
const emailRoutes = require('./Routes/emailRoutes')

const app = express()

app.use(bodyparser.json())

app.use(usersRoutes)
app.use(blacklistRoutes)
app.use(emailRoutes)

module.exports = app