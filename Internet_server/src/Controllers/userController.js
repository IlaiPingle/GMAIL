const User = require('../Models/userModel')
const users = []

exports.registerUser = (req, res) => {
    
    const { username, password , first_name, sur_name , picture } = req.body
    
    if (!username || !password || !first_name || !sur_name || !picture) {
        return res.status(400).json({ message: 'Missing required feilds' })
    }
    
    const existingUser = users.find(user => user.username === username)
    if (existingUser) {
        return res.status(400).json({ message: 'Username already exists' })
    }
    
    const user = new User(username, password, first_name, sur_name, picture)
    users.push(user)
    res.status(201).json({id: user.id, username: user.username, first_name: user.first_name, sur_name: user.sur_name, picture: user.picture})
}


