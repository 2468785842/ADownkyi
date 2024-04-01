package com.mgws.adownkyi.di

import android.content.Context
import android.os.Environment
import com.mgws.adownkyi.core.downloader.BuiltinDownloader
import com.mgws.adownkyi.core.downloader.Downloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ADownKyiCoreModule {


    @Provides
    @Singleton
    fun provideDownloader(
        @ApplicationContext context: Context,
    ): Downloader =
        BuiltinDownloader(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path)
//        AndroidDownloader(context, Environment.DIRECTORY_DOWNLOADS)

}