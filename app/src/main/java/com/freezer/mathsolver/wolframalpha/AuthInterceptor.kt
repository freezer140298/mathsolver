package com.freezer.mathsolver.wolframalpha

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder().addQueryParameter("appid", Configuration.APP_ID).addQueryParameter("output", Configuration.OUTPUT_FORMAT).build()
        val finalRequest = request.newBuilder().url(url).build()
        return chain.proceed(finalRequest)
    }

}