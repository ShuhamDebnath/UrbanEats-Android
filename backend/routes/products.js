const router = require('express').Router();
const Product = require('../models/Product');
const Category = require('../models/Category');
const cloudinary = require('cloudinary').v2;



//Load env vars explicitly to ensure they exist before config
require('dotenv').config();


// Configure Cloudinary (Best practice: Use .env variables)
cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME || "dgtjtv4zg", // Replace 'demo' with yours if .env fails
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET
});


// HELPER: Extract Public ID from Cloudinary URL
const extractPublicId = (url) => {
    if (!url) return null;
    try {
        // Regex to capture everything after the version number and before the extension
        const regex = /\/v\d+\/(.+)\.[a-z]+$/;
        const match = url.match(regex);
        return match ? match[1] : null;
    } catch (error) {
        console.error("Error extracting Public ID:", error);
        return null;
    }
};


// HELPER: Upload Image
async function uploadImage(base64Image) {
    if (!base64Image) return null;
    if (!base64Image.startsWith('data:image')) {
        return base64Image;
    }

    try {
        const result = await cloudinary.uploader.upload(base64Image, {
            folder: "UrbanEats/product", // Specific folder for products
            resource_type: "image"
        });
        return result.secure_url;
    } catch (error) {
        console.error("❌ Cloudinary Upload Failed:", error);
        throw new Error("Image upload failed: " + error.message);
    }
}


// HELPER: Upload Image
async function uploadImage(base64Image) {
    if (!base64Image) return null;
    if (!base64Image.startsWith('data:image')) {
        console.log("⚠️ Image is not base64, skipping upload.");
        return base64Image; // Assume it's a URL
    }

    try {
        //console.log("Uploading image to Cloudinary...");
        // FIX: Updated folder path to 'UrbanEats/product'
        const result = await cloudinary.uploader.upload(base64Image, {
            folder: "UrbanEats/product",
            resource_type: "image"
        });
        console.log("✅ Upload Success:", result.secure_url);
        return result.secure_url;
    } catch (error) {
        console.error("❌ Cloudinary Upload Failed:", error);
        throw new Error("Image upload failed: " + error.message);
    }
}




