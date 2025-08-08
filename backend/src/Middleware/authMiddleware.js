const jwt = require('jsonwebtoken');
const User = require('../Models/userModel'); 
/*
 * Middleware to authenticate requests using JWT tokens stored in cookies.
 * This middleware checks for the presence of a token in the request cookies,
 * validates it, and attaches the user information to the request object.
 * If the token is missing or invalid, it responds with a 401 Unauthorized status.
 */
const authMiddleware = async (req, res, next) => {
    const token = req.cookies.token;
    
    if (!token) {
        return res.status(401).json({ message: 'Authentication required' });
    }
    
    try {
        const SECRET = process.env.JWT_SECRET;
        if (!SECRET) {
            console.error('JWT_SECRET not set in environment variables');
            return res.status(500).json({ message: 'Server configuration error' });
        }

        const decoded = jwt.verify(token, SECRET);
        req.userId = decoded.id;
		const user = await User.findUserById(decoded.id);
		if (!user) {
			return res.status(404).json({ message: 'User not found' });
		}
		
        next();
    } catch (error) {
        console.error('Token verification failed:', error.message);
        return res.status(401).json({ message: 'Invalid or expired token' });
    }
};

module.exports = authMiddleware;