package com.example.meohaji.home

enum class SortOrder(
    val str: String,
    val id: String
) {
    DESCENDING_RECOMMENDATION("추천 순", "1"),
    DESCENDING_VIEW_COUNT("조회수 순", "2"),
    DESCENDING_LIKE_COUNT("좋아요 순", "3"),
    ASCENDING_NEW_VIDEO("최신 순", "4"),
}