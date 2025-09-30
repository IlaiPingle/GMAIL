const { findUserById, isSystemLabel } = require("./userService");
const Email = require("../Models/emailModel");
/**
* Create a new label for a user
*/
async function createLabel(userId, labelName) {
  const user = await getUserOrThrow(userId);
  if (isSystemLabel(labelName)) {
    const error = new Error("Cannot create system label");
    error.status = 400;
    throw error;
  }
  if (user.labels.some((label) => label.name === labelName)) {
    // Check if label already exists
    const error = new Error("Label already exists");
    error.status = 409;
    throw error;
  }
  
  user.labels.push({ name: labelName });
  await user.save(); // Save the user with the new label
  return { name: labelName }; // Return the created label
}

/**
* Get all labels for a user
*/
async function getUserLabels(userId) {
  const user = await getUserOrThrow(userId);
  return user.labels
  .filter((label) => !isSystemLabel(label.name)) // Filter out system labels
  .map((label) => label.name); // Return only label names
}

/**
* Get a specific label by name
*/
async function getLabelByName(userId, labelName) {
  const user = await getUserOrThrow(userId);
  const label = user.labels.find((label) => label.name === labelName);
  if (!label) {
    const error = new Error("Label not found");
    error.status = 404;
    throw error;
  }
  return label;
}

/**
* Update a label name
*/
async function updateLabel(userId, labelName, newName) {
  const user = await getUserOrThrow(userId);
  if (isSystemLabel(newName)) {
    const error = new Error("new name cannot be a system label");
    error.status = 400;
    throw error;
  }
  if (isSystemLabel(labelName)) {
    const error = new Error("Cannot rename system label");
    error.status = 400;
    throw error;
  }
  // Check if label exists
  const labelIndex = user.labels.findIndex((l) => l.name === labelName);
  if (labelIndex === -1) {
    const error = new Error("Label not found");
    error.status = 404;
    throw error;
  }
  // Check if new label name already exists
  if (user.labels.some((l) => l.name === newName)) {
    const error = new Error("Label with this name already exists");
    error.status = 400;
    throw error;
  }
  // Update label name
  user.labels[labelIndex].name = newName;
  await user.save();
  
  await Email.updateMany(
    { owner: user._id, labels: labelName },
    { $addToSet: { labels: newName }, $pull: { labels: labelName } }
  );
  
  return true;
}

/**
* Delete a label
*/
async function deleteLabel(userId, labelName) {
  const user = await getUserOrThrow(userId);
  if (isSystemLabel(labelName)) {
    const error = new Error("Cannot delete system label");
    error.status = 400;
    throw error;
  }
  
  const before = user.labels?.length || 0;
  user.labels = user.labels.filter((label) => label.name !== labelName);
  if (before === user.labels?.length) {
    const error = new Error("Label not found");
    error.status = 404;
    throw error;
  }
  await user.save();
  
  // Remove the label from all associated mails
  await Email.updateMany(
    { owner: user._id, labels: labelName },
    { $pull: { labels: labelName } }
  );
  return true;
}

async function addLabelToMail(userId, mailId, labelName) {
  const user = await getUserOrThrow(userId);
  const exists =
  isSystemLabel(labelName) ||
  user.labels.some((label) => label.name === labelName);
  // Check if label exists
  if (!exists) {
    const error = new Error("Label not found");
    error.status = 404;
    throw error;
  }
  try {
    const res = await Email.updateOne(
      { _id: mailId, owner: user._id },
      { $addToSet: { labels: labelName } }
    );
    if (res.matchedCount === 0) {
      const error = new Error("Mail not found");
      error.status = 404;
      throw error;
    }
  } catch (error) {
    if (error?.name === "CastError") {
      const castError = new Error("Invalid mail ID");
      castError.status = 400;
      throw castError;
    }
    throw error;
  }
  return true;
}
/**
* Remove a label from a mail
*/
async function removeLabelFromMail(userId, mailId, labelName) {
  const user = await getUserOrThrow(userId);
  try {
    const res = await Email.updateOne(
      { _id: mailId, owner: user._id },
      { $pull: { labels: labelName } }
    );
    if (res.matchedCount === 0) {
      const error = new Error("Mail not found");
      error.status = 404;
      throw error;
    }
    return true;
  } catch (error) {
    if (error?.name === "CastError") {
      const castError = new Error("Invalid mail ID");
      castError.status = 400;
      throw castError;
    }
    throw error;
  }
}
/**
* Get all mails associated with a specific label for a user
*/
async function getMailsByLabel(userId, labelName) {
  let query = { owner: userId };
  if (labelName === "all") {
    query.labels = { $nin:["spam", "bin"] };
  } else if (labelName === "bin" || labelName === "spam") {
    query.labels = labelName;
  } else {
    query.labels = { $all: [labelName], $nin: ['spam', 'bin'] };
  }
  const emails = await Email.find(query)
    .sort({ createdAt: -1 })
    .limit(50)
    .lean();
  const mailsOut = emails.map(({ _id, ...rest }) => ({
    id: _id.toString(),
    ...rest
  }));
  return mailsOut;
}

/**
* Helper function to get a user or throw an error
*/
async function getUserOrThrow(userId) {
  if (!userId) {
    const error = new Error("User ID is required");
    error.status = 400;
    throw error;
  }
  try {
    const user = await findUserById(userId);
    if (!user) {
      const error = new Error("User not found");
      error.status = 404;
      throw error;
    }
    return user;
  } catch (error) {
    if (error.name === "CastError") {
      const notFoundError = new Error("User not found");
      notFoundError.status = 404;
      throw notFoundError;
    }
    throw error;
  }
}

module.exports = {
  createLabel,
  getUserLabels,
  getLabelByName,
  updateLabel,
  deleteLabel,
  addLabelToMail,
  getMailsByLabel,
  removeLabelFromMail,
};
