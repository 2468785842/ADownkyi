package com.mgws.datastore.annotations

/**
 * 生成 DataStore Serializer by kotlin.serialization.protobuf
 * 添加到 Hilt module 生成provider方法, 自动注入
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class HiltDataStore(val dataStoreFile: String)