package com.csci5708.dalcommunity.model

data class SavedPostGroup(val name: String, val posts: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SavedPostGroup

        if (name != other.name) return false
        if (!posts.contentEquals(other.posts)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + posts.contentHashCode()
        return result
    }
}