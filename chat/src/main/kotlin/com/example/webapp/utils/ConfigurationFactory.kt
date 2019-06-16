package com.example.webapp.utils

import org.cfg4j.provider.ConfigurationProvider
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.ConfigurationSource
import org.cfg4j.source.classpath.ClasspathConfigurationSource
import org.cfg4j.source.compose.MergeConfigurationSource
import org.cfg4j.source.files.FilesConfigurationSource
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource
import org.cfg4j.source.context.environment.ImmutableEnvironment
import org.slf4j.LoggerFactory

const val CONFIG_PREFIX_TOMCAT = "tomcat"
const val CONFIG_PREFIX_APP = "app"

val logger = LoggerFactory.getLogger("configurer")
const val configDir = "configDir"

fun buildConfigurationProvider(): ConfigurationProvider {
    val propertyVal = System.getProperty(configDir)
    if (propertyVal!=null&&propertyVal!=""){
        return buildConfigurationProvider(propertyVal)
    } else {
        return buildConfigurationProvider(null)
    }
}

fun buildConfigurationProvider(confDir: String?): ConfigurationProvider {
    if (confDir != null) {
        logger.info("System property {} is set, will read config from this directory, environment variables will be ignored", configDir)
        val environment = ImmutableEnvironment(confDir)
        val fileSource: ConfigurationSource = FilesConfigurationSource()
        return ConfigurationProviderBuilder().withConfigurationSource(fileSource)
                .withEnvironment(environment)
                .build()
    }

    val classpathSource = ClasspathConfigurationSource()
    val environmentSource = EnvironmentVariablesConfigurationSource()
    val mergeConfigurationSource = MergeConfigurationSource(classpathSource, environmentSource)
    return ConfigurationProviderBuilder().withConfigurationSource(mergeConfigurationSource).build()
}