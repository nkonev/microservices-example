package com.example.webapp.servlets

import com.codenotfound.grpc.helloworld.HelloRequest
import com.codenotfound.grpc.helloworld.HelloServiceGrpc
import com.example.webapp.dto.Dto
import com.example.webapp.utils.getUserId
import com.example.webapp.utils.getUserName
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


@WebServlet(urlPatterns = ["/profile"], loadOnStartup = 1)
class ProfileServlet : AbstractInjectableServlet() {

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Inject
    private lateinit var helloServiceStub: HelloServiceGrpc.HelloServiceBlockingStub

    private var LOGGER: Logger  = LoggerFactory.getLogger(ProfileServlet::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        setJson(resp)
        val writer = resp.writer

        val helloRequest = HelloRequest.newBuilder().setLastName("Konev").setFirstName("Nikita").build()
        val helloResponse = helloServiceStub.hello(helloRequest)
        LOGGER.info("Returning profile")
        objectMapper.writeValue(writer, Dto(getUserName(req), getUserId(req), helloResponse.greeting))
    }

    override val logger: Logger
        get() = LOGGER

}
