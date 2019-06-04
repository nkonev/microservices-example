package com.example.webapp.servlets

import com.example.webapp.GuiceServletContextListener
import javax.servlet.http.HttpServlet

abstract class AbstractInjectableServlet : HttpServlet() {

    abstract val logger: org.slf4j.Logger

    override fun init() {
        logger.info("Initializing {}", this)
        GuiceServletContextListener.injector.injectMembers(this)
    }

    override fun destroy() {
        logger.info("Destroying {}", this)
    }

}
