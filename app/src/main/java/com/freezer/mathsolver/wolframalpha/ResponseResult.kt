package com.freezer.mathsolver.wolframalpha

import com.google.gson.annotations.SerializedName

data class ResponseResult(
    @field:SerializedName("queryresult")
    val queryResult: QueryResult? = null
)

data class QueryResult(
    @field:SerializedName("pods")
    val pods: List<PodsItem?>? = null
)

data class SubPodsItem(
    @field:SerializedName("img")
    val img: Img? = null,

    @field:SerializedName("plaintext")
    val plaintext: String? = null,

    @field:SerializedName("title")
    val title: String? = null
)


data class Img(
    @field:SerializedName("src")
    val src: String? = null
)


data class PodsItem(
    @field:SerializedName("subpods")
    val subPods: List<SubPodsItem?>? = null,

    @field:SerializedName("title")
    val title: String? = null
)
