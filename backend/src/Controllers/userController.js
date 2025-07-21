const User = require('../Models/userModel')

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

exports.registerUser = (req, res) => {
	try {
	const { username, password, first_name, sur_name, picture } = req.body;
	if (!username || !password || !first_name || !sur_name || picture === undefined) {
		return res.status(400).json({ message: "Missing required fields" });
	}

	const validation = validatePassword(password);
	if (!validation.valid) {
		return res.status(400).json({ message: validation.message });
	}

	const existingUser = User.findUserByUsername(username)
	if (existingUser) {
		return res.status(400).json({ message: 'Username already exists' })
	}
	const newUser = User.createUser(username, password, first_name, sur_name, picture)
	res.set('Location', `/api/users/${newUser.id}`);
	res.status(201).json({
		id: newUser.id,
		username: newUser.username,
		first_name: newUser.first_name,
		sur_name: newUser.sur_name,
		picture: newUser.picture
	})
} catch (error) {
		console.error('Error registering user:', error);
		res.status(500).json({ message: 'Internal server error' });
	}
}

exports.loginUser = (req, res) => {
	const { username, password } = req.body

	if (!username || !password) {
		return res.status(400).json({ message: 'Username and password are required' })
	}

	const user = User.findUserByUsername(username)

	if (!user || user.password !== password) {
		return res.status(401).json({ message: 'Invalid username or password' })
	}
	res.status(200).json({
		id: user.id,
	})
}
exports.getUser = (req, res) => {
	const Id = parseInt(req.params.id, 10)
	const user = User.findUserById(Id)
	if (!user) {
		return res.status(404).json({ message: 'User not found' })
	}
	res.status(200).json({
		id: user.id,
		username: user.username,
		first_name: user.first_name,
		sur_name: user.sur_name,
		picture: user.picture,
		inbox: user.inbox,
		labels: Array.from(user.labels.keys())
	})
}