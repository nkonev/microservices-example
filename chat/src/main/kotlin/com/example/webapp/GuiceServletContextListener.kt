package com.example.webapp

import brave.Tracing
import brave.grpc.GrpcTracing
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
import org.slf4j.LoggerFactory
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import zipkin2.reporter.AsyncReporter
import zipkin2.reporter.Sender
import javax.servlet.DispatcherType
import java.util.EnumSet
import brave.servlet.TracingFilter
import zipkin2.reporter.okhttp3.OkHttpSender

@WebListener
class GuiceServletContextListener : ServletContextListener {

    override fun contextInitialized(servletContextEvent: ServletContextEvent?) {
        servletContextEvent
                ?.getServletContext()
                ?.addFilter("tracingFilter", TracingFilter.create(tracing))
                ?.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
        LOGGER.info("Servlet context initialized")
    }

    override fun contextDestroyed(servletContextEvent: ServletContextEvent?) {
        close()
        println("Servlet context and logger destroyed")
    }

    companion object {
        val injector : Injector

        private val config: SampleConfig
        private val mongoClient : MongoClient
        private val LOGGER = LoggerFactory.getLogger(GuiceServletContextListener::class.java)
        private val tracing: Tracing
        private val grpcClient: ManagedChannel
        private val helloServiceStub: HelloServiceGrpc.HelloServiceBlockingStub

        init {
            config = buildConfigurationProvider().bind(CONFIG_PREFIX_APP, SampleConfig::class.java)
            LOGGER.info("Application: {}", config.name())
            mongoClient = MongoClients.create(config.mongodbUrl())

            val sender: Sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans")
            val spanReporter = AsyncReporter.create(sender)

            tracing = Tracing.newBuilder()
                    .localServiceName("chat")
                    .spanReporter(spanReporter)
                    .build();
            val grpcTracing = GrpcTracing.create(tracing);
            // https://codenotfound.com/grpc-java-example.html
            // https://www.baeldung.com/grpc-introduction
            grpcClient = ManagedChannelBuilder.forAddress(config.grpcHost(), config.grpcPort())
                    .intercept(grpcTracing.newClientInterceptor())
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
            try {
                tracing.close()
            } catch (e: Exception) {
                println("Error on closing tracing")
            }
        }
    }

    object guiceConfig : AbstractModule() {
        override fun configure() {
            bind(ObjectMapper::class.java).asEagerSingleton()
            bind(SampleConfig::class.java).toInstance(config)
            bind(MongoClient::class.java).toInstance(mongoClient)
            bind(ChatRepository::class.java).asEagerSingleton()
            bind(Tracing::class.java).toInstance(tracing)
            bind(ManagedChannel::class.java).toInstance(grpcClient)
            bind(HelloServiceGrpc.HelloServiceBlockingStub::class.java).toInstance(helloServiceStub)
        }
    }

    interface SampleConfig {
        fun name(): String
        fun mongodbUrl(): String
        fun grpcHost(): String
        fun grpcPort(): Int
    }

}
