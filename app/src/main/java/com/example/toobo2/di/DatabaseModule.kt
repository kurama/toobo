package com.example.toobo2.di

import android.content.Context
import androidx.room.Room
import com.example.toobo2.data.local.WeatherDatabase
import com.example.toobo2.data.local.dao.LocationDao
import com.example.toobo2.data.local.dao.WeatherCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            WeatherDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: WeatherDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideWeatherCacheDao(database: WeatherDatabase): WeatherCacheDao {
        return database.weatherCacheDao()
    }
}
