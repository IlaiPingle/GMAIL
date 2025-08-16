const{User , SYSTEM_LABELS} = require('../Models/userModel');
const bcrypt = require('bcrypt');

const createUser = async (username, password, first_name, sur_name, picture) => {
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    
    const newUser = new User({
        username,
        password: hashedPassword,
        first_name,
        sur_name,
        picture,
        labels: []
    });
    return await newUser.save();
}

const findUserById = async (id) => {
    return await User.findById(id);
};

const findUserByUsername = async (username) => {
    return await User.findOne({ username });
};

const isSystemLabel = (labelName) => {
    return SYSTEM_LABELS.includes(labelName.toLowerCase().trim());
};

module.exports = {
    createUser,
    findUserById,
    findUserByUsername,
    isSystemLabel
};
