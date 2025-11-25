const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name: { type: String, required: true, min: 3 },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true, min: 6 },
    date: { type: Date, default: Date.now },
    profileImage: { type: String, default: "" }, // <--- NEW FIELD (Base64 String)
    addresses: [
        { label: String, fullAddress: String }
    ]
});

module.exports = mongoose.model('User', userSchema);