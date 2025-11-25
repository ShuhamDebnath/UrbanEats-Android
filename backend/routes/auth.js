const router = require('express').Router();
const User = require('../models/User');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// REGISTER
router.post('/register', async (req, res) => {
    try {
        // 1. Check if user exists
        const emailExist = await User.findOne({ email: req.body.email });
        if (emailExist) return res.status(400).send({ message: 'Email already exists' });

        // 2. Hash the password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(req.body.password, salt);

        // 3. Create new user
        const user = new User({
            name: req.body.name,
            email: req.body.email,
            password: hashedPassword
        });

        const savedUser = await user.save();

        // 4. Generate Token IMMEDIATELY (Critical for Auto-Login)
        const token = jwt.sign({ _id: savedUser._id }, process.env.TOKEN_SECRET || 'secretKey123');

        // 5. Return Token + User Info (Matches Android AuthResponse)
        res.header('auth-token', token).send({
            token: token,
            user: {
                id: savedUser._id,
                name: savedUser.name,
                email: savedUser.email,
                profileImage: user.profileImage
            }
        });

    } catch (err) {
        res.status(500).send({ message: err.message });
    }
});

// LOGIN
router.post('/login', async (req, res) => {
    try {
        // 1. Check if email exists
        const user = await User.findOne({ email: req.body.email });
        if (!user) return res.status(400).send({ message: 'Email is wrong' });

        // 2. Check password
        const validPass = await bcrypt.compare(req.body.password, user.password);
        if (!validPass) return res.status(400).send({ message: 'Invalid password' });

        // 3. Create and assign token
        const token = jwt.sign({ _id: user._id }, process.env.TOKEN_SECRET || 'secretKey123');

        // 4. Send back the token and user info
        res.header('auth-token', token).send({
            token: token,
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                profileImage: user.profileImage
            }
        });

    } catch (err) {
        res.status(500).send({ message: err.message });
    }
});

module.exports = router;