package com.furkandarakcilar.havadurumu

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HavaDurumuApi {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "tr"
    ): Call<HavaDurumuCevap>
}
