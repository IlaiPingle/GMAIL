const mongoose = require('mongoose');

const emailSchema = new mongoose.Schema({
//  id: Number, // Keep the numeric ID for backward compatibility
  sender: String,
  receiver: String,
  subject: String,
  body: String,
  dateCreated: {
    type: Date,
    default: Date.now
  },
  labels: [String],
  unread: {
    type: Boolean,
    default: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  }
});

const Email = mongoose.model('Email', emailSchema);

module.exports = Email;