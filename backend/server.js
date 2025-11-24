const express = require('express');
const mongoose = require('mongoose');
const dotenv = require('dotenv');
const cors = require('cors');

// Import Routes
const authRoute = require('./routes/auth');
const productsRoute = require('./routes/products');
const ordersRoute = require('./routes/orders');
const categoriesRoute = require('./routes/categories');
const userRoute = require('./routes/user');

// Configuration
dotenv.config();
const app = express();

// Middleware
app.use(express.json());
app.use(cors());

// Database Connection
mongoose.connect(process.env.DB_CONNECT || 'mongodb://127.0.0.1:27017/urbaneats')
    .then(() => console.log('✅ Connected to MongoDB'))
    .catch(err => console.error('❌ Could not connect to MongoDB:', err));

// Routes Middleware
app.use('/api/auth', authRoute);
app.use('/api/products', productsRoute);
app.use('/api/orders', ordersRoute);
app.use('/api/categories', categoriesRoute);
app.use('/api/user', userRoute);

// Base Route
app.get('/', (req, res) => {
    res.send('UrbanEats Backend is Running 🚀');
});

// Start Server
const PORT = 3000;
// '0.0.0.0' allows access from Android Emulator and external devices
app.listen(PORT, '0.0.0.0', () => console.log(`Server Up and Running on port ${PORT}`));