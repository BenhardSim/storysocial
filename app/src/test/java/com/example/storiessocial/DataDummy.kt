package com.example.storiessocial

import com.example.storiessocial.model.local.entity.StoryItem

object DataDummy {

    fun generateStories(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryItem(
                i.toString(),
                "https://assets.pikiran-rakyat.com/crop/0x0:0x0/x/photo/2022/04/19/668037258.png",
                "2023-05-06T14:30:00.000Z",
                "benhard",
                "sangat pro sekali",
                3.2,
                4.1
            )
            items.add(quote)
        }
        return items
    }
}