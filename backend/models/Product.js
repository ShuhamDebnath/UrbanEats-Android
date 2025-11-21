const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    description: {
        type: String,
        required: true
    },
    price: {
        type: Number,
        required: true
    },
    imageUrl: {
        type: String,
        required: true
    },
    rating: {
        type: Number,
        default: 4.5
    },
    category: {
        type: String, // e.g., "Fast Food", "Drinks"
        default: "General"
    }
});

module.exports = mongoose.model('Product', productSchema);