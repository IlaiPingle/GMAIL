const users = []

// This variable is used to generate unique IDs for each user.
let nextUserId = 1

// This function creates a new user object and adds it to the users array.
function createUser(username, password, first_name, sur_name, picture) {
    const user = {
        id: nextUserId++,
        username,
        password,
        first_name,
        sur_name,
        picture
    }
    users.push(user)
    return user
}

// This function find the user by its id and returns the user object.
function findUserById(id) {
    return users.find(user => user.id === id)
}

// This function find the user by its username and returns the user object.
function findUserByUsername(username) {
    return users.find(user => user.username === username)
}

    

module.exports = {
    createUser,
    findUserById,
    findUserByUsername
}
