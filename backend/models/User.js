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
    date: {
        type: Date,
        default: Date.now
    },
    // FIX: Changed from single String to Array of Objects
    addresses: [
        {
            label: { type: String, required: true }, // e.g., "Home", "Work"
            fullAddress: { type: String, required: true } // e.g., "123 Main St"
        }
    ]
});

module.exports = mongoose.model('User', userSchema);