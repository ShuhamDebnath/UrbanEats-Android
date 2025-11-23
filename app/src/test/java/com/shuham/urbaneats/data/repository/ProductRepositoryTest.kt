package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.local.entity.ProductEntity
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class ProductRepositoryTest {

    private lateinit var repository: ProductRepositoryImpl
    private val dao: ProductDao = mock()

    // Fake JSON response from server
    private val serverResponse = """
        [
            {
                "_id": "1",
                "name": "Burger",
                "description": "Tasty",
                "price": 10.0,
                "imageUrl": "url",
                "rating": 4.5,
                "category": "Food"
            }
        ]
    """.trimIndent()

    @Before
    fun setup() {
        // 1. Mock the Ktor Client (Don't hit real internet)
        val mockEngine = MockEngine { _ ->
            respond(
                content = serverResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        repository = ProductRepositoryImpl(client, dao)
    }

    @Test
    fun `refreshProducts preserves local favorite status`() { // <--- REMOVED '=' here
        runBlocking { // <--- Wrapped body in runBlocking block
            // 1. GIVEN: Local DB has "Burger" as FAVORITE
            whenever(dao.getFavoriteProductIds()).thenReturn(listOf("1"))

            // 2. WHEN: We refresh from network (Server sends Burger)
            repository.refreshProducts()

            // 3. THEN: We verify the DAO insert was called with isFavorite = true
            // captor grabs the argument passed to insertAll
            argumentCaptor<List<ProductEntity>>().apply {
                verify(dao).insertAll(capture())

                val insertedList = firstValue
                // Ensure the list is not empty before accessing index 0 to avoid IndexOutOfBounds in testing if logic fails
                if (insertedList.isNotEmpty()) {
                    assertTrue("Favorite status should be preserved", insertedList[0].isFavorite)
                } else {
                    assertTrue("Inserted list was empty", false)
                }
            }
        }
    }
}