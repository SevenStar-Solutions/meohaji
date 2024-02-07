package com.example.meohaji

import android.content.Context

object Utils {
    //데이터 포멧
    //키보드 숨김

    fun saveMaInfo(context: Context, name: String, image: String) {
        val namePrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_NAME, Context.MODE_PRIVATE)
        namePrefs.edit().putString(Constants.PREFS_DIALOG_NAME_KEY, name).apply()
        val imgPrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_IMG, Context.MODE_PRIVATE)
        imgPrefs.edit().putString(Constants.PREFS_DIALOG_IMG_KEY, image).apply()

    }

    fun getMyInfo(context: Context): Pair<String?, String?> {
        val namePrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_NAME, 0)
        val name = namePrefs.getString(Constants.PREFS_DIALOG_NAME_KEY, null)

        val imgPrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_IMG, 0)
        val image = imgPrefs.getString(Constants.PREFS_DIALOG_IMG_KEY, null)

        return Pair(name, image)
        //사용: val (name, _) = getLastSearch(context)
    }
}
