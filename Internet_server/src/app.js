const express = require('express')
const bodyparser = require('body-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')
const emailRoutes = require('./Routes/emailRoutes')
const labelRoutes = require('./Routes/labelRoutes')
const path = require('path') // Importing the path module to handle file paths
const app = express()

app.use(bodyparser.json())
app.set('json spaces', 2);

app.use(express.static(path.join(__dirname, 'public'))) // Serve static files from the 'public' directory

app.use(usersRoutes)
app.use(blacklistRoutes)
app.use(emailRoutes)
app.use(labelRoutes)

module.exports = app