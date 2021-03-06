package com.example.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.models.WeatherDataClass
import com.example.weather.retrofit.Query
import com.example.weather.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var binding: FragmentWeatherBinding

class WeatherFragment : Fragment() {
    private val api: Query =
        RetrofitClient.getClient()!!.create(Query::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val key = getString(R.string.weatherApiKey)
        val units = "metric"
        val lang = "ru"
        binding.btnShowWeather.setOnClickListener {

            api.getWeatherList(
                city = binding.cityEditText.text.toString(),
                key,
                units,
                lang
            ).enqueue(object : Callback<WeatherDataClass> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherDataClass>,
                    response: Response<WeatherDataClass>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        binding.description.text = "Описание"
                        binding.country.text = "Страна"
                        binding.windSpeed.text = "Скорость ветра"
                        binding.clouds.text = "Облака"
                        binding.lat.text = "Долгота"
                        binding.lon.text = "Широта"

                        val data1 = data.weather.toList()
                        val data2 = data1.firstOrNull()
                        binding.descriptionTextView.text = data2!!.description
                        binding.tempTextView.text = data.main.temp.toString() + " ℃"
                        binding.cloudsTextView.text = "min " + data.clouds.all.toString()
                        binding.latTextView.text = "lat " + data.coord.lat.toString()
                        binding.lonTextView.text = "lon " + data.coord.lon.toString()
                        binding.windSpeedTextView.text = "wind speed " + data.wind.speed.toString()
                        binding.countryTextView.text = "county " + data.sys.country
                        when {
                            data.main.temp < -15.1  -> binding.comment.text =
                                "Холодно пи*дец))))"
                            data.main.temp < 0.1 && data.main.temp < -10.1 -> binding.comment.text =
                                "Холодно нахрен!"
                            data.main.temp > 10.1 && data.main.temp < 20.1 -> binding.comment.text =
                                "Ну так, прохладно..."
                            data.main.temp > 20.1 -> binding.comment.text = "Нормас!"
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Введите верно город или через дефис",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<WeatherDataClass>, t: Throwable) {
                    Log.e("Error", "Error getting weather", t)
                    Toast.makeText(
                        requireActivity(),
                        "Something wrong, ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }


    }


}



