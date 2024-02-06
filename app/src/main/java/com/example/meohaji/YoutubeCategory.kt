package com.example.meohaji

enum class YoutubeCategory(
    val str: String,
    val id: String
) {
    FILM_ANIMATION("영화&애니메이션", "1"),
    AUTOS_VEHICLES("자동차&탈 것", "2"),
    MUSIC("음악", "10"),
    PETS_ANIMALS("애완동물 & 동물", "15"),
    SPORTS("스포츠", "17"),
    TRAVEL_EVENTS("여행 & 행사", "19"),
    GAMING("게임", "20"),
    PEOPLE_BLOGS("일상 & 브이로그", "22"),
    COMEDY("코미디", "23"),
    ENTERTAINMENT("엔터테인먼트", "24"),
    NEWS_POLITICS("뉴스 & 정치", "25"),
    HOWTO_STYLE("정보 & 스타일", "26"),
    EDUCATION("교육", "27"),
    SCIENCE_TECHNOLOGY("과학 & 기술", "28"),
}