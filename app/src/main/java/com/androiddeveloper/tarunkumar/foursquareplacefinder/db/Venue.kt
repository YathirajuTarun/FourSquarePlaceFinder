package com.androiddeveloper.tarunkumar.foursquareplacefinder.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Venue(@PrimaryKey val id: String)