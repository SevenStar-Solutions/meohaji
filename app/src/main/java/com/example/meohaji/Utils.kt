package com.example.meohaji

import android.app.Activity
import android.content.Context
import com.example.meohaji.detail.DetailTags.PREF_KEY
import com.example.meohaji.mypage.SavedItem
import com.google.gson.GsonBuilder

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

    fun getPrefBookmarkItems(context: Context): ArrayList<SavedItem> {
        val prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        val bookmarkItems = ArrayList<SavedItem>()
        val gson = GsonBuilder().create()

        prefs.all.forEach { (key, value) ->
            val item = gson.fromJson(value as String, SavedItem::class.java)
            bookmarkItems.add(item)
        }
        return bookmarkItems
    }

    fun deletePrefItem(context: Context) {
        val prefs = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear().apply()
    }




}
