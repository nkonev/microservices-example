package com.example.webapp.utils

import javax.servlet.http.HttpServletRequest

fun getUrlPart(req: HttpServletRequest, i: Int): String {
    val pathInfo = req.pathInfo
    val pathParts = pathInfo.split("/")

    val part1 = pathParts[i]
    return part1
}