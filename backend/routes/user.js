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






// UPDATE PROFILE (With Cloudinary)
router.put('/profile', async (req, res) => {
    try {
        const { userId, name, profileImage } = req.body;

        const user = await User.findById(userId);
        if (!user) return res.status(404).send("User not found");

        if (name) user.name = name;

        // CLOUDINARY LOGIC
        if (profileImage && profileImage.startsWith('data:image')) {
            try {
                // Upload Base64 image to Cloudinary
                // "urbaneats_profiles" is the folder name in your Cloudinary console
                const uploadResponse = await cloudinary.uploader.upload(profileImage, {
                    folder: "urbaneats_profiles",
                    resource_type: "image"
                });

                // Save the URL (https://res.cloudinary.com/...) instead of the raw string
                user.profileImage = uploadResponse.secure_url;
            } catch (uploadError) {
                console.error("Cloudinary Upload Failed:", uploadError);
                // Fallback: Don't save image if upload fails
            }
        } else if (profileImage) {
            // If it's already a URL (not base64), just save it
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