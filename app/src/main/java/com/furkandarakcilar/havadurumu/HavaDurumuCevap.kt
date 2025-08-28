package com.furkandarakcilar.havadurumu

import com.google.gson.annotations.SerializedName

data class HavaDurumuCevap(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)
