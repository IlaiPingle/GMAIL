let nextUserId = 1
class User{
    constructor(username, password, first_name, sur_name, picture) { 
        this.id = nextUserId++
        this.username = username
        this.password = password
        this.first_name = first_name
        this.sur_name = sur_name
        this.picture = picture
    }
}
module.exports = User

