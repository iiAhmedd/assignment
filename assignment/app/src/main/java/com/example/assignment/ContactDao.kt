package com.example.assignment
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContactDao {
    // Insert a new contact [cite: 3489]
    @Insert
    suspend fun insertContact(contact: Contact)

    // Get all contacts [cite: 3494]
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<Contact>

    // Filter contacts by category
    @Query("SELECT * FROM contacts WHERE category = :category")
    suspend fun getContactsByCategory(category: String): List<Contact>

    // Helper to get unique categories for your Spinner
    @Query("SELECT DISTINCT category FROM contacts")
    suspend fun getAllCategories(): List<String>
}