package com.example.launcher

import com.example.webapp.utils.CONFIG_PREFIX_TOMCAT
import com.example.webapp.utils.buildConfigurationProvider
import org.apache.catalina.LifecycleListener
import org.apache.catalina.Service
import org.apache.catalina.WebResourceSet
import org.apache.catalina.connector.Connector
import org.apache.catalina.core.StandardContext
import org.apache.catalina.core.StandardServer
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.DirResourceSet
import org.apache.catalina.webresources.JarResourceSet
import org.apache.catalina.webresources.StandardRoot
import org.apache.tomcat.util.scan.StandardJarScanFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import java.io.File

class MainResourceHolder

interface TomcatConfig {
    fun port(): Int
    fun staticdir(): String
    fun tmpdir(): String
    fun contextPath(): String
}

fun main(args: Array<String>) {
    val provider = buildConfigurationProvider()
    val config: TomcatConfig = provider.bind(CONFIG_PREFIX_TOMCAT, TomcatConfig::class.java)

    val logger: Logger = LoggerFactory.getLogger(::main.name)

    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

    val tomcat = Tomcat()
    tomcat.setBaseDir(config.tmpdir())
    tomcat.setPort(config.port())

    val connector = Connector("org.apache.coyote.http11.Http11Nio2Protocol")
    connector.port = config.port()
    tomcat.connector = connector
    // prevent register jsp servlet
    tomcat.setAddDefaultWebXmlToWebapp(false)

    val contextPath = config.contextPath() // "" (empty string) root context
    File(config.staticdir()).mkdirs()
    val docBase = File(config.staticdir()).canonicalPath
    val rootContext = tomcat.addWebapp(contextPath, docBase)
    rootContext.addWebinfClassesResources = true // process /META-INF/resources for static

    val jarScanFilter : StandardJarScanFilter = StandardJarScanFilter()
    jarScanFilter.isDefaultPluggabilityScan = false
    // for jar launch variant scan for annotations only in itself
    jarScanFilter.pluggabilityScan = File(System.getProperty("java.class.path")).name
    jarScanFilter.isDefaultTldScan = false
    rootContext.jarScanner.jarScanFilter = jarScanFilter

    // fix Illegal reflective access by org.apache.catalina.loader.WebappClassLoaderBase
    // https://github.com/spring-projects/spring-boot/issues/15101#issuecomment-437384942
    val standardContext = rootContext as StandardContext
    standardContext.clearReferencesObjectStreamClassCaches = false
    standardContext.clearReferencesRmiTargets = false
    standardContext.clearReferencesThreadLocals = false

    var stopInvoked = false
    tomcat.server.addLifecycleListener(LifecycleListener {
        logger.debug("Server listener event: type={}, lifecycle={}", it.type, it.lifecycle)
        if (it.source is StandardServer && !stopInvoked) {
            val server: StandardServer = it.source as StandardServer
            val services = server.findServices()
            services.forEach {
                val service: Service = it
                val failed = service.findConnectors()?.filter { connector -> connector.stateName == "FAILED" }.orEmpty()
                if (!failed.isEmpty()) {
                    stopInvoked = true
                    logger.warn("For Service `{}` found FAILED connectors: `{}`, shutting down", service, failed)
                    System.exit(1)
                }
            }
        }
    })

    // Additions to make serving static work
    val defaultServletName = "default"
    val defaultServlet = rootContext.createWrapper()
    defaultServlet.name = defaultServletName
    defaultServlet.servletClass = "org.apache.catalina.servlets.DefaultServlet"
    defaultServlet.addInitParameter("debug", "0")
    defaultServlet.addInitParameter("listings", "false")
    defaultServlet.loadOnStartup = 1
    rootContext.addChild(defaultServlet)
    rootContext.addServletMappingDecoded("/", defaultServletName)
    rootContext.addWelcomeFile("index.html")


    // add self jar with static and annotated servlets
    val webResourceRoot = StandardRoot(rootContext)
    val webAppMount = "/WEB-INF/classes"
    val webResourceSet: WebResourceSet
    if (!isJar()) {
        webResourceSet = DirResourceSet(webResourceRoot, webAppMount, resourceFromFs(), "/")
    } else {
        webResourceSet = JarResourceSet(webResourceRoot, webAppMount, resourceFromJarFile(), "/")
    }
    webResourceRoot.addJarResources(webResourceSet)
    if (isMavenTest()){
        val dirResourceSet = DirResourceSet(webResourceRoot, webAppMount, mavenMainClasses(), "/")
        webResourceRoot.addPreResources(dirResourceSet)
    }
    rootContext.resources = webResourceRoot

    Runtime.getRuntime().addShutdownHook(Thread() {
        if (!stopInvoked) {
            tomcat.server.stop()
        }
    })

    tomcat.start()

    tomcat.server.await()
}

fun mavenMainClasses() : String {
    return File(File(resourceFromFs()).parent, "classes").absolutePath
}

fun isMavenTest(): Boolean {
    return File(MainResourceHolder::javaClass::class.java.getResource("/").file).name == "test-classes"
}

fun isJar(): Boolean  {
        val resource = MainResourceHolder::javaClass::class.java.getResource("/")
        return resource == null
}

fun resourceFromJarFile(): String {
        val jarFile = File(System.getProperty("java.class.path"))
        return jarFile.absolutePath
}

fun resourceFromFs(): String {
        val resource = MainResourceHolder::javaClass::class.java.getResource("/")
        return resource.file
}
