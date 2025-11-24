const router = require('express').Router();
const Product = require('../models/Product');
const Category = require('../models/Category');

// GET ALL PRODUCTS (Now populates category data)
router.get('/', async (req, res) => {
    try {
        const products = await Product.find();
        res.json(products);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// SEARCH
router.get('/search', async (req, res) => {
    try {
        const query = req.query.q;
        if (!query) return res.json([]);
        const products = await Product.find({ name: { $regex: query, $options: 'i' } }).populate('category');
        res.json(products);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// SEED DATA (Categories + Products)
router.post('/seed', async (req, res) => {
    try {
        // 1. Clear Old Data
        await Product.deleteMany({});
        await Category.deleteMany({});

        // 2. Create Categories
        const categoriesData = [
            { name: "Burger", imageUrl: "https://cdn-icons-png.flaticon.com/512/3075/3075977.png" },
            { name: "Pizza", imageUrl: "https://cdn-icons-png.flaticon.com/512/1404/1404945.png" },
            { name: "Biryani", imageUrl: "https://cdn-icons-png.flaticon.com/128/4781/4781223.png.png" },
            { name: "Sushi", imageUrl: "https://cdn-icons-png.flaticon.com/512/2252/2252075.png" },
            { name: "Vegan", imageUrl: "https://cdn-icons-png.flaticon.com/512/2918/2918148.png" },
            { name: "Drinks", imageUrl: "https://cdn-icons-png.flaticon.com/512/2738/2738730.png" },
        ];

        const createdCategories = await Category.insertMany(categoriesData);

        // Helper to find ID by Name
        const getCatId = (name) => createdCategories.find(c => c.name === name)._id;

        // 3. Create Products Linked to Categories
        const productsData = [
            {
                name: "Spicy Chicken Burger",
                description: "Fiery hot chicken patty.",
                price: 12.99,
                imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500",
                category: getCatId("Burger"), // Link!
                sizes: [{ name: "Regular", price: 0 }, { name: "Large", price: 3.50 }],
                addons: [{ name: "Extra Cheese", price: 1.50 }]
            },
            {
                name: "Margherita Pizza",
                description: "Classic tomato & basil.",
                price: 14.50,
                imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500",
                category: getCatId("Pizza"),
                sizes: [{ name: "Medium", price: 0 }, { name: "Large", price: 5.00 }],
                addons: [{ name: "Extra Cheese", price: 2.00 }]
            },
             {
                 name: "Chicken Biryani",
                 description: "Aromatic basmati rice cooked with tender chicken and spices.",
                 price: 18.00,
                 imageUrl: "https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=500",
                 rating: 4.9,
                 category: getCatId("Biryani"),
                 sizes: [
                     { name: "Single", price: 0 },
                     { name: "Family Pack", price: 12.00 }
                 ],
                 addons: [
                     { name: "Extra Raita", price: 1.00 },
                     { name: "Boiled Egg", price: 1.50 }
                 ]
             },
             {
                 name: "Coca Cola",
                 description: "Chilled refreshing cola.",
                 price: 2.50,
                 imageUrl: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500",
                 rating: 4.5,
                 category: getCatId("Drinks"),
                 sizes: [
                     { name: "Can (330ml)", price: 0 },
                     { name: "Bottle (500ml)", price: 1.00 }
                 ],
                 addons: []
             }

        ];

        const savedProducts = await Product.insertMany(productsData);
        res.json({ categories: createdCategories, products: savedProducts });

    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});


// 4. GET SINGLE PRODUCT
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        res.json(product);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});


module.exports = router;