package com.example.webapp.utils

import javax.servlet.http.HttpServletResponse

fun setJson(resp: HttpServletResponse){
    resp.addHeader("Content-Type", "application/json")
}