const mongoose = require('mongoose');

const dealSchema = new mongoose.Schema({
    title: { type: String, required: true },       // e.g. "50% Off"
    description: { type: String, required: true }, // e.g. "On all Vegan orders"
    imageUrl: { type: String, required: true },    // Cloudinary URL or placeholder
    code: { type: String, default: "" },           // Promo code e.g. "VEGAN50"
    isActive: { type: Boolean, default: true }
});

module.exports = mongoose.model('Deal', dealSchema);