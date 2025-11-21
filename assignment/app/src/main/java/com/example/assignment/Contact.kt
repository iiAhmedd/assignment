package com.example.assignment
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,   // Column for name [cite: 3417]
    val phone: String,
    val category: String
)
