package com.androiddeveloper.tarunkumar.foursquareplacefinder.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.androiddeveloper.tarunkumar.foursquareplacefinder.db.PlacesDatabase
import com.androiddeveloper.tarunkumar.foursquareplacefinder.db.Venue
import java.util.concurrent.Executor

interface FavoritesRepository {
    fun getItemIds(): LiveData<Set<String>>
    fun add(id: String)
    fun remove(id: String)
    fun isInFavorites(id: String): LiveData<Venue?>
}


class FavoritesRepositoryImpl(private val placesDatabase: PlacesDatabase, private val executor: Executor) :
    FavoritesRepository {

    override fun isInFavorites(id: String): LiveData<Venue?> = placesDatabase.venueDao().isFavorite(id)

    override fun add(id: String) {
        executor.execute { placesDatabase.venueDao().insertVenue(Venue(id)) }
    }

    override fun remove(id: String) {
        executor.execute { placesDatabase.venueDao().deleteById(id) }
    }

    override fun getItemIds(): LiveData<Set<String>> {
        return Transformations.map(placesDatabase.venueDao().getVenues(), ::toSet)
    }

    private fun toSet(venues: List<Venue>): Set<String> {
        val set = hashSetOf<String>()
        venues.forEach {
            set.add(it.id)
        }
        return set
    }
}