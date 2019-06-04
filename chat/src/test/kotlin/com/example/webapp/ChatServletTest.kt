package com.example.webapp

import com.example.launcher.TomcatConfig
import com.example.launcher.main
import com.example.webapp.repo.ChatRepository
import com.example.webapp.utils.CONFIG_PREFIX_APP
import com.example.webapp.utils.CONFIG_PREFIX_TOMCAT
import com.example.webapp.utils.buildConfigurationProvider
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import okhttp3.OkHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.AfterSuite
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

class ChatServletTest {

    private lateinit var client: OkHttpClient

    private val LOGGER: Logger = LoggerFactory.getLogger(ChatServletTest::class.java)

    private lateinit var mongoClient: MongoClient

    private lateinit var sampleConfig: GuiceServletContextListener.SampleConfig
    private lateinit var tomcatConfig: TomcatConfig

    private lateinit var baseUrl: String

    @BeforeSuite
    fun setup() {
        val provider = buildConfigurationProvider()
        sampleConfig = provider.bind(CONFIG_PREFIX_APP, GuiceServletContextListener.SampleConfig::class.java)
        tomcatConfig = provider.bind(CONFIG_PREFIX_TOMCAT, TomcatConfig::class.java)

        mongoClient = MongoClients.create(sampleConfig.mongodbUrl())
        mongoClient.getDatabase(ChatRepository.dbName).drop()

        client = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60 / 2, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .build()

        baseUrl = "http://127.0.0.1:${tomcatConfig.port()}/${tomcatConfig.contextPath()}"

        LOGGER.info("Will start server")
        GlobalScope.launch {
            main(emptyArray())
        }
        LOGGER.info("Server is started")

        runBlocking {     // but this expression blocks the main thread
            var htmlResponded = false
            var num = 0
            val max = 100
            do {
                try {
                    val request = Request.Builder()
                            .url("$baseUrl/index.html")
                            .build()

                    val response = client.newCall(request).execute()
                    val html = response.body()!!.string()
                    LOGGER.info("{}/{} Response: {}", num, max, html)
                    if (html.contains("Hello World!")) {
                        htmlResponded = true
                    } else {
                        TimeUnit.SECONDS.sleep(1)
                    }
                } catch (e: Exception) {
                    LOGGER.info("Exception: {}", e.message)
                    TimeUnit.SECONDS.sleep(1)
                }
            } while (!htmlResponded && num++ < max)
        }
    }

    @AfterSuite
    fun tearDown() {
        mongoClient.close()
    }

    @Test
    fun `test create chat`() {
        val request = Request.Builder()
                .url("$baseUrl/chat/one")
                .post(RequestBody.create(MediaType.parse("application/json"), """{}"""))
                .addHeader("x-auth-subject", "uuid-user-1")
                .build()

        val response = client.newCall(request).execute()
        val json = response.body()!!.string()
        LOGGER.info("Response: {}", json)
        Assert.assertEquals(200, response.code())
    }

    @Test
    fun `test create get chats`() {
        `test create chat`();

        val request = Request.Builder()
                .url("$baseUrl/chat")
                .get()
                .addHeader("x-auth-subject", "uuid-user-1")
                .build()

        val response = client.newCall(request).execute()
        val json = response.body()!!.string()
        LOGGER.info("Response: {}", json)
        Assert.assertEquals(200, response.code())
        Assert.assertTrue(json.contains("one"))
    }

}