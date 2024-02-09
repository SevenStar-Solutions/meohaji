package com.example.meohaji.mypage

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meohaji.Constants
import com.example.meohaji.home.VideoForUi
import com.google.gson.GsonBuilder

class MyPageViewModel(
    private val dataPreferences: SharedPreferences,
    private val namePreferences: SharedPreferences,
    private val imagePreferences: SharedPreferences): ViewModel() {

    private val _uiState: MutableLiveData<List<VideoForUi>> = MutableLiveData()
    val uiState: LiveData<List<VideoForUi>> get() = _uiState

    private val _userImage: MutableLiveData<String> = MutableLiveData()
    val userImage: LiveData<String> get() = _userImage

    private val _userName: MutableLiveData<String> = MutableLiveData()
    val userName: LiveData<String> get() = _userName

    fun loadData() {
        val allEntries: Map<String, *> = dataPreferences.all
        val bookmarks = ArrayList<VideoForUi>()
        val gson = GsonBuilder().create()
        for((key, value) in allEntries) {
            val item = gson.fromJson(value as String, VideoForUi::class.java)
            bookmarks.add(item)
        }
        _uiState.value = bookmarks
    }

    fun loadUserData() {
        _userImage.value = imagePreferences.getString(Constants.PREFS_DIALOG_IMG_KEY, null)
        _userName.value = namePreferences.getString(Constants.PREFS_DIALOG_NAME_KEY, null)
    }

    fun clearData() {
        dataPreferences.edit().clear().apply()
        _uiState.value = listOf()
    }
}