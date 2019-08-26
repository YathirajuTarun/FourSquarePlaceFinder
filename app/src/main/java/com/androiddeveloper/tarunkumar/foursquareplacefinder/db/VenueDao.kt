package com.androiddeveloper.tarunkumar.foursquareplacefinder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VenueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVenue(venue: Venue)

    @Query("DELETE FROM Venue WHERE id = :id")
    fun deleteById(id: String)

    @Query("SELECT * FROM Venue")
    fun getVenues(): LiveData<List<Venue>>

    @Query("SELECT * FROM Venue WHERE id LIKE :id")
    fun isFavorite(id: String): LiveData<Venue?>
}