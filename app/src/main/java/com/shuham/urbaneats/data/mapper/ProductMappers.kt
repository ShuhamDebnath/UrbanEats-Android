package com.shuham.urbaneats.data.mapper

import com.shuham.urbaneats.data.local.entity.ProductEntity
import com.shuham.urbaneats.data.remote.dto.ProductDto
import com.shuham.urbaneats.domain.model.Product

// 1. Network -> Database (Sync)
fun ProductDto.toEntity(isFavorite: Boolean = false): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category,
        isFavorite = isFavorite,
        // Pass lists
        sizes = sizes,
        addons = addons
    )
}

// 2. Database -> Domain (UI Reading from Cache)
fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category,
        isFavorite = isFavorite,
        // Pass lists
        sizes = sizes,
        addons = addons
    )
}

// 3. Network -> Domain (Direct Search Results)
fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        category = category,
        isFavorite = false, // Search doesn't know local favorite status yet
        // Pass lists
        sizes = sizes,
        addons = addons
    )
}