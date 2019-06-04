package com.example.webapp.servlets;

import com.example.webapp.dto.EditChatDto
import com.example.webapp.repo.ChatRepository
import com.example.webapp.utils.getUrlPart
import com.example.webapp.utils.getUserId
import com.example.webapp.utils.setJson
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Inject
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(urlPatterns = ["/chat", "/chat/*"], loadOnStartup = 1)
class ChatServlet: AbstractInjectableServlet() {
    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Inject
    private lateinit var chatRepository: ChatRepository

    private var LOGGER: Logger = LoggerFactory.getLogger(ProfileServlet::class.java)

    override val logger: Logger
        get() = LOGGER

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        setJson(resp)

        val userId = getUserId(req)
        val chatName: String = getUrlPart(req, 1)

        chatRepository.insertChat(chatName, userId)
    }


    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        setJson(resp)

        objectMapper.writeValue(resp.writer, chatRepository.getChats())
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        setJson(resp)

        val userId = getUserId(req)
        val chatName: EditChatDto = objectMapper.readValue(req.reader, EditChatDto::class.java)

        chatRepository.editChat(chatName, userId)
    }

}