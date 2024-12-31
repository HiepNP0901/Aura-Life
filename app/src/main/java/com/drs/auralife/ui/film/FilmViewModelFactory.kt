package com.drs.auralife.ui.film

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.data.FilmRepository

class FilmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FilmsViewModel::class.java) -> {
                val repository = FilmRepository(context)
                @Suppress("UNCHECKED_CAST")
                FilmsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}