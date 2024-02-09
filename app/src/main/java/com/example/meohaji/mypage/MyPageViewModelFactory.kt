package com.example.meohaji.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meohaji.Constants
import com.example.meohaji.detail.DetailTags

class MyPageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(
                context.getSharedPreferences(
                    DetailTags.PREF_KEY,
                    Context.MODE_PRIVATE
                ),
                context.getSharedPreferences(Constants.PREFS_DIALOG_NAME, 0),
                context.getSharedPreferences(Constants.PREFS_DIALOG_IMG, 0)
            ) as T
        }
        throw IllegalArgumentException("Unknown viewModel Class")
    }
}