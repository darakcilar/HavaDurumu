package com.furkandarakcilar.havadurumu

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "Api key "

    private lateinit var tilCity: TextInputLayout
    private lateinit var etCity: TextInputEditText
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var ivIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // MaterialToolbar’ı ActionBar olarak ayarla
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // View referanslarını al
        tilCity     = findViewById(R.id.tilCity)
        etCity      = findViewById(R.id.etCity)
        tvCity      = findViewById(R.id.tvCity)
        tvTemp      = findViewById(R.id.tvTemp)
        tvDesc      = findViewById(R.id.tvDesc)
        tvHumidity  = findViewById(R.id.tvHumidity)
        tvFeelsLike = findViewById(R.id.tvFeelsLike)
        ivIcon      = findViewById(R.id.ivIcon)

        // Uygulama açıldığında Kutahya için veri çek
        getWeatherData("Kutahya")

        // “Ara” ikonuna tıklandığında şehir sorgusunu başlat
        tilCity.setEndIconOnClickListener {
            val cityName = etCity.text.toString().trim()
            if (cityName.isNotEmpty()) {
                getWeatherData(cityName)
            } else {
                tilCity.error = "Lütfen bir şehir girin"
            }
        }
    }

    private fun getWeatherData(city: String) {
        // Hata mesajını temizle
        tilCity.error = null

        // Yükleniyor placeholder’ları
        tvCity.text      = city
        tvTemp.text      = "...°C"
        tvDesc.text      = "Yükleniyor..."
        tvHumidity.text  = "Nem: --%"
        tvFeelsLike.text = "Hissedilen: --°C"
        ivIcon.setImageResource(R.drawable.ic_search_24)

        // Retrofit’inizi kur
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(HavaDurumuApi::class.java)
        service.getWeather(city, apiKey, "metric", "tr")
            .enqueue(object : Callback<HavaDurumuCevap> {
                override fun onResponse(
                    call: Call<HavaDurumuCevap>,
                    response: Response<HavaDurumuCevap>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            // Gerçek verilerle UI güncelle
                            tvCity.text      = data.name
                            tvTemp.text      = "${data.main.temp.toInt()}°C"
                            tvDesc.text      = data.weather[0].description
                                .replaceFirstChar { it.uppercase() }
                            tvHumidity.text  = "Nem: ${data.main.humidity}%"
                            tvFeelsLike.text = "Hissedilen: ${data.main.feelsLike.toInt()}°C"

                            val iconUrl =
                                "https://openweathermap.org/img/wn/${data.weather[0].icon}@2x.png"
                            Glide.with(this@MainActivity)
                                .load(iconUrl)
                                .into(ivIcon)
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Şehir bulunamadı: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<HavaDurumuCevap>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Veri alınamadı: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
