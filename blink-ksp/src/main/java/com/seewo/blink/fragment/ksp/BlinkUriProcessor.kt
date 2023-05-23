package com.seewo.blink.fragment.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.seewo.blink.annotation.BlinkMetadata
import com.seewo.blink.annotation.BlinkUri
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

class BlinkUriProcessor(
    private val processingEnv: SymbolProcessorEnvironment
) : SymbolProcessor {
    private var round = 0

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (round != 0) return emptyList()

        val metadataSymbols = resolver.getSymbolsWithAnnotation(BlinkMetadata::class.qualifiedName!!)
        val metadataElements = metadataSymbols.filterIsInstance<KSClassDeclaration>()
        val metadataList = mutableListOf<BlinkMetadataEntry>()
        metadataElements.forEach {
            val packageName = it.packageName.asString()
            val type = it.asType(emptyList())
            metadataList.add(BlinkMetadataEntry(packageName, type.toString()))
        }
        processingEnv.logger.warn("metadataList = $metadataList")

        val symbols = resolver.getSymbolsWithAnnotation(BlinkUri::class.qualifiedName!!)
        val elements = symbols.filterIsInstance<KSClassDeclaration>()
        val entries = mutableListOf<BlinkUriEntry>()
        elements.forEach {
            val packageName = it.packageName.asString()
            val type = it.asType(emptyList())

            processingEnv.logger.warn("asType >> $packageName.$type")
            it.annotations.forEach { ks ->
                processingEnv.logger.warn("forEach >> $ks")
                if (ks.shortName.asString() == BlinkUri::class.java.simpleName) {
                    ks.arguments.forEach { arg ->
                        if (arg.name?.asString() == "value") {
                            val uri = arg.value as List<String>
                            entries.add(
                                BlinkUriEntry(
                                    packageName, "$type", uri
                                )
                            )
                        }
                    }
                }
            }
        }


        metadataList.firstOrNull()?.let {
            val metadataClassName = "${it.packageName}.${it.className}"
            val meatadata = ClassName(it.packageName, it.className)
            val fileSpec = FileSpec.builder("com.seewo.blink", "Initializer\$${metadataClassName.replace('.', '$')}")
                .addFunction(FunSpec.builder("_inject")
                    .receiver(meatadata)
                    .apply {
                        entries.forEach {
                            it.uris.forEach { uri ->
                                addStatement("com.seewo.blink.fragment.RouteMetadata.register(%S, %T::class.java)", uri, ClassName(it.packageName, it.className))
                            }
                        }
                    }
                    .build())
                .build()

            processingEnv.logger.warn("processingEnv.codeGenerator.generatedFile = ${processingEnv.codeGenerator.generatedFile}")
            processingEnv.codeGenerator.createNewFile(Dependencies.ALL_FILES, fileSpec.packageName, fileSpec.name).use {
                it.write(fileSpec.toString().toByteArray())
            }
        }

        round++
        return listOf()
    }
}