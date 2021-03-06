package com.example.android.politicalpreparedness.network.models

sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val data: T?) : ApiResult<T>()
    data class Error(val error: String?) : ApiResult<Nothing>()
    data class Retry(val error: String?) : ApiResult<Nothing>()
}
