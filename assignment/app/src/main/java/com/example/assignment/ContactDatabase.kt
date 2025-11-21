package com.example.assignment
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 1)
abstract    class ContactDatabase : RoomDatabase(){
    // Function to access the DAO [cite: 3565]
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        // Singleton pattern to get the database instance [cite: 3611]
        fun getInstance(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_db" // Database name [cite: 3614]
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}