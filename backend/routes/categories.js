const router = require('express').Router();
const Category = require('../models/Category');

// GET ALL CATEGORIES
router.get('/', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

module.exports = router;