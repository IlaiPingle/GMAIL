const jwt = require('jsonwebtoken');
/*
 * Middleware to authenticate requests using JWT tokens stored in cookies.
 * This middleware checks for the presence of a token in the request cookies,
 * validates it, and attaches the user information to the request object.
 * If the token is missing or invalid, it responds with a 401 Unauthorized status.
 */
const authMiddleware = (req, res, next) => {
    const token = req.cookies.token;
    
    if (!token) {
        return res.status(401).json({ message: 'Authentication required' });
    }
    
    try {
        if (!process.env.JWT_SECRET) {
            console.error('JWT_SECRET not set in environment variables');
            return res.status(500).json({ message: 'Server configuration error' });
        }
        
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.userId = decoded.id;
        next();
    } catch (error) {
        console.error('Token verification failed:', error);
        return res.status(401).json({ message: 'Invalid or expired token' });
    }
};

module.exports = authMiddleware;