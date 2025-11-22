package com.orderpush.app.core.network

data class APIResponse<T>(val status:Int,val message:String?,val data:T?)

fun <T> APIResponse<T>.isSuccess(): Boolean {
    return status in 200..299
}
fun <T> APIResponse<T>.toResult(): Result<T> =
    if (isSuccess() && data != null) {
        Result.success(data)
    } else {
        Result.failure(Exception(message ?: "Unknown error"))
    }
