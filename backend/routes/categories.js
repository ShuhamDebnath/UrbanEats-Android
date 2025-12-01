const router = require('express').Router();
const Category = require('../models/Category');
const cloudinary = require('cloudinary').v2;

// Load env vars
require('dotenv').config();

// Configure Cloudinary
cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET
});

// HELPER: Upload Image
async function uploadImage(base64Image) {
    if (!base64Image) return null;
    if (!base64Image.startsWith('data:image')) return base64Image; // Already URL

    try {
        const result = await cloudinary.uploader.upload(base64Image, {
            folder: "UrbanEats/category", // Specific folder
            resource_type: "image"
        });
        console.log("✅ Upload Success:", result.secure_url);
        return result.secure_url;
    } catch (error) {
        console.error("❌ Category Image Upload Failed:", error);
        throw new Error("Image upload failed: " + error.message);
    }
}

// 1. GET ALL CATEGORIES
router.get('/', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 2. ADD CATEGORY (Admin)
router.post('/', async (req, res) => {
//    console.log("--------------------------------");
//    console.log("📝 Received Add Category Request");
    try {
        let { name, imageUrl } = req.body;

        //Log what we received (excluding the massive image string)
//        console.log("Data received:", {
//            name: name,
//            hasImage: imageUrl
//        });

        if (imageUrl) {
            imageUrl = await uploadImage(imageUrl);
        }

        const category = new Category({ name, imageUrl });
        console.log("Saving to MongoDB...");
        const savedCategory = await category.save();
        console.log("✅ Product Saved Successfully!");
        res.json(savedCategory);
    } catch (err) {
        console.error("🔥 CRITICAL SERVER ERROR 🔥");
        console.error(err);
        res.status(500).json({
            message: "Server Error",
            error: err.message, // Send exact error to Android
            details: err.errors // Send Mongoose validation errors if any
        });
    }
});

// 3. UPDATE CATEGORY (Admin)
router.put('/:id', async (req, res) => {
    try {
        let updateData = req.body;

        if (updateData.imageUrl) {
            updateData.imageUrl = await uploadImage(updateData.imageUrl);
        }

        const updatedCategory = await Category.findByIdAndUpdate(
            req.params.id,
            { $set: updateData },
            { new: true }
        );
        res.json(updatedCategory);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// 4. DELETE CATEGORY (Admin)
router.delete('/:id', async (req, res) => {
    try {
        await Category.findByIdAndDelete(req.params.id);
        res.json({ message: "Category deleted" });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;