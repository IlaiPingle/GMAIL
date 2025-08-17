const mongoose = require('mongoose');
const {Schema , Types} = mongoose;

const emailSchema = new Schema({
  //  id: Number, // Keep the numeric ID for backward compatibility
  owner: {
    type: Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  sender: {
    type: String,
    required: false,
    index: true
  },
  receiver: {
    type: String,
    required: false,
    index: true,
    maxlength: 50
  },
  subject: {
    type: String,
    default: '',
    maxlength: 100
  },
  body: {
    type: String,
    default: '',
    maxlength: 500
  },
  labels: {
    type: [String],
    default: [],
    index: true
  },
},{ timestamps: true });

emailSchema.index({createdAt: -1});

const Email = mongoose.model('Email', emailSchema);

module.exports = Email;