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

// SEED DEALS (Updated Data)
router.post('/seed', async (req, res) => {
    try {
        await Deal.deleteMany({});

        const dummyDeals = [
            {
                title: "50% OFF Sushi",
                description: "Get half price on all platter sets this weekend.",
                imageUrl: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=800",
                code: "SUSHI50"
            },
            {
                title: "Free Delivery",
                description: "Order above $20 and get free delivery instantly.",
                imageUrl: "https://images.unsplash.com/photo-1615297928064-24977384d0f5?w=800", // Burger
                code: "FREESHIP"
            },
            {
                title: "Buy 1 Get 1",
                description: "On all medium classic pepperoni pizzas.",
                imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800",
                code: "BOGOPIZZA"
            },
            {
                title: "Dessert Rush",
                description: "Sweet tooth? Get 20% off all chocolate desserts.",
                imageUrl: "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=800", // Cake
                code: "SWEET20"
            },
            {
                title: "Vegan Power",
                description: "Try our new plant-based menu with $5 off.",
                imageUrl: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800", // Vegan Bowl
                code: "VEGAN5"
            }
        ];

        const savedDeals = await Deal.insertMany(dummyDeals);
        res.json(savedDeals);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});


module.exports = router;