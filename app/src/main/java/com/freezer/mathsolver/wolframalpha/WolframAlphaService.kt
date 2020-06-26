package com.freezer.mathsolver.wolframalpha

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WolframAlphaService {
    private var api : WolframAlphaApi
    init {
        api = createInstance()
    }

    companion object {
        private val mInstance = WolframAlphaService()

        @Synchronized
        fun getInstance() : WolframAlphaService{
            return mInstance
        }

        private fun createInstance() : WolframAlphaApi {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor()).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Configuration.BAES_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(WolframAlphaApi::class.java)
        }
    }

    fun getApi() : WolframAlphaApi = api

}