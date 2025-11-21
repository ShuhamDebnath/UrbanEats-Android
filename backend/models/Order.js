const mongoose = require('mongoose');

const orderSchema = new mongoose.Schema({
    userId: {
        type: String,
        required: true
    },
    userName: {
        type: String, // Store name snapshot in case user changes it later
        required: true
    },
    items: [
        {
            productId: String,
            name: String,
            quantity: Number,
            price: Number
        }
    ],
    totalAmount: {
        type: Number,
        required: true
    },
    address: {
        type: String,
        required: true
    },
    status: {
        type: String,
        default: "Pending" // Pending -> Preparing -> Out for Delivery -> Delivered
    },
    date: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Order', orderSchema);