package com.example.tasktodoapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    val title: String,
    val content: String,
    val date: String,
    val time: String,
    var isDone: Boolean = false

)