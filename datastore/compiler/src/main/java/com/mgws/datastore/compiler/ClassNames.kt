package com.mgws.datastore.compiler

import com.squareup.kotlinpoet.ClassName

object ClassNames {
    val DISPATCHERS = ClassName("kotlinx.coroutines", "Dispatchers")
    val WITH_CONTEXT = ClassName("kotlinx.coroutines", "withContext")
    val DECODE_FROM_BYTE_ARRAY = ClassName("kotlinx.serialization", "decodeFromByteArray")
    val ENCODE_TO_BYTE_ARRAY = ClassName("kotlinx.serialization", "encodeToByteArray")
    val EXPERIMENTAL_SERIALIZATION_API =
        ClassName("kotlinx.serialization", "ExperimentalSerializationApi")
    val OPT_IN = ClassName("kotlin", "OptIn")

    val PROTOBUF = ClassName("kotlinx.serialization.protobuf", "ProtoBuf")
    val INPUT_STREAM = ClassName("java.io", "InputStream")
    val OUTPUT_STREAM = ClassName("java.io", "OutputStream")

    val SERIALIZABLE = ClassName("kotlinx.serialization", "Serializable")

    val HILT_DATA_STORE = ClassName("com.mgws.datastore.annotations", "HiltDataStore")

    val PROVIDES = ClassName("dagger", "Provides")
    val MODULE = ClassName("dagger", "Module")
    val INSTALL_IN = ClassName("dagger.hilt", "InstallIn")
    val ORIGINATING_ELEMENT = ClassName("dagger.hilt.codegen", "OriginatingElement")
    val SINGLETON_COMPONENT = ClassName("dagger.hilt.components", "SingletonComponent")
    val APPLICATION_CONTEXT = ClassName("dagger.hilt.android.qualifiers", "ApplicationContext")

    val SINGLETON = ClassName("javax.inject", "Singleton")

    val CONTEXT = ClassName("android.content", "Context")
    val DATASTORE_FACTORY = ClassName("androidx.datastore.core", "DataStoreFactory")

    val DATASTORE = ClassName("androidx.datastore.core", "DataStore")
    val DATASTORE_FILE = ClassName("androidx.datastore", "dataStoreFile")
    val SERIALIZER = ClassName("androidx.datastore.core", "Serializer")
}