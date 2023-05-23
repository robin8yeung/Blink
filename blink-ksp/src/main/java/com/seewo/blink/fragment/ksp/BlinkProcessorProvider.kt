package com.seewo.blink.fragment.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class BlinkProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): BlinkUriProcessor {
        environment.logger.warn("creating processor")
        return BlinkUriProcessor(environment)
    }
}