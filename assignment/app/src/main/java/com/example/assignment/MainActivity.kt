package com.example.assignment

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var db: ContactDatabase

    // Lists to hold data for adapters
    private var contactList = mutableListOf<Contact>()
    private var categoryList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        db = ContactDatabase.getInstance(this)

        // initialize Views
        val saveButton = findViewById<Button>(R.id.button)
        val filterButton = findViewById<Button>(R.id.button2)
        val showAllButton = findViewById<Button>(R.id.button3)

        // FIXED: ID matches XML "listViewContacts"
        val listview = findViewById<ListView>(R.id.listViewContacts)
        val spinner = findViewById<Spinner>(R.id.spinner)

        val edittextName = findViewById<EditText>(R.id.editText)
        val edittextPhone = findViewById<EditText>(R.id.editText2)
        val edittextCategory = findViewById<EditText>(R.id.editText3)

        // --- SAVE BUTTON ---
        saveButton.setOnClickListener {
            val name = edittextName.text.toString().trim()
            val phone = edittextPhone.text.toString().trim()
            val category = edittextCategory.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty() && category.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // 1. Insert into DB
                    db.contactDao().insertContact(Contact(name = name, phone = phone, category = category))

                    // 2. Switch to Main Thread for UI updates
                    withContext(Dispatchers.Main) {
                        edittextName.text.clear()
                        edittextPhone.text.clear()
                        edittextCategory.text.clear()
                        Toast.makeText(this@MainActivity, "Contact Saved", Toast.LENGTH_SHORT).show()

                        // Optional: Refresh the list immediately after saving
                        refreshData(listview, spinner)
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // --- FILTER BUTTON --- (Moved outside Save Button)
        filterButton.setOnClickListener {
            val selectedCategory = spinner.selectedItem?.toString()

            if (selectedCategory != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val filteredList = db.contactDao().getContactsByCategory(selectedCategory)

                    withContext(Dispatchers.Main) {
                        val displayList = filteredList.map { "Name: ${it.name}\nPhone: ${it.phone}" }
                        val adapter = ArrayAdapter(
                            this@MainActivity,
                            android.R.layout.simple_list_item_1,
                            displayList
                        )
                        listview.adapter = adapter
                    }
                }
            } else {
                Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show()
            }
        }

        // --- SHOW ALL BUTTON --- (Moved outside Save Button)
        showAllButton.setOnClickListener {
            refreshData(listview, spinner)
        }

        // Load initial data when app starts
        refreshData(listview, spinner)
    }

    // Helper function to refresh List and Spinner
    private fun refreshData(listview: ListView, spinner: Spinner) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Fetch all contacts
            val contacts = db.contactDao().getAllContacts()

            // Calculate unique categories for the spinner
            val uniqueCategories = contacts.map { it.category }.distinct()

            withContext(Dispatchers.Main) {
                // Update ListView
                contactList.clear()
                contactList.addAll(contacts)

                val listAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    contacts.map { "${it.name} - ${it.category}" }
                )
                listview.adapter = listAdapter

                // Update Spinner with categories
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    uniqueCategories
                )
                spinner.adapter = spinnerAdapter
            }
        }
    }
}