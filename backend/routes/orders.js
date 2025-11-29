const router = require('express').Router();
const Order = require('../models/Order');



// 1. GET ALL ORDERS (Admin)
router.get('/all', async (req, res) => {
    try {
        // Sort by date descending (newest first)
        const orders = await Order.find().sort({ date: -1 });
        res.json(orders);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 2. UPDATE ORDER STATUS (Admin)
// Usage: PUT /api/orders/:id/status { "status": "Preparing" }
router.put('/:id/status', async (req, res) => {
    try {
        const { status } = req.body;
        const order = await Order.findByIdAndUpdate(
            req.params.id,
            { status: status },
            { new: true } // Return the updated object
        );
        res.json(order);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});



// PLACE ORDER
router.post('/', async (req, res) => {
    try {
        const order = new Order({
            userId: req.body.userId,
            userName: req.body.userName,
            items: req.body.items,
            totalAmount: req.body.totalAmount,
            address: req.body.address
        });

        const savedOrder = await order.save();
        res.json(savedOrder);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// GET MY ORDERS (For History Screen later)
router.get('/:userId', async (req, res) => {
    try {
        const orders = await Order.find({ userId: req.params.userId });
        res.json(orders);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;