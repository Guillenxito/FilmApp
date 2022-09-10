package com.example.filmapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.filmapp.R
import com.example.filmapp.databinding.ActivityMainBinding
import com.example.filmapp.model.Movie
import com.example.filmapp.model.MovieDbClient
import com.example.filmapp.model.MovieDbResult
import com.example.filmapp.ui.detail.DetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val moviesAdapter = MoviesAdapter(emptyList()) { navigateTo(it) }
    private val requestPermissionLauncher =
    registerForActivityResult( ActivityResultContracts.RequestPermission()) { isGranted ->
       requestPopularMovies(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding:ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        binding.recycler.adapter = moviesAdapter

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

    }


    @SuppressLint("MissingPermission")
    private fun requestPopularMovies(isLocationGranted: Boolean) {
        if ( isLocationGranted ) {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                doRequestPopularMovies(getRegionFromLocation(it.result))
            }
        }else{
            doRequestPopularMovies("US")
        }
    }

    private fun doRequestPopularMovies(region: String) {
        lifecycleScope.launch{
            val apiKey= getString(R.string.api_key)
            val popularMovies: MovieDbResult = MovieDbClient.service.listPopularMovies(apiKey,region)
            moviesAdapter.movies = popularMovies.results
            moviesAdapter.notifyDataSetChanged()
        }
    }

    private fun getRegionFromLocation(location: Location?): String {
        if(location == null){
            return "US"
        }
        val geocoder = Geocoder(this)
        val result = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        )
        return result.firstOrNull()?.countryCode ?: "US"
    }

    private fun navigateTo(movie: Movie){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie)
        startActivity(intent)

    }
}