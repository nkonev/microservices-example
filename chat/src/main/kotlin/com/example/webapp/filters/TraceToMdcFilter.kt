package com.example.webapp.filters

import org.slf4j.MDC
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val traceId = "traceId"
const val traceHeader = "X-B3-Traceid"

@WebFilter(servletNames=["*"])
class TraceToMdcFilter : HttpFilter() {
    override fun doFilter(p0: HttpServletRequest?, p1: HttpServletResponse?, p2: FilterChain?) {
        val trace = p0?.getHeader(traceHeader)

        MDC.put(traceId, trace)
        p1?.addHeader(traceId, trace)

        p2?.doFilter(p0, p1)
        MDC.remove(traceId)
        // for remove after exception - see also in ErrorServlet
    }

}