package com.example.meohaji.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(
                context.getSharedPreferences(
                    DetailTags.PREF_KEY,
                    Context.MODE_PRIVATE
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown viewModel Class")
    }
}