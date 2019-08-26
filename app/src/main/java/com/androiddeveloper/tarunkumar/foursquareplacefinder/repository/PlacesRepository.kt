package com.androiddeveloper.tarunkumar.foursquareplacefinder.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddeveloper.tarunkumar.foursquareplacefinder.R
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.DetailsApiResponse
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.FoursquareService
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.SearchResponse
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.Venue
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.ResourceProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface PlacesRepository {
    fun search(query: String, near: String, limit: Int)
    fun getDetails(id: String): LiveData<VenueResponse>
    fun getSearchResultLiveData(): LiveData<PlacesResponse>
}

class PlacesRepositoryImpl(
    private val foursquareService: FoursquareService,
    private val resourceProvider: ResourceProvider
) : PlacesRepository {

    val placesResponse = MutableLiveData<PlacesResponse>()
    val venueResponse = MutableLiveData<VenueResponse>()

    override fun getSearchResultLiveData(): LiveData<PlacesResponse> = placesResponse

    override fun search(query: String, near: String, limit: Int) {
        foursquareService.search(query, near, limit).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                placesResponse.value = PlacesResponse(
                    response.body()?.response?.venues ?: emptyList(),
                    if (!response.isSuccessful) resourceProvider.getString(R.string.error_msg) else null
                )
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                placesResponse.value = PlacesResponse(emptyList(), t.message)
            }
        })
    }

    override fun getDetails(id: String): LiveData<VenueResponse> {
        foursquareService.details(id).enqueue(object : Callback<DetailsApiResponse> {
            override fun onResponse(call: Call<DetailsApiResponse>, response: Response<DetailsApiResponse>) {

                venueResponse.value = VenueResponse(
                    response.body()?.response?.venue,
                    if (!response.isSuccessful) resourceProvider.getString(R.string.error_msg) else null
                )
            }

            override fun onFailure(call: Call<DetailsApiResponse>, t: Throwable) {
                venueResponse.value = VenueResponse(null, t.message)
            }
        })
        return venueResponse
    }
}

data class PlacesResponse(val venues: List<Venue>, val errorMsg: String?)

data class VenueResponse(val venue: Venue?, val errorMsg: String?)