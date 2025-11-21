const router = require('express').Router();
const Product = require('../models/Product');

// 1. SEARCH PRODUCTS (Must be before /:id)
// Usage: /api/products/search?q=burger
router.get('/search', async (req, res) => {
    try {
        const query = req.query.q; // Reads 'q' from URL
        if (!query) return res.json([]);

        // Regex Search: 'i' makes it case-insensitive (Pizza matches pizza)
        const products = await Product.find({
            name: { $regex: query, $options: 'i' }
        });
        res.json(products);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 2. GET ALL PRODUCTS
router.get('/', async (req, res) => {
    try {
        const products = await Product.find();
        res.json(products);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 3. SEED DATA (Run this once to fill DB)
router.post('/seed', async (req, res) => {
    try {
        // Optional: await Product.deleteMany({});

        const dummyData = [
            {
                name: "Classic Cheese Burger",
                description: "Juicy beef patty with melted cheddar, lettuce, and tomato.",
                price: 8.99,
                imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500",
                rating: 4.7,
                category: "Fast Food"
            },
            {
                name: "Pepperoni Pizza",
                description: "Crispy crust topped with spicy pepperoni and mozzarella.",
                price: 14.50,
                imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500",
                rating: 4.8,
                category: "Pizza"
            },
            {
                name: "Sushi Platter",
                description: "Assorted fresh nigiri and maki rolls.",
                price: 22.00,
                imageUrl: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=500",
                rating: 4.9,
                category: "Asian"
            },
            {
                name: "Caesar Salad",
                description: "Fresh romaine lettuce with parmesan, croutons, and caesar dressing.",
                price: 10.00,
                imageUrl: "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=500",
                rating: 4.2,
                category: "Healthy"
            },
            {
                name: "Fried Chicken Bucket",
                description: "6 pieces of golden crispy fried chicken.",
                price: 18.99,
                imageUrl: "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=500",
                rating: 4.6,
                category: "Fast Food"
            }
        ];

        const savedProducts = await Product.insertMany(dummyData);
        res.json(savedProducts);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 4. GET SINGLE PRODUCT (Must be last)
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        res.json(product);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;