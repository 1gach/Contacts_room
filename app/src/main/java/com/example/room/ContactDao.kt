package com.example.room

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
   @Upsert
   suspend fun upsertContact(contact: Contact)

    @Query("SELECT * FROM Contact")
    fun getContactsUnsorted(): Flow<List<Contact>>

    @Delete
    suspend fun deleteContact(contact: Contact)
    @Query("SELECT * FROM Contact ORDER BY lastname ASC")
     fun getContactOrderedByLastname(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY firstname ASC")
     fun getContactOrderedByFirstname(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY number ASC")
     fun getContactOrderedByNumber(): Flow<List<Contact>>

}