package com.mgws.adownkyi.work

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

class WorkManagerInitializer : Initializer<WorkManager> {

    override fun create(
        @ApplicationContext context: Context,
    ): WorkManager {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkManagerInitializerEntryPoint::class.java
        )

        val configuration = Configuration
            .Builder()
            .setWorkerFactory(entryPoint.hiltWorkerFactory())
            .build()
        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface WorkManagerInitializerEntryPoint {
        fun hiltWorkerFactory(): HiltWorkerFactory
    }
}