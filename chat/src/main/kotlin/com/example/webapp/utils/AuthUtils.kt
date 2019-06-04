package com.example.webapp.utils

import javax.servlet.http.HttpServletRequest

private val authUsername : String = "x-auth-username"
private val authSubject : String = "x-auth-subject"
private val authRoles : String = "x-auth-roles"
private val authEmail : String = "x-auth-email"

fun getUserName(req: HttpServletRequest): String {
    return req.getHeader(authUsername) ?: throw RuntimeException("$authUsername is missing")
}

fun getUserId(req: HttpServletRequest): String {
    return req.getHeader(authSubject) ?: throw RuntimeException("$authSubject is missing")
}

fun getRoles(req: HttpServletRequest): Collection<String> {
    val s = req.getHeader(authRoles) ?: throw RuntimeException("$authRoles is missing")
    return s.split(",")
}

fun getEmail(req: HttpServletRequest): String? {
    return req.getHeader(authEmail).let { s -> if (""==s ) null else s }
}