const express = require('express');
const mongoose = require('mongoose');
const dotenv = require('dotenv');
const cors = require('cors');
const authRoute = require('./routes/auth');
const productsRoute = require('./routes/products');

// Configuration
dotenv.config();
const app = express();

// Middleware
app.use(express.json()); // Allows us to read JSON bodies
app.use(cors()); // Allows requests from other domains/ports
app.use('/api/products', productsRoute);

// Database Connection
// We will set DB_CONNECT in the .env file
mongoose.connect(process.env.DB_CONNECT || 'mongodb://127.0.0.1:27017/urbaneats')
    .then(() => console.log('✅ Connected to MongoDB'))
    .catch(err => console.error('❌ Could not connect to MongoDB:', err));

// Routes Middleware
app.use('/api/auth', authRoute);

// Base Route for testing
app.get('/', (req, res) => {
    res.send('UrbanEats Backend is Running 🚀');
});

// Start Server
const PORT = 3000;
// CHANGE THIS LINE: Add '0.0.0.0' as the second argument
app.listen(PORT, '0.0.0.0', () => console.log(`Server Up and Running on port ${PORT}`));