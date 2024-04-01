package com.mgws.datastore.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.mgws.datastore.annotations.HiltDataStore
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import javax.annotation.processing.Generated

class HiltDataStoreProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor {
        return HiltDataStoreProcessor(environment.codeGenerator, environment.logger)
    }
}

class HiltDataStoreProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private val generatePackageName = "com.mgws.hilt.datastore"

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols =
            resolver.getSymbolsWithAnnotation(HiltDataStore::class.asClassName().canonicalName)

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(SerializerVisitor(), Unit) }

        return symbols.filter { !it.validate() }.toList()
    }

    inner class SerializerVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {

            //检查是否实现序列化
            val serializableAnnotation = classDeclaration.annotations.firstOrNull {
                it.annotationType.toTypeName() == ClassNames.SERIALIZABLE
            }

            if (serializableAnnotation == null) {
                error("must be have ${ClassNames.SERIALIZABLE.canonicalName} annotation")
            }

            val datastoreFile = classDeclaration.annotations.first {
                it.annotationType.toTypeName() == ClassNames.HILT_DATA_STORE
            }.arguments.first().value.toString()

            val className = classDeclaration.toClassName()
            val generateName = "${classDeclaration.simpleName.asString()}Serializer"
            val serializerTypeSpecBuilder = TypeSpec
                .objectBuilder(generateName)
                .addAnnotation(
                    AnnotationSpec.builder(Generated::class)
                        .addMember(
                            "value = arrayOf(%S)",
                            this::class.asClassName().canonicalName
                        )
                        .build()
                )
                .addAnnotation(
                    AnnotationSpec.builder(ClassNames.ORIGINATING_ELEMENT)
                        .addMember(
                            "topLevelClass = %T::class",
                            className
                        )
                        .build()
                )
                .addAnnotation(
                    AnnotationSpec.builder(ClassNames.OPT_IN)
                        .addMember("%T::class", ClassNames.EXPERIMENTAL_SERIALIZATION_API)
                        .build()
                )
                .addSuperinterface(ClassNames.SERIALIZER.parameterizedBy(className))
                .addProperty(
                    PropertySpec.builder("defaultValue", className)
                        .addModifiers(KModifier.OVERRIDE)
                        .mutable(false)
                        .initializer("%T()", className)
                        .build()
                ).addFunction(
                    FunSpec.builder("readFrom")
                        .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                        .addParameter("input", ClassNames.INPUT_STREAM)
                        .addStatement(
                            "return %T.%T(input.readBytes())",
                            ClassNames.PROTOBUF,
                            ClassNames.DECODE_FROM_BYTE_ARRAY
                        )
                        .returns(className)
                        .build()
                ).addFunction(
                    FunSpec.builder("writeTo")
                        .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
                        .addParameter("t", className)
                        .addParameter("output", ClassNames.OUTPUT_STREAM)
                        .beginControlFlow(
                            "%T(%T.IO)",
                            ClassNames.WITH_CONTEXT,
                            ClassNames.DISPATCHERS
                        )
                        .addStatement(
                            "output.write(%T.%T(t))",
                            ClassNames.PROTOBUF,
                            ClassNames.ENCODE_TO_BYTE_ARRAY
                        )
                        .endControlFlow()
                        .build()
                )

            FileSpec.builder(className.packageName, generateName)
                .addType(serializerTypeSpecBuilder.build())
                .build().writeTo(
                    codeGenerator, false,
                    listOf(classDeclaration.containingFile!!)
                )

            val (serialize, serializer, dataStoreFile) = Triple(
                className,
                ClassName(className.packageName, generateName),
                datastoreFile
            )
            logger.info("$serializer")
            val returnTypeName = ClassNames.DATASTORE.parameterizedBy(serialize)
            val parameterType = ParameterSpec.builder("context", ClassNames.CONTEXT)
                .addAnnotation(ClassNames.APPLICATION_CONTEXT).build()

            val moduleClassName = ClassName(
                generatePackageName,
                "${serialize.simpleName}_DataStore_HiltModule"
            )

            val hiltModuleTypeSpecBuilder = TypeSpec.classBuilder(moduleClassName)
                // @Generated
                .addAnnotation(
                    AnnotationSpec.builder(Generated::class)
                        .addMember(
                            "value = arrayOf(%S)",
                            this::class.asClassName().canonicalName
                        )
                        .build()
                )
                // @OriginatingElement
                .addAnnotation(
                    AnnotationSpec.builder(ClassNames.ORIGINATING_ELEMENT)
                        .addMember(
                            "topLevelClass = %T::class",
                            serialize
                        )
                        .build()
                )
                // @Module
                .addAnnotation(ClassNames.MODULE)
                // @InstallIn
                .addAnnotation(
                    AnnotationSpec.builder(ClassNames.INSTALL_IN)
                        .addMember("value = arrayOf(%T::class)", ClassNames.SINGLETON_COMPONENT)
                        .build()
                ).addFunction(
                    FunSpec.builder("provideDataStore${serialize.simpleName}")
                        .addAnnotation(ClassNames.PROVIDES)
                        .addAnnotation(ClassNames.SINGLETON)
                        .addParameter(parameterType)
                        .addStatement(
                            "return %T.create(%T) { context.%T(%S) }",
                            ClassNames.DATASTORE_FACTORY,
                            serializer,
                            ClassNames.DATASTORE_FILE,
                            dataStoreFile
                        )
                        .returns(returnTypeName)
                        .build()
                )

            FileSpec.builder(moduleClassName.packageName, moduleClassName.simpleName)
                .addType(hiltModuleTypeSpecBuilder.build())
                .build().writeTo(
                    codeGenerator, false,
                    listOf(classDeclaration.containingFile!!)
                )

        }

    }

}