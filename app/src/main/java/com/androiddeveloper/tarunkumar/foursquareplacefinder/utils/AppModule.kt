package com.androiddeveloper.tarunkumar.foursquareplacefinder.utils

import androidx.room.Room
import com.androiddeveloper.tarunkumar.foursquareplacefinder.db.DB_NAME
import com.androiddeveloper.tarunkumar.foursquareplacefinder.db.PlacesDatabase
import com.androiddeveloper.tarunkumar.foursquareplacefinder.network.FoursquareService
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.FavoritesRepository
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.FavoritesRepositoryImpl
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.PlacesRepository
import com.androiddeveloper.tarunkumar.foursquareplacefinder.repository.PlacesRepositoryImpl
import com.androiddeveloper.tarunkumar.foursquareplacefinder.viewmodel.DetailViewModel
import com.androiddeveloper.tarunkumar.foursquareplacefinder.viewmodel.SearchViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val appModule = module {

    single<OkHttpClient> {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        builder.build()
    }

    single<FoursquareService> {
        Retrofit.Builder()
            .baseUrl(FoursquareService.API_CLIENT_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(FoursquareService::class.java)
    }

    single<Executor> { Executors.newSingleThreadExecutor() }

    single { Room.databaseBuilder(androidApplication(), PlacesDatabase::class.java, DB_NAME).build() }

    single<ResourceProvider> { ResourceProviderImpl(androidApplication()) }
}

val repositoryModule = module {
    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { (id: String) -> DetailViewModel(id, get(), get(), get()) }
}