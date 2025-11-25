package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.domain.model.Address
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getAddresses(): NetworkResult<List<Address>>
    suspend fun addAddress(label: String, fullAddress: String): NetworkResult<List<Address>>

    // SELECTION LOGIC
    suspend fun selectAddress(addressId: String)
    fun getSelectedAddressId(): Flow<String?>

    // NEW: Helper to find an address by ID (from cache or fetch)
    suspend fun getAddressById(id: String): Address?
    suspend fun deleteAddress(addressId: String): NetworkResult<List<Address>>
    suspend fun updateProfile(name: String, base64Image: String?): NetworkResult<Boolean>
    suspend fun changePassword(oldPass: String, newPass: String): NetworkResult<Boolean>
}