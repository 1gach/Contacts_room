package com.example.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    val firstname: String,
    val lastname: String,
    val number: String,
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null
)
