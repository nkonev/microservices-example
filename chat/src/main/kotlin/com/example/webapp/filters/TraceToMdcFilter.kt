package com.example.webapp.filters

import org.slf4j.MDC
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter(servletNames=["*"])
class TraceToMdcFilter : HttpFilter() {
    override fun doFilter(p0: HttpServletRequest?, p1: HttpServletResponse?, p2: FilterChain?) {
        val trace = p0?.getHeader("X-B3-Traceid")

        val traceId = "traceId"
        MDC.put(traceId, trace)

        try {
            p2?.doFilter(p0, p1)
        } finally {
            MDC.remove(traceId)
        }
    }

}