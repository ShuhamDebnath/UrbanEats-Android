const router = require('express').Router();
const User = require('../models/User');
const cloudinary = require('cloudinary').v2;
const bcrypt = require('bcryptjs');

//Load env vars explicitly to ensure they exist before config
require('dotenv').config();


// Configure Cloudinary (Best practice: Use .env variables)
cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME || "dgtjtv4zg", // Replace 'demo' with yours if .env fails
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET
});


// HELPER: Extract Public ID from Cloudinary URL
// URL Example: https://res.cloudinary.com/.../image/upload/v1234/UrbanEats/profile/abc.jpg
// Public ID: UrbanEats/profile/abc
const extractPublicId = (url) => {
    if (!url) return null;
    try {
        // Regex to capture everything after the version number (/v1234/) and before the extension (.jpg)
        const regex = /\/v\d+\/(.+)\.[a-z]+$/;
        const match = url.match(regex);
        return match ? match[1] : null;
    } catch (error) {
        console.error("Error extracting Public ID:", error);
        return null;
    }
};


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


// UPDATE PROFILE (Now with Auto-Delete of Old Image)
router.put('/profile', async (req, res) => {
    try {
        const { userId, name, profileImage } = req.body;
        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        if (name) user.name = name;

        // If a NEW image is being uploaded (Base64 format)
        if (profileImage && profileImage.startsWith('data:image')) {

            // 1. Check if there is an OLD image to delete
            if (user.profileImage && user.profileImage.includes("cloudinary")) {
                const oldPublicId = extractPublicId(user.profileImage);
                if (oldPublicId) {
                    try {
                        await cloudinary.uploader.destroy(oldPublicId);
                        console.log(`🗑️ Deleted old image: ${oldPublicId}`);
                    } catch (delError) {
                        console.error("⚠️ Failed to delete old image:", delError);
                        // We continue anyway, don't block the upload
                    }
                }
            }

            // 2. Upload the NEW image
            try {
                const uploadResponse = await cloudinary.uploader.upload(profileImage, {
                    folder: "UrbanEats/profile",
                    resource_type: "image"
                });
                user.profileImage = uploadResponse.secure_url;
            } catch (uploadError) {
                console.error("❌ Cloudinary Upload Failed:", uploadError);
                return res.status(500).send("Image upload failed");
            }
        } else if (profileImage) {
            // If it's just a URL string (no change, or restoring default)
            user.profileImage = profileImage;
        }

        await user.save();

        res.json({
            id: user._id,
            name: user.name,
            email: user.email,
            profileImage: user.profileImage
        });
    } catch (err) {
        res.status(500).send(err.message);
    }
});


// CHANGE PASSWORD
router.put('/password', async (req, res) => {
    try {
        const { userId, oldPassword, newPassword } = req.body;

        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        // 1. Verify Old Password
        const validPass = await bcrypt.compare(oldPassword, user.password);
        if (!validPass) return res.status(400).send("Invalid old password");

        // 2. Hash New Password
        const salt = await bcrypt.genSalt(10);
        const hashedNewPassword = await bcrypt.hash(newPassword, salt);

        // 3. Update and Save
        user.password = hashedNewPassword;
        await user.save();

        res.json({ message: "Password updated successfully" });
    } catch (err) {
        res.status(500).send(err.message);
    }
});

module.exports = router;