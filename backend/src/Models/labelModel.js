/**
 * Creates a new label
 * @param {string} labelName - The name of the label to create
 * @returns {Object} The created label object
 */
function createLabel(labelName) {
    const newLabel = {
        labelName,
        mails: [],
        dateCreated: new Date().toISOString()
    };
    return newLabel;
}

module.exports = {
    createLabel
};