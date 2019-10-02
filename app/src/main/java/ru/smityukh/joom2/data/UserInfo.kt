package ru.smityukh.joom2.data

data class UserInfo(val name: String, val displayName: String, val profileUrl: String, val avatarUrl: String) {
    companion object {
        val EMPTY = UserInfo("", "", "", "")
    }
}