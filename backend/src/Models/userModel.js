const users = new Map(); // <key- userId, value-userObject>

// This variable is used to generate unique IDs for each user.
let nextUserId = 1
const SYSTEM_LABELS = ['inbox','Starred', 'Snoozed', 'Important', 'Chats', 'Sent', 'Drafts', 'Bin', 'Spam', 'All Mail'];
// This function creates a new user object and adds it to the users array.
function createUser(username, password, first_name, sur_name, picture) {
    userId = nextUserId++;
    const newUser = {
        id: userId,
        username,
        password,
        first_name,
        sur_name,
        picture,
        mails: [] ,
        labels : new Map()
    }
    users.set(userId, newUser);
    return newUser;
}

// This function find the user by its id and returns the user object.
function findUserById(id) {
    return users.get(id);
}

// This function find the user by its username and returns the user object.

function findUserByUsername(username) {
    return Array.from(users.values()).find((user) =>
        user.username === username);
}



module.exports = {
    users,
    createUser,
    findUserById,
    findUserByUsername
}
