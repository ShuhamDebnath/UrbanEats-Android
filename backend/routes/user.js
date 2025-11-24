const router = require('express').Router();
const User = require('../models/User');

// GET ADDRESSES
router.get('/address', async (req, res) => {
    try {
        const userId = req.query.userId;
        if (!userId) return res.status(400).send("User ID required");

        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        res.json(user.addresses);
    } catch (err) {
        res.status(500).send(err.message);
    }
});

// ADD NEW ADDRESS
router.post('/address', async (req, res) => {
    try {
        const { userId, label, fullAddress } = req.body;

        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        // Push to the array defined in the Model
        user.addresses.push({ label, fullAddress });
        await user.save();

        res.json(user.addresses);
    } catch (err) {
        res.status(500).send(err.message);
    }
});

// DELETE ADDRESS
router.delete('/address/:addressId', async (req, res) => {
    try {
        const userId = req.query.userId;
        const addressId = req.params.addressId;

        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        // Remove item from array
        user.addresses = user.addresses.filter(addr => addr._id.toString() !== addressId);
        await user.save();

        res.json(user.addresses);
    } catch (err) {
        res.status(500).send(err.message);
    }
});

module.exports = router;