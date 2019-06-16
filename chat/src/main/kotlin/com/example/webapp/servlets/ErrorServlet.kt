package com.example.webapp.servlets

import com.example.webapp.filters.traceHeader
import com.example.webapp.filters.traceId
import org.slf4j.MDC
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/error")
class ErrorServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doGet(request: HttpServletRequest,
                       response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(request: HttpServletRequest,
                        response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPut(request: HttpServletRequest,
                        response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doDelete(request: HttpServletRequest,
                       response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doHead(request: HttpServletRequest,
                          response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doOptions(request: HttpServletRequest,
                        response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doTrace(request: HttpServletRequest,
                           response: HttpServletResponse) {
        processError(request, response)
    }

    @Throws(IOException::class)
    private fun processError(request: HttpServletRequest,
                             response: HttpServletResponse) {
        val traceId = MDC.get(traceId)
        MDC.remove(traceId)

        // Analyze the servlet exception
        val throwable = request
                .getAttribute("javax.servlet.error.exception") as Throwable
        val statusCode = request
                .getAttribute("javax.servlet.error.status_code") as Int
        var servletName: String? = request
                .getAttribute("javax.servlet.error.servlet_name") as String
        if (servletName == null) {
            servletName = "Unknown"
        }
        var requestUri: String? = request
                .getAttribute("javax.servlet.error.request_uri") as String
        if (requestUri == null) {
            requestUri = "Unknown"
        }

        // Set response content type
        response.contentType = "text/html"

        val out = response.writer
        out.write("<html><head><title>Exception/Error Details</title></head><body>")
            out.write("<h3>Error Details</h3>")
            out.write("<strong>Status Code</strong>:$statusCode<br>")
            out.write("<ul><li>Servlet Name:$servletName</li>")
            out.write("<li>Exception Name:" + throwable.javaClass.name + "</li>")
            out.write("<li>Requested URI:$requestUri</li>")
            out.write("<li>Exception Message:" + throwable.message + "</li>")
            out.write("</ul>")
        out.write("<strong>traceId</strong>:$traceId")
        out.write("<br><br>")
        out.write("<a href=\"index.html\">Home Page</a>")
        out.write("</body></html>")
    }


}