/**
 * Validates if a string is a properly formatted email address
 * @param {string} email - The email string to validate
 * @returns {boolean} True if email is valid, false otherwise
 */
function isValidEmail(email) {
    // RFC 5322 compliant email regex
    const emailRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return emailRegex.test(email);
}

module.exports = { isValidEmail };