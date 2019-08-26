package com.androiddeveloper.tarunkumar.foursquareplacefinder.viewmodel

import androidx.lifecycle.ViewModel
import android.provider.Browser
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.androiddeveloper.tarunkumar.foursquareplacefinder.BuildConfig
import com.androiddeveloper.tarunkumar.foursquareplacefinder.R
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.Venue
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.FavoritesRepository
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.PlacesRepository
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.VenueResponse
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.ResourceProvider
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.observeOnce

const val STATIC_MAP_ENDPOINT = "http://maps.googleapis.com/maps/api/staticmap"
const val DEFAULT_MAP_IMAGE_SIZE = "400x400"
const val DEFAULT_MARKER_CENTER = "Seattle"
const val DEFAULT_ZOOM = 13
const val DEFAULT_SCALE = 1

class DetailViewModel(
    private val id: String,
    private val placesRepository: PlacesRepository,
    private val favoritesRepository: FavoritesRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private var urlStr: String = ""
    private var phoneStr: String = ""

    val isSelected = ObservableField<Boolean>()
    val title = ObservableField<String>()
    val description = ObservableField<String>()
    val phone = ObservableField<String>()
    val price = ObservableField<String>()
    val hours = ObservableField<String>()
    val mapUrl = ObservableField<String>()
    val url = ObservableField<String>()

    private val venueLiveData: LiveData<VenueResponse> = placesRepository.getDetails(id)
    val errorLiveData = MediatorLiveData<String>()

    private val venueObserver = Observer<VenueResponse> { updateView(it) }

    init {
        favoritesRepository.isInFavorites(id).observeOnce(Observer { isSelected.set(it != null) })

        venueLiveData.observeForever(venueObserver)
    }

    private fun updateView(it: VenueResponse) {
        if (it.errorMsg != null) {
            errorLiveData.value = "${it.errorMsg}"
        }
        val venue = it.venue
        urlStr = venue?.canonicalUrl ?: ""
        phoneStr = venue?.contact?.phone ?: ""
        val statusStr = venue?.hours?.status ?: ""
        val priceStr = venue?.price?.message ?: ""


        title.set(venue?.name)
        description.set((venue?.description))

        if (urlStr.isNotEmpty())
            url.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_url_text
                    ), urlStr
                )
            )

        if (phoneStr.isNotEmpty())
            phone.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_phone_text
                    ),
                    phoneStr
                )
            )

        if (statusStr.isNotEmpty())
            hours.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_status_text
                    ),
                    statusStr
                )
            )


        if (priceStr.isNotEmpty())
            price.set(
                String.format(
                    resourceProvider.getString(
                        R.string.generic_price_text
                    ),
                    venue?.price?.tier,
                    priceStr
                )
            )


        mapUrl.set(buildStaticMapUrl(venue))
    }

    private fun buildStaticMapUrl(venue: Venue?) =
        STATIC_MAP_ENDPOINT +
                "?center=$DEFAULT_MARKER_CENTER" +
                "&zoom=$DEFAULT_ZOOM" +
                "&scale=$DEFAULT_SCALE" +
                "&size=$DEFAULT_MAP_IMAGE_SIZE" +
                "&markers=$SEATTLE_LAT,$SEATTLE_LNG|${venue?.location?.lat},${venue?.location?.lng}" +
                "&key=${BuildConfig.GOOGLE_MAPS_API_KEY}"

    fun onUrlClick(view: View) {
        val context = view.context
        if (urlStr.isEmpty()) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
        intent.putExtra(
            Browser.EXTRA_APPLICATION_ID,
            context.packageName
        )
        context.startActivity(intent)
    }

    fun onPhoneClick(view: View) {
        val context = view.context
        if (phoneStr.isEmpty()) return

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel: $phoneStr"))
        intent.putExtra(
            Browser.EXTRA_APPLICATION_ID,
            context.packageName
        )
        context.startActivity(intent)
    }

    fun onStarClick(view: View) {
        val imageButton = view as ImageButton
        val isSelected = imageButton.isSelected
        if (isSelected) {
            favoritesRepository.remove(id)
        } else {
            favoritesRepository.add(id)
        }
        imageButton.isSelected = !isSelected
    }

    override fun onCleared() {
        super.onCleared()
        venueLiveData.removeObserver(venueObserver)
    }
}