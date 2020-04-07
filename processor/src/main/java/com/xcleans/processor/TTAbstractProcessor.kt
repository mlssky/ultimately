package com.xcleans.processor

import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableSet
import com.squareup.kotlinpoet.ClassName
import com.xcleans.anno.ModuleProvider
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Created by mengliwei on 2020/4/1.
 */
@AutoService(AbstractProcessor::class)
class TTAbstractProcessor : AbstractProcessor() {

    var mMessager: Messager? = null
    var mFiler: Filer? = null
    var mElementsUtils: Elements? = null
    var mElements: Elements? = null
    private var mTypeUtils: Types? = null

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        mMessager = p0?.messager
        mMessager?.printMessage(Diagnostic.Kind.NOTE, "========start process")
        mFiler = p0?.filer
        mElementsUtils = p0?.elementUtils
        mTypeUtils = p0?.typeUtils
    }

    /**
     *
     * @param p0 MutableSet<out TypeElement>
     * @param p1 RoundEnvironment
     * @return Boolean
     */
    override fun process(
        annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?
    ): Boolean {

        val bindViewElements = roundEnvironment?.getElementsAnnotatedWith(
            ModuleProvider::class.java
        )
        bindViewElements?.forEach {
            val pkgname = mElementsUtils?.getPackageOf(it)?.qualifiedName
            mMessager?.printMessage(Diagnostic.Kind.NOTE, "$pkgname")
        }

        mMessager?.printMessage(Diagnostic.Kind.ERROR, "========start process")
        mMessager?.printMessage(Diagnostic.Kind.WARNING, "========start process")
        mMessager?.printMessage(Diagnostic.Kind.ERROR, "========start process")
        mMessager?.printMessage(Diagnostic.Kind.ERROR, "========start process")
        mMessager?.printMessage(Diagnostic.Kind.ERROR, "========start process")
        mMessager?.printMessage(Diagnostic.Kind.ERROR, "========start process")

        annotations?.forEach {
            it.superclass
        }


        val clzName = ClassName("", "TestOpt")

        return true
    }

    /**
     * 处理的注解类型
     * @return ImmutableSet<String?>?
     */
    override fun getSupportedAnnotationTypes(): ImmutableSet<String?>? {
        return ImmutableSet.of(ModuleProvider::class.java.canonicalName)
    }

    /**
     * 支持的注解版本
     * @return SourceVersion?
     */
    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }


}