const express = require('express')
const bodyparser = require('body-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')
const emailRoutes = require('./Routes/emailRoutes')
const labelRoutes = require('./Routes/labelRoutes')
const app = express()
const cors = require("cors");
app.use(cors());

app.use(bodyparser.json())
app.set('json spaces', 2);

app.use(usersRoutes)
app.use(blacklistRoutes)
app.use(emailRoutes)
app.use(labelRoutes)

module.exports = app