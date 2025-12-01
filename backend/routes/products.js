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
        const products = await Product.find({ name: { $regex: query, $options: 'i' } });
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