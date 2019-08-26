package com.androiddeveloper.tarunkumar.foursquareplacefinder

import android.app.Application
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.appModule
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.repositoryModule
import com.androiddeveloper.tarunkumar.foursquareplacefinder.utils.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, repositoryModule, viewModelModule))
        }
    }
}