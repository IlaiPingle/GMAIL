const express = require('express')
const bodyparser = require('body-parser')
const mongoose = require('mongoose')
const cors = require("cors");
const cookieParser = require('cookie-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')
const emailRoutes = require('./Routes/emailRoutes')
const labelRoutes = require('./Routes/labelRoutes')

// Connect to MongoDB
mongoose.connect(process.env.MONGODB_URI)
	.then(() => console.log('MongoDB connected'))
	.catch(err => {console.error('MongoDB connection error:', err)});

const app = express()

app.use(cors({
	credentials: true // Allow credentials (cookies) to be sent
}));
app.use('/uploads', express.static('uploads'));
app.use(cookieParser());
app.use(bodyparser.json({ limit: '256kb' }));
app.use(bodyparser.urlencoded({ extended: true, limit: '256kb' }));
app.set('json spaces', 2);

// Public routes (no authentication required)
app.use(usersRoutes); // Login route
// Protected routes (auth required)
app.use(blacklistRoutes);
app.use(emailRoutes);
app.use(labelRoutes);

module.exports = app