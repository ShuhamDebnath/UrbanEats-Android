const router = require('express').Router();
const Order = require('../models/Order');

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