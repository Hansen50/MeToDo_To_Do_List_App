package com.example.tasktodoapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Task::class], // Jika ingin menambahkan entitas, beri koma setelah class
    version = 2
)
abstract class TaskDB : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    // Copy code ini jika ingin menambahkan abstrak apabila ada entitas baru

    companion object {
        @Volatile
        private var INSTANCE: TaskDB? = null

        // Define migration object
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE task ADD COLUMN isDone INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): TaskDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDB::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
