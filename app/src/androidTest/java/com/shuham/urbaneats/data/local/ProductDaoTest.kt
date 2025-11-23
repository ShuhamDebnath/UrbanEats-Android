package com.shuham.urbaneats.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var database: UrbanEatsDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setup() {
        // Create a RAM-only database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UrbanEatsDatabase::class.java
        ).allowMainThreadQueries().build() // Allowed only in tests
        dao = database.productDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndReadProduct() = runBlocking {
        // 1. Given a product
        val product = ProductEntity(
            id = "1",
            name = "Test Burger",
            description = "Delicious",
            price = 10.0,
            imageUrl = "url",
            rating = 5.0,
            category = "Food",
            isFavorite = false
        )

        // 2. When inserted
        dao.insertAll(listOf(product))

        // 3. Then we can read it back
        val products = dao.getAllProducts().first()
        assertEquals(1, products.size)
        assertEquals("Test Burger", products[0].name)
    }

    @Test
    fun toggleFavoriteUpdatesStatus() = runBlocking {
        // 1. Insert product (not favorite)
        val product = ProductEntity("1", "Burger", "Desc", 10.0, "url", 5.0, "Food", false)
        dao.insertAll(listOf(product))

        // 2. Toggle Favorite to TRUE
        dao.updateFavoriteStatus("1", true)

        // 3. Check if it updated
        val favs = dao.getFavoriteProducts().first()
        assertEquals(1, favs.size)
        assertEquals(true, favs[0].isFavorite)
    }
}