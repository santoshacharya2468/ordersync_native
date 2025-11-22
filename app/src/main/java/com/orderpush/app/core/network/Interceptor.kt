package com.orderpush.app.core.network

import com.orderpush.app.core.extension.toApiResponse
import com.orderpush.app.core.session.SessionEventBus
import com.orderpush.app.core.session.SessionManager
import com.orderpush.app.features.auth.data.api.AuthApi
import com.orderpush.app.features.auth.data.model.RefreshTokenRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    private val tokenManager: SessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        tokenManager.getAccessToken()?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        tokenManager.getStore()?.let {
            request.url(chain.request().url.newBuilder().addQueryParameter("store", it).build())
        }
       return chain.proceed(request.build())

}

class TokenAuthenticator @Inject constructor(
    private val tokenManager: SessionManager,
    private val eventBus: SessionEventBus,
   @Named("auth") private val apiService: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: okhttp3.Response): okhttp3.Request? {
        if (responseCount(response) >= 2) {
            doLogout()
            return null
        }
        val refreshToken = tokenManager.getRefreshToken()
        val accessToken = tokenManager.getAccessToken()
        if (refreshToken == null || accessToken == null) {
            doLogout()
            return null
        }

        val newTokenResponse = runBlocking {
            try {
                val response = apiService.refreshToken(
                    RefreshTokenRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
                response.toApiResponse()
            } catch (e: Exception) {
                null
            }
        }
        if (newTokenResponse?.isSuccess() == true) {
            val device = newTokenResponse.data!!
            tokenManager.saveAccessToken(device.accessJti ?: "")
            tokenManager.saveRefreshToken(device.refreshTokenHash ?: "")
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${device.accessJti}")
                .build()
        } else if (newTokenResponse != null) {
            doLogout()
        }
        return null
    }

    private fun responseCount(response: okhttp3.Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }

    fun doLogout(){
        tokenManager.clearSession()
        GlobalScope.launch { eventBus.emitLogout() }

    }

}}



