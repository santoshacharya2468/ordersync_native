package com.orderpush.app.core.extension

import com.google.gson.Gson
import com.orderpush.app.core.network.APIResponse
import retrofit2.Response

fun <T> Response<APIResponse<T>>.toApiResponse(): APIResponse<T> {
    return if (isSuccessful) {
        body() ?: APIResponse(
            status = code(),
            message = "Empty body",
            data = null
        )
    } else {
        // Try to parse the error body into APIResponse<T>
        val errorStr = errorBody()?.string()
        val parsed = runCatching {
            Gson().fromJson(errorStr, APIResponse::class.java) as APIResponse<T>
        }.getOrNull()

        parsed ?: APIResponse(
            status = code(),
            message = message(), // fallback if parsing fails
            data = null
        )
    }
}
