package com.shuham.urbaneats.data.remote

// T is a Generic type. It means this class can handle User, List<Food>, or anything.
sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {

    // Success always has Data
    class Success<T>(data: T) : NetworkResult<T>(data)

    // Error always has a Message, and sometimes cached Data
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    // Loading has nothing
    class Loading<T> : NetworkResult<T>()
}