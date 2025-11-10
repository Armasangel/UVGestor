package com.uvg.uvgestor.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uvg.uvgestor.data.local.dao.ExpenseDao
import com.uvg.uvgestor.data.local.dao.UserDao
import com.uvg.uvgestor.data.local.entity.ExpenseEntity
import com.uvg.uvgestor.data.local.entity.UserEntity

@Database(
    entities = [ExpenseEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uvgestor_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
