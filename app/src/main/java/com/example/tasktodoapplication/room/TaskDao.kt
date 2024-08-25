package com.example.tasktodoapplication.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    // karena akan menggunakan courotine(async) maka tambahkan suspend
    @Insert
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task WHERE isDone = 0")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM task WHERE id=:task_id")
    suspend fun getTask(task_id: Int): List<Task>

    @Query("UPDATE task SET isDone = :isDone WHERE id = :id")
    suspend fun markAsDone(id: Int, isDone: Boolean)

    @Query("SELECT * FROM task WHERE isDone = 1")
    fun getCompletedTasks(): List<Task>
}