package com.example.meohaji.detail

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meohaji.home.VideoForUi
import com.google.gson.GsonBuilder

class DetailViewModel(private val preferences: SharedPreferences): ViewModel() {

    private val _isContain: MutableLiveData<Boolean> = MutableLiveData()
    val isContain: LiveData<Boolean> get() = _isContain

    private val _uiState: MutableLiveData<VideoForUi?> = MutableLiveData()
    val uiState: LiveData<VideoForUi?> get() = _uiState

    private val _isSave: MutableLiveData<Boolean> = MutableLiveData()
    val isSave: LiveData<Boolean> get() = _isSave

    private val _formatUrl: MutableLiveData<String> = MutableLiveData()
    val formatUrl: LiveData<String> get() = _formatUrl

    fun setButton() {
        when(uiState.value?.id) {
            !in preferences.all.keys -> _isContain.value = true
            in preferences.all.keys -> _isContain.value = false
        }
    }

    fun setUiData(video: VideoForUi?) {
        _uiState.value = video
    }

    fun clickSaveOrDelete() {
        when(uiState.value?.id) {
            !in preferences.all.keys -> {
                saveData (uiState.value)
                _isSave.value = false
            }
            in preferences.all.keys -> {
                deleteData(uiState.value?.id)
                _isSave.value = true
            }
        }
    }

    fun clickShare() {
        _formatUrl.value = "https://youtube.com/video/${uiState.value?.id}"
    }

    fun setCount(count: Int?):String {
        if (count != null) {
            val countLong = count.toLong()
            var ans = ""
            if(countLong / 10000L <= 0L) {
                ans = "$countLong"
            } else if(countLong / 10000L > 0L) {
                ans = if((countLong / 10000L) / 10000L <= 0L) {
                    "${countLong/10000L}.${(countLong%10000L).toString().first()}만"
                } else {
                    "${(countLong/10000L)/10000L}.${((countLong/10000L)%10000L).toString().first()}억"
                }
            }
            return ans
        }

        return "0"
    }

    // SharedPreference에 저장(key = id, value = 값.toString)
    private fun saveData(test:VideoForUi?) {
        val editor = preferences.edit()
        val gson = GsonBuilder().create()
        editor.putString(test?.id, gson.toJson(test))
        editor.apply()
    }

    // SharedPreference에서 삭제
    private fun deleteData(id: String?) {
        val editor = preferences.edit()
        editor.remove(id)
        editor.apply()
    }
}