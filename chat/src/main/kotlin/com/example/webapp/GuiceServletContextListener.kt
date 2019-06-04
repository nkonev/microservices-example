package com.example.webapp

import com.codenotfound.grpc.helloworld.HelloServiceGrpc
import com.example.webapp.repo.ChatRepository
import com.example.webapp.utils.CONFIG_PREFIX_APP
import com.example.webapp.utils.buildConfigurationProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.classpath.ClasspathConfigurationSource
import org.cfg4j.source.compose.MergeConfigurationSource
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

@WebListener
class GuiceServletContextListener : ServletContextListener {

    override fun contextInitialized(sce: ServletContextEvent?) {
        LOGGER.info("Servlet context initialized")
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        close()
        println("Servlet context and logger destroyed")
    }

    companion object {
        val injector : Injector

        private val config: SampleConfig
        private val mongoClient : MongoClient
        private val LOGGER = LoggerFactory.getLogger(GuiceServletContextListener::class.java)
        private val grpcClient: ManagedChannel
        private val helloServiceStub: HelloServiceGrpc.HelloServiceBlockingStub

        init {
            config = buildConfigurationProvider().bind(CONFIG_PREFIX_APP, SampleConfig::class.java)
            mongoClient = MongoClients.create(config.mongodbUrl())

            // https://codenotfound.com/grpc-java-example.html
            // https://www.baeldung.com/grpc-introduction
            grpcClient = ManagedChannelBuilder.forAddress(config.grpcHost(), config.grpcPort())
                    .usePlaintext()
                    .build()
            helloServiceStub = HelloServiceGrpc.newBlockingStub(grpcClient);

            injector = Guice.createInjector(guiceConfig)
        }

        fun close() {
            println("Closing resources")
            try {
                mongoClient.close()
            } catch (e: Exception) {
                println("Error on closing mongo client")
            }
            try {
                grpcClient.shutdownNow()
            } catch (e: Exception) {
                println("Error on closing grpc client")
            }
        }
    }

    object guiceConfig : AbstractModule() {
        override fun configure() {
            bind(ObjectMapper::class.java).asEagerSingleton()
            bind(SampleConfig::class.java).toInstance(config)
            bind(MongoClient::class.java).toInstance(mongoClient)
            bind(ChatRepository::class.java).asEagerSingleton()
            bind(ManagedChannel::class.java).toInstance(grpcClient)
            bind(HelloServiceGrpc.HelloServiceBlockingStub::class.java).toInstance(helloServiceStub)
        }
    }

    interface SampleConfig {
        fun mongodbUrl(): String
        fun grpcHost(): String
        fun grpcPort(): Int
    }

}
