package com.example.meohaji

import android.app.Activity
import android.content.Context
import com.example.meohaji.Constants.PREF_KEY
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.round

object Utils {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    fun saveMyInfo(context: Context, name: String, image: String) {
        val namePrefs =
            context.getSharedPreferences(Constants.PREFS_DIALOG_NAME, Context.MODE_PRIVATE)
        namePrefs.edit().putString(Constants.PREFS_DIALOG_NAME_KEY, name).apply()
        val imgPrefs =
            context.getSharedPreferences(Constants.PREFS_DIALOG_IMG, Context.MODE_PRIVATE)
        imgPrefs.edit().putString(Constants.PREFS_DIALOG_IMG_KEY, image).apply()

    }

    fun getMyInfo(context: Context): Pair<String?, String?> {
        val namePrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_NAME, 0)
        val name = namePrefs.getString(Constants.PREFS_DIALOG_NAME_KEY, null)

        val imgPrefs = context.getSharedPreferences(Constants.PREFS_DIALOG_IMG, 0)
        val image = imgPrefs.getString(Constants.PREFS_DIALOG_IMG_KEY, null)

        return Pair(name, image)
    }

    fun deletePrefItem(context: Context) {
        val prefs = context.getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear().apply()
    }

    fun saveCounts(context: Context, view: Int, like: Int, comment: Int) {
        val viewPrefs =
            context.getSharedPreferences(Constants.PREFS_VIEW_COUNT, Context.MODE_PRIVATE)
        viewPrefs.edit().putString(Constants.PREFS_VIEW_COUNT_KEY, view.toString()).apply()
        val likePrefs =
            context.getSharedPreferences(Constants.PREFS_LIKE_COUNT, Context.MODE_PRIVATE)
        likePrefs.edit().putString(Constants.PREFS_LIKE_COUNT_KEY, like.toString()).apply()
        val commentPrefs =
            context.getSharedPreferences(Constants.PREFS_COMMENT_COUNT, Context.MODE_PRIVATE)
        commentPrefs.edit().putString(Constants.PREFS_COMMENT_COUNT_KEY, comment.toString()).apply()
    }

    fun getCounts(context: Context): Triple<String, String, String> {
        val viewPrefs =
            context.getSharedPreferences(Constants.PREFS_VIEW_COUNT, Context.MODE_PRIVATE)
        val likePrefs =
            context.getSharedPreferences(Constants.PREFS_LIKE_COUNT, Context.MODE_PRIVATE)
        val commentPrefs =
            context.getSharedPreferences(Constants.PREFS_COMMENT_COUNT, Context.MODE_PRIVATE)

        return Triple(
            likePrefs.getString(Constants.PREFS_LIKE_COUNT_KEY, null) ?: "0",
            viewPrefs.getString(Constants.PREFS_VIEW_COUNT_KEY, null) ?: "0",
            commentPrefs.getString(Constants.PREFS_COMMENT_COUNT_KEY, null) ?: "0"
        )
    }

    fun setCount(count: Long): String {
        var ans = ""
        if (count / 10000L <= 0L) {
            ans = "$count"
        } else if (count / 10000L > 0L) {
            ans = if ((count / 10000L) / 10000L <= 0L) {
                "${count / 10000L}.${(count % 10000L).toString().first()}만"
            } else {
                "${(count / 10000L) / 10000L}.${((count / 10000L) % 10000L).toString().first()}억"
            }
        }
        return ans
    }

    fun calRecommendScore(
        description: String,
        viewCount: Int,
        likeCount: Int,
        commentCount: Int
    ): Double {
        val viewScore = viewCount * 0.5 * (1.0 / viewCount.toString().length)
        val likeScore = likeCount * 0.3 * (1.0 / likeCount.toString().length)
        val commentScore = commentCount * 0.2 * (1.0 / commentCount.toString().length)
        val isShorts = description.contains("shorts")

        var totalScore =
            if (isShorts) (viewScore + likeScore + commentScore) / viewScore * 3.3 * 1.5 else (viewScore + likeScore + commentScore) / viewScore * 3.3
        totalScore = round(totalScore * 10) / 10
        return if (totalScore > 5.0) 5.0 else totalScore
    }
}
