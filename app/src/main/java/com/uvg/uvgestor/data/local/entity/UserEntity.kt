package com.uvg.uvgestor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uvg.uvgestor.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isMinor: Boolean = false
)

fun UserEntity.toUser(): User {
    return User(
        id = id,
        email = email,
        name = name,
        createdAt = createdAt,
        guardians = emptyList(),
        isMinor = isMinor
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        name = name,
        createdAt = createdAt,
        isMinor = isMinor
    )
}
