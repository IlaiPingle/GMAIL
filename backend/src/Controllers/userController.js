const UserService = require('../services/userService');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');

function validatePassword(password) {
	if (password.length < 8) {
		return { valid: false, message: 'Password must be at least 8 characters long' };
	}
	// Password must contain at least one uppercase letter
	if (!/[A-Z]/.test(password)) {
		return { valid: false, message: 'Password must contain at least one uppercase letter' };
	}

	// Password must contain at least one lowercase letter
	if (!/[a-z]/.test(password)) {
		return { valid: false, message: 'Password must contain at least one lowercase letter' };
	}

	// Password must contain at least one number
	if (!/\d/.test(password)) {
		return { valid: false, message: 'Password must contain at least one number' };
	}

	// Password must contain at least one special character
	if (!/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(password)) {
		return { valid: false, message: 'Password must contain at least one special character' };
	}

	return { valid: true };
}

exports.registerUser = async (req, res) => {
	try {
		const { username, password, first_name, sur_name, picture } = req.body;
		if (typeof username !== 'string' || typeof password !== 'string' || typeof first_name !== 'string' || typeof sur_name !== 'string') {
			return res.status(400).json({ message: 'Invalid input types' });
		}
		if (!/[^\s]/.test(username) || !/[^\s]/.test(first_name) || !/[^\s]/.test(sur_name)) {
			return res.status(400).json({ message: 'Missing required fields' });
		}
		if (username.length < 3 || username.length > 20) {
			return res.status(400).json({ message: 'Username must be between 3 and 20 characters long' });
		}
		const validation = validatePassword(password);
		if (!validation.valid) {
			return res.status(400).json({ message: validation.message });
		}

		const existingUser = await UserService.findUserByUsername(username)
		if (existingUser) {
			return res.status(409).json({ message: 'User already exists' })
		}
		const newUser = await UserService.createUser(username, password, first_name, sur_name, picture)
		res.set('Location', `/api/users/${newUser._id}`);
		res.status(201).json({
			id: newUser._id,
			username: newUser.username,
			first_name: newUser.first_name,
			sur_name: newUser.sur_name,
			picture: newUser.picture
		})
	} catch (error) {
		if (error?.code === 11000) {
			return res.status(409).json({ message: 'User already exists' });
		}
		console.error('Error registering user:', error);
		res.status(500).json({ message: 'Internal server error' });
	}
}

exports.loginUser = async (req, res) => {
	const { username, password } = req.body

	if (typeof username !== 'string' || typeof password !== 'string') {
		return res.status(400).json({ message: 'Username and password are required' })
	}
	if (!/[^\s]/.test(username) || password.length === 0) {
		return res.status(400).json({ message: 'Username and password are required' });
	}
	const user = await UserService.findUserByUsername(username)

	if (!user) {
		return res.status(401).json({ message: 'Invalid username or password' });
	}
	const passwordMatch = await bcrypt.compare(password, user.password);
	if (!passwordMatch) {
		return res.status(401).json({ message: 'Invalid username or password' });
	}
	// Generate JWT token
	if (!process.env.JWT_SECRET) {
		console.error('JWT_SECRET is not set in environment variables');
		return res.status(500).json({ message: 'Server configuration error' });
	}
	const token = jwt.sign({ id: user._id.toString() }, process.env.JWT_SECRET, { expiresIn: '1h' })
	res.cookie('token', token, {
		httpOnly: true,
		secure: process.env.NODE_ENV === 'production',
		sameSite: 'Strict',
		maxAge: 60 * 60 * 1000 // 1 hour
	})

	res.status(200).json({ message: 'Login successful' })
}

exports.logoutUser = (req, res) => {
	res.clearCookie('token');
	res.status(200).json({ message: 'Logged out successfully' });
}

exports.getUser = async (req, res) => {
	try {
		const Id = req.params.id;
		if (typeof Id !== 'string' || !mongoose.isValidObjectId(Id)) {
			return res.status(400).json({ message: 'Invalid user ID' });
		}
		const user = await UserService.findUserById(Id)
		if (!user) {
			return res.status(404).json({ message: 'User not found' })
		}
		res.status(200).json({
			id: user._id.toString(),
			username: user.username,
			first_name: user.first_name,
			sur_name: user.sur_name,
			picture: user.picture,
			labels: user.labels,
		})
	} catch (error) {
		console.error('Error fetching user:', error);
		res.status(error.status || 500).json({ message: error.message || 'Internal server error' });
	}
}
exports.isSignedIn = async (req, res) => {
	const user = await UserService.findUserById(req.userId);
	console.log('User ID from token:', req.userId);
	if (!user) {
		return res.status(404).json({ message: 'User not found' });
	}
	res.status(200).json({
		username: user.username,
		first_name: user.first_name,
		sur_name: user.sur_name,
		picture: user.picture
	});
}
