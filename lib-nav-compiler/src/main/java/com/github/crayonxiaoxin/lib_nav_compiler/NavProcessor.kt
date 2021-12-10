package com.github.crayonxiaoxin.lib_nav_compiler

import com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.math.abs

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(
    "com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination",
    "com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination"
)
class NavProcessor : AbstractProcessor() {

    private val OUTPUT_FILE_NAME = "destination.json"
    private lateinit var filer: Filer
    private lateinit var messager: Messager

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.let { env ->
            val activityElements =
                env.getElementsAnnotatedWith(ActivityDestination::class.java)
            val fragmentElements =
                env.getElementsAnnotatedWith(FragmentDestination::class.java)
            if (activityElements.isNotEmpty() || fragmentElements.isNotEmpty()) {
                val destMap: HashMap<String, JsonObject> = HashMap()
                handleDestination(activityElements, ActivityDestination::class.java, destMap)
                handleDestination(fragmentElements, FragmentDestination::class.java, destMap)

                var fileOutputStream: FileOutputStream? = null
                var outputStreamWriter: OutputStreamWriter? = null
                // app/src/main/assets
                try {
                    // 获取 CLASS_OUTPUT 路径
                    val createResource =
                        filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME)
                    val path = createResource.toUri().path
                    // 获取 app/ 路径
                    val appPath = path.substring(0, path.indexOf("app") + 4)
                    // app/src/main/assets
                    val assetsPath = appPath + "src/main/assets"
                    val file = File(assetsPath)
                    if (!file.exists()) file.mkdir()

                    val outputFile = File(file, OUTPUT_FILE_NAME)
                    if (outputFile.exists()) outputFile.delete()
                    outputFile.createNewFile()

                    val toJson = Gson().toJson(destMap)
                    fileOutputStream = FileOutputStream(outputFile)
                    outputStreamWriter =
                        OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
                    outputStreamWriter.write(toJson)
                    outputStreamWriter.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    outputStreamWriter?.close()
                    fileOutputStream?.close()
                }
            }
        }
        return true
    }

    private fun handleDestination(
        elements: Set<Element>,
        clazz: Class<out Annotation>,
        destMap: HashMap<String, JsonObject>
    ) {
        elements.forEach {
            val typeElement = it as TypeElement
            val clazzName = typeElement.qualifiedName.toString()
            val id = abs(clazzName.hashCode())
            var pageUrl = ""
            var needLogin = false
            var asStarter = false
            var isFragment = false
            val annotation = typeElement.getAnnotation(clazz)
            if (annotation is ActivityDestination) {
                pageUrl = annotation.pageUrl
                needLogin = annotation.needLogin
                asStarter = annotation.asStarter
                isFragment = false
            } else if (annotation is FragmentDestination) {
                pageUrl = annotation.pageUrl
                needLogin = annotation.needLogin
                asStarter = annotation.asStarter
                isFragment = true
            }
            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同页面请不要使用相同的 pageUrl：$pageUrl", it)
            } else {
                val jsonObject = JsonObject()
                jsonObject.addProperty("id", id)
                jsonObject.addProperty("pageUrl", pageUrl)
                jsonObject.addProperty("needLogin", needLogin)
                jsonObject.addProperty("asStarter", asStarter)
                jsonObject.addProperty("isFragment", isFragment)
                jsonObject.addProperty("clazzName", clazzName)
                destMap[pageUrl] = jsonObject
            }
        }
    }

}