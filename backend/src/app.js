const express = require('express')
const bodyparser = require('body-parser')
const usersRoutes = require('./Routes/userRoutes')
const blacklistRoutes = require('./Routes/blacklistRoutes')
const emailRoutes = require('./Routes/emailRoutes')
const labelRoutes = require('./Routes/labelRoutes')
const path = require('path') // Importing the path module to handle file paths
const app = express()
const cors = require("cors");
const cookieParser = require('cookie-parser')
app.use(cors({
  origin: 'http://localhost:3000', // Adjust this to your frontend URL
  credentials: true // Allow credentials (cookies) to be sent
}));

app.use(cookieParser());
app.use(bodyparser.json());
app.set('json spaces', 2);

app.use(express.static(path.join(__dirname, 'public'))) // Serve static files from the 'public' directory
// Public routes (no authentication required)
app.use('/api/tokens', usersRoutes); // Login route
// Protected routes (auth required)
const authMiddleware = require('./middleware/authMiddleware');
app.use('/api/mails', authMiddleware, emailRoutes);
app.use('/api/labels', authMiddleware, labelRoutes);
app.use('/api/blacklist', authMiddleware, blacklistRoutes);

module.exports = app