const mongoose = require('mongoose');

const productSchema = new mongoose.Schema({
    name: { type: String, required: true },
    description: { type: String, required: true },
    price: { type: Number, required: true },
    imageUrl: { type: String, required: true },
    rating: { type: Number, default: 4.5 },

    // UPDATED: Link to Category Model
    category: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Category',
        required: true
    },

    sizes: [{ name: String, price: Number }],
    addons: [{ name: String, price: Number }]
});

module.exports = mongoose.model('Product', productSchema);