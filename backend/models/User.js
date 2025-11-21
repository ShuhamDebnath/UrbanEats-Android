const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        min: 3
    },
    email: {
        type: String,
        required: true,
        unique: true // No duplicate emails allowed
    },
    password: {
        type: String,
        required: true,
        min: 6
    },
    address: {
        type: String,
        default: ""
    },
    date: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('User', userSchema);