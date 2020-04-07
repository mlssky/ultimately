package com.xcleans.processor

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * Created by mengliwei on 2020/4/2.
 */

class ModuleProviderProcessor {

    /**
     *
     * @param annotations Set<TypeElement?>?  the annotation types requested to be processed
     * @param roundEnv RoundEnvironment? environment for information about the current and prior round
     * @return Boolean
     */
    fun process(
        annotations: Set<TypeElement?>?, roundEnv: RoundEnvironment?
    ): Boolean {
        return true;
    }

}