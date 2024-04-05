package com.mgws.adownkyi.di

import com.mgws.adownkyi.core.bilibili.login.LoginService
import com.mgws.adownkyi.core.bilibili.video.VideoInfoService
import com.mgws.adownkyi.core.bilibili.videoStream.VideoStreamService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideVideoInfoService() = VideoInfoService()

    @Provides
    @Singleton
    fun provideVideoStreamService() = VideoStreamService()

    @Provides
    @Singleton
    fun provideLoginService() = LoginService()
}
