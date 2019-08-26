package com.androiddeveloper.tarunkumar.foursquareplacefinder.db

import androidx.room.Database
import androidx.room.RoomDatabase

const val DB_NAME = "places.db"

@Database(entities = [Venue::class], version = 1)
abstract class PlacesDatabase : RoomDatabase() {
    abstract fun venueDao(): VenueDao
}