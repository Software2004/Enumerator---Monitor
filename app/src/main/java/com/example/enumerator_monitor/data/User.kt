package com.example.enumerator_monitor.data

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.ENUMERATOR,
    val blockNo: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    ENUMERATOR,
    TEHSIL_MONITOR
}
