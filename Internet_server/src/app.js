const express = require('express')
const bodyparser = require('body-parser')

const usersRoutes = require('./Routes/userRoutes')

const app = express()
app.use(bodyparser.json())

app.use('/api/users', usersRoutes)

module.exports = app

