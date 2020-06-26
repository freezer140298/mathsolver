package com.freezer.mathsolver.wolframalpha

import retrofit2.http.GET
import retrofit2.http.Query

interface WolframAlphaApi {
    @GET("query")
    suspend fun query(@Query("input") input : String) : ResponseResult
}