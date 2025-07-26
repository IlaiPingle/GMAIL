const users = new Map(); // <key- userId, value-userObject>

// This variable is used to generate unique IDs for each user.
let nextUserId = 1
const SYSTEM_LABELS = ['inbox','starred', 'snoozed', 'important', 'chats', 'sent', 'drafts', 'bin', 'spam', 'all mail','scheduled'];
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
    
    // Initialize system labels
    SYSTEM_LABELS.forEach(labelName => {
        newUser.labels.set(labelName, {
            mailIds: new Set()
        });
    });
    
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
function isSystemLabel(labelName) {
    return SYSTEM_LABELS.includes(labelName.toLowerCase());
}



module.exports = {
    users,
    createUser,
    findUserById,
    findUserByUsername,
    isSystemLabel,
}