// 1. SEARCH PRODUCTS
router.get('/search', async (req, res) => {
    try {
        const query = req.query.q;
        if (!query) return res.json([]);
        //const products = await Product.find({ name: { $regex: query, $options: 'i' } });

        // Step A: Find Categories that match the search term
        // e.g., Searching "Burg" finds the "Burger" category
        const matchingCategories = await Category.find({
            name: { $regex: query, $options: 'i' }
        }).select('_id'); // We only need the IDs

        const categoryIds = matchingCategories.map(cat => cat._id);

        // Step B: Find Products that match Name OR contain one of the Category IDs
        const products = await Product.find({
            $or: [
                { name: { $regex: query, $options: 'i' } }, // Match Product Name
                { category: { $in: categoryIds } }          // Match Category ID
            ]
        });
        res.json(products);
    }catch (err) {
        console.error("Search Error:", err);
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


// 3. ADD PRODUCT (With Debugging)
router.post('/', async (req, res) => {
//    console.log("--------------------------------");
//    console.log("📝 Received Add Product Request");

    try {
        let productData = req.body;

        // Log what we received (excluding the massive image string)
//        console.log("Data received:", {
//            name: productData.name,
//            price: productData.price,
//            category: productData.category,
//            hasImage: !!productData.imageUrl
//        });

        // 1. Handle Image Upload
        if (productData.imageUrl) {
            productData.imageUrl = await uploadImage(productData.imageUrl);
        }

        // 2. Create Model
        const product = new Product(productData);

        // 3. Save to DB
        console.log("Saving to MongoDB...");
        const savedProduct = await product.save();

        console.log("✅ Product Saved Successfully!");
        res.json(savedProduct);

    } catch (err) {
        console.error("🔥 CRITICAL SERVER ERROR 🔥");
        console.error(err); // This prints the FULL error stack
        res.status(500).json({
            message: "Server Error",
            error: err.message, // Send exact error to Android
            details: err.errors // Send Mongoose validation errors if any
        });
    }
});

// 4. UPDATE PRODUCT (With Image Cleanup)
router.put('/:id', async (req, res) => {
    try {
        let updateData = req.body;
        const productId = req.params.id;

        // Handle Image Update
        if (updateData.imageUrl && updateData.imageUrl.startsWith('data:image')) {
            // A. Find existing product to get old image URL
            const oldProduct = await Product.findById(productId);

            if (oldProduct && oldProduct.imageUrl && oldProduct.imageUrl.includes("cloudinary")) {
                const publicId = extractPublicId(oldProduct.imageUrl);
                if (publicId) {
                    // B. Delete old image from Cloudinary
                    try {
                        await cloudinary.uploader.destroy(publicId);
                        console.log(`🗑️ Deleted old product image: ${publicId}`);
                    } catch (e) {
                        console.error("Failed to delete old image:", e);
                    }
                }
            }

            // C. Upload new image
            updateData.imageUrl = await uploadImage(updateData.imageUrl);
        }

        const updatedProduct = await Product.findByIdAndUpdate(
            productId,
            { $set: updateData },
            { new: true }
        );
        res.json(updatedProduct);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 5. DELETE PRODUCT (With Image Cleanup)
router.delete('/:id', async (req, res) => {
    try {
        const productId = req.params.id;
        const product = await Product.findById(productId);

        if (!product) {
            return res.status(404).json({ message: "Product not found" });
        }

        // 1. Delete image from Cloudinary
        if (product.imageUrl && product.imageUrl.includes("cloudinary")) {
            const publicId = extractPublicId(product.imageUrl);
            if (publicId) {
                try {
                    await cloudinary.uploader.destroy(publicId);
                    console.log(`🗑️ Deleted product image: ${publicId}`);
                } catch (e) {
                    console.error("Failed to delete image:", e);
                }
            }
        }

        // 2. Delete from DB
        await Product.findByIdAndDelete(productId);
        res.json({ message: "Product deleted" });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});



// --- THE MEGA SEED ---
router.post('/seed', async (req, res) => {
    try {
        // 1. Clean Slate
        await Product.deleteMany({});
        await Category.deleteMany({});

        // 2. Create Categories
        const categoriesData = [
            { name: "Burger", imageUrl: "https://cdn-icons-png.flaticon.com/512/3075/3075977.png" },
            { name: "Pizza", imageUrl: "https://cdn-icons-png.flaticon.com/512/1404/1404945.png" },
            { name: "Sushi", imageUrl: "https://cdn-icons-png.flaticon.com/512/2252/2252075.png" },
            { name: "Vegan", imageUrl: "https://cdn-icons-png.flaticon.com/512/2918/2918148.png" },
            { name: "Cold Drink", imageUrl: "https://cdn-icons-png.flaticon.com/512/2738/2738730.png" },
            { name: "Biriyani", imageUrl: "https://cdn-icons-png.flaticon.com/512/1065/1065715.png" },
            { name: "Dessert", imageUrl: "https://cdn-icons-png.flaticon.com/512/3081/3081840.png" },
            { name: "Roll", imageUrl: "https://cdn-icons-png.flaticon.com/512/1231/1231662.png" }
        ];

        const createdCategories = await Category.insertMany(categoriesData);
        const getCatId = (name) => createdCategories.find(c => c.name === name)._id;

        // 3. Create 20+ Products
        const productsData = [
            // --- BURGERS ---
            {
                name: "Classic Smash Burger",
                description: "Two smashed beef patties, american cheese, pickles, onions, and secret sauce.",
                price: 12.50,
                imageUrl: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800",
                rating: 4.8,
                category: getCatId("Burger"),
                sizes: [{ name: "Single", price: 0 }, { name: "Double", price: 3.0 }],
                addons: [{ name: "Bacon", price: 2.0 }, { name: "Extra Sauce", price: 0.5 }]
            },
            {
                name: "Spicy Crispy Chicken",
                description: "Fried chicken breast, spicy mayo, lettuce, and pickles on a brioche bun.",
                price: 11.99,
                imageUrl: "https://images.unsplash.com/photo-1615297928064-24977384d0f5?w=800",
                rating: 4.7,
                category: getCatId("Burger"),
                sizes: [{ name: "Regular", price: 0 }, { name: "Large Meal", price: 4.0 }],
                addons: [{ name: "Cheese Slice", price: 1.0 }, { name: "Jalapenos", price: 0.75 }]
            },
            {
                name: "Truffle Mushroom Swiss",
                description: "Gourmet beef patty, swiss cheese, sautéed mushrooms, truffle aioli.",
                price: 15.50,
                imageUrl: "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?w=800",
                rating: 4.9,
                category: getCatId("Burger"),
                sizes: [{ name: "Regular", price: 0 }],
                addons: [{ name: "Extra Truffle", price: 2.0 }]
            },

            // --- PIZZA ---
            {
                name: "Margherita Classico",
                description: "San Marzano tomato sauce, fresh mozzarella, basil, extra virgin olive oil.",
                price: 14.00,
                imageUrl: "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800",
                rating: 4.6,
                category: getCatId("Pizza"),
                sizes: [{ name: "12 inch", price: 0 }, { name: "16 inch", price: 5.0 }],
                addons: [{ name: "Extra Cheese", price: 2.0 }, { name: "Chili Oil", price: 0.0 }]
            },
            {
                name: "Pepperoni Feast",
                description: "Loaded with crispy pepperoni slices and mozzarella cheese.",
                price: 16.50,
                imageUrl: "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800",
                rating: 4.8,
                category: getCatId("Pizza"),
                sizes: [{ name: "12 inch", price: 0 }, { name: "16 inch", price: 6.0 }],
                addons: [{ name: "Hot Honey", price: 1.5 }, { name: "Ranch Dip", price: 0.75 }]
            },
            {
                name: "BBQ Chicken",
                description: "BBQ sauce base, grilled chicken, red onions, cilantro.",
                price: 17.00,
                imageUrl: "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800",
                rating: 4.7,
                category: getCatId("Pizza"),
                sizes: [{ name: "12 inch", price: 0 }, { name: "16 inch", price: 6.0 }],
                addons: [{ name: "Extra Chicken", price: 3.0 }]
            },

            // --- SUSHI ---
            {
                name: "Salmon Nigiri Set",
                description: "5 pieces of fresh salmon over vinegared rice.",
                price: 12.00,
                imageUrl: "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=800",
                rating: 4.9,
                category: getCatId("Sushi"),
                sizes: [{ name: "5 pcs", price: 0 }, { name: "10 pcs", price: 10.0 }],
                addons: [{ name: "Wasabi", price: 0.0 }, { name: "Soy Sauce", price: 0.0 }]
            },
            {
                name: "Dragon Roll",
                description: "Eel and cucumber inside, topped with avocado and eel sauce.",
                price: 14.50,
                imageUrl: "https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=800",
                rating: 4.8,
                category: getCatId("Sushi"),
                sizes: [{ name: "8 pcs", price: 0 }],
                addons: [{ name: "Spicy Mayo", price: 0.5 }]
            },
            {
                name: "Spicy Tuna Roll",
                description: "Fresh tuna mixed with spicy mayo and cucumber.",
                price: 9.50,
                imageUrl: "https://images.unsplash.com/photo-1553621042-f6e147245754?w=800",
                rating: 4.6,
                category: getCatId("Sushi"),
                sizes: [{ name: "6 pcs", price: 0 }],
                addons: [{ name: "Extra Ginger", price: 0.5 }]
            },

            // --- VEGAN ---
            {
                name: "Vegan Buddha Bowl",
                description: "Quinoa, roasted chickpeas, avocado, kale, tahini dressing.",
                price: 13.50,
                imageUrl: "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800",
                rating: 4.7,
                category: getCatId("Vegan"),
                sizes: [{ name: "Regular", price: 0 }, { name: "Large", price: 3.0 }],
                addons: [{ name: "Extra Tofu", price: 2.0 }]
            },
            {
                name: "Beyond Burger",
                description: "Plant-based patty, vegan cheese, lettuce, tomato, vegan mayo.",
                price: 14.00,
                imageUrl: "https://images.unsplash.com/photo-1520072959219-c595dc3f3a58?w=800",
                rating: 4.5,
                category: getCatId("Vegan"),
                sizes: [{ name: "Regular", price: 0 }],
                addons: [{ name: "Avocado", price: 2.0 }]
            },

            // --- BIRIYANI ---
            {
                name: "Hyderabadi Chicken Biriyani",
                description: "Authentic dum biriyani with marinated chicken and saffron rice.",
                price: 15.00,
                imageUrl: "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
                rating: 4.9,
                category: getCatId("Biriyani"),
                sizes: [{ name: "Single", price: 0 }, { name: "Family Pack", price: 15.0 }],
                addons: [{ name: "Raita", price: 1.0 }, { name: "Salad", price: 0.5 }]
            },
            {
                name: "Mutton Biriyani",
                description: "Tender mutton pieces cooked with aromatic spices and basmati rice.",
                price: 18.00,
                imageUrl: "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=800",
                rating: 4.8,
                category: getCatId("Biriyani"),
                sizes: [{ name: "Single", price: 0 }],
                addons: [{ name: "Extra Egg", price: 1.0 }]
            },

            // --- ROLLS ---
            {
                name: "Chicken Kathi Roll",
                description: "Grilled chicken chunks wrapped in a paratha with onions and chutney.",
                price: 8.50,
                imageUrl: "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=800",
                rating: 4.6,
                category: getCatId("Roll"),
                sizes: [{ name: "Regular", price: 0 }, { name: "Double Egg", price: 1.5 }],
                addons: [{ name: "Extra Cheese", price: 1.0 }]
            },
            {
                name: "Paneer Tikka Roll",
                description: "Spicy paneer cubes wrapped with veggies and mint sauce.",
                price: 7.50,
                imageUrl: "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=800",
                rating: 4.5,
                category: getCatId("Roll"),
                sizes: [{ name: "Regular", price: 0 }],
                addons: [{ name: "Extra Sauce", price: 0.5 }]
            },

            // --- DESSERT ---
            {
                name: "Chocolate Lava Cake",
                description: "Warm chocolate cake with a gooey molten center.",
                price: 7.00,
                imageUrl: "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=800",
                rating: 4.9,
                category: getCatId("Dessert"),
                sizes: [{ name: "Single", price: 0 }],
                addons: [{ name: "Vanilla Ice Cream", price: 2.0 }]
            },
            {
                name: "New York Cheesecake",
                description: "Creamy cheesecake with a graham cracker crust and strawberry topping.",
                price: 6.50,
                imageUrl: "https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=800",
                rating: 4.7,
                category: getCatId("Dessert"),
                sizes: [{ name: "Slice", price: 0 }],
                addons: [{ name: "Extra Strawberry Sauce", price: 1.0 }]
            },
            {
                name: "Tiramisu",
                description: "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone.",
                price: 8.00,
                imageUrl: "https://images.unsplash.com/photo-1571875257727-256c39da42af?w=800",
                rating: 4.8,
                category: getCatId("Dessert"),
                sizes: [{ name: "Slice", price: 0 }],
                addons: []
            },

            // --- DRINKS ---
            {
                name: "Mango Lassi",
                description: "Yogurt-based mango smoothie.",
                price: 4.50,
                imageUrl: "https://images.unsplash.com/photo-1546173159-315724a31696?w=800",
                rating: 4.8,
                category: getCatId("Cold Drink"),
                sizes: [{ name: "Regular", price: 0 }, { name: "Large", price: 1.5 }],
                addons: []
            },
            {
                name: "Iced Coffee",
                description: "Cold brewed coffee with milk and ice.",
                price: 3.50,
                imageUrl: "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=800",
                rating: 4.6,
                category: getCatId("Cold Drink"),
                sizes: [{ name: "Regular", price: 0 }],
                addons: [{ name: "Oat Milk", price: 1.0 }, { name: "Caramel Syrup", price: 0.5 }]
            },
            {
                name: "Berry Smoothie",
                description: "Blend of strawberries, blueberries, and raspberries.",
                price: 6.00,
                imageUrl: "https://images.unsplash.com/photo-1623595119608-e905d2d4b6c3?w=800",
                rating: 4.7,
                category: getCatId("Cold Drink"),
                sizes: [{ name: "Regular", price: 0 }],
                addons: [{ name: "Protein Powder", price: 2.0 }]
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