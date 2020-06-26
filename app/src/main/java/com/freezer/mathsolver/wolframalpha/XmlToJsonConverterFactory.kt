package com.freezer.mathsolver.wolframalpha


import com.google.gson.Gson
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class XmlToJsonConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return XmlToJsonConverter.INSTANCE
    }

    companion object {
        class XmlToJsonConverter : Converter<ResponseBody, ResponseResult> {
            companion object {
                val INSTANCE = XmlToJsonConverter()

            }
            override fun convert(responseBody : ResponseBody): ResponseResult? {
                val gson = Gson()

                val xmlStr = responseBody.string()
                val xmlToJson = XmlToJson.Builder(xmlStr)
                    .forceList("/queryresult/pod/subpod")
                    .forceList("/queryresult/pod/expressiontypes")
                    .forceList("/queryresult/pod/expressiontypes/expressiontype")
                    .forceList("/queryresult/pod/states")
                    .forceList("/queryresult/pod/states/state")
                    .build()

                val responseResult = gson.fromJson(xmlToJson.toString(), ResponseResult::class.java)

                return responseResult
            }
        }
    }
}