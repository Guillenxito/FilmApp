package com.example.filmapp.model

import android.annotation.SuppressLint
import android.content.Context

class MovieDatabase(private val context: Context) {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private var movieDatabase: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            if(movieDatabase == null){
                movieDatabase = MovieDatabase(context.applicationContext)
            }
            return movieDatabase!!
        }
    }
}