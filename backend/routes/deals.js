const router = require('express').Router();
const Deal = require('../models/Deal');

// GET ALL ACTIVE DEALS
router.get('/', async (req, res) => {
    try {
        const deals = await Deal.find({ isActive: true });
        res.json(deals);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// SEED DEALS (Run this once via Postman/Curl)
router.post('/seed', async (req, res) => {
    try {
        await Deal.deleteMany({});

        const dummyDeals = [
            {
                title: "Daily Deals",
                description: "Fresh deals from local favorites.",
                imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500", // Burger
                code: "DAILY20"
            },
            {
                title: "50% Off",
                description: "On selected Sushi platters.",
                imageUrl: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500", // Sushi
                code: "SUSHI50"
            },
            {
                title: "Free Drink",
                description: "With any large pizza order.",
                imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500", // Pizza
                code: "FREEDRINK"
            }
        ];

        const savedDeals = await Deal.insertMany(dummyDeals);
        res.json(savedDeals);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;