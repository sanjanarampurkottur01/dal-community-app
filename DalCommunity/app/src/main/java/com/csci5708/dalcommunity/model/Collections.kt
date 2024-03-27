package com.csci5708.dalcommunity.model

class Collections {
    companion object {
        fun getPollPosts(): ArrayList<PollPost> {
            val pollsList: ArrayList<PollPost> = ArrayList()

            val pollValuesList: ArrayList<PollValue> = arrayListOf(
                PollValue("Java", 0, false),
                PollValue("Python", 0, false),
                PollValue("Kotlin", 0, false),
                PollValue("C", 0, false)
            )
            val pollPost1 = PollPost("", "" , 2,"Which is the best language?", pollValuesList, false)
            pollPost1.calculatePercentages()
            pollsList.add(pollPost1)
            pollsList.add(
                PollPost("", "", 2,
                    "What is 2+2?",
                    arrayListOf(
                        PollValue("4", 75, false),
                        PollValue("6", 20, false),
                        PollValue("2", 2, false),
                        PollValue("0", 3, true)
                    ),
                    false
                )
            )


            return pollsList
        }
    }
}