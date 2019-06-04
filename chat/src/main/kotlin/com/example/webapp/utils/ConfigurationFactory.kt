package com.example.webapp.utils

import org.cfg4j.provider.ConfigurationProvider
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.classpath.ClasspathConfigurationSource
import org.cfg4j.source.compose.MergeConfigurationSource
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource

const val CONFIG_PREFIX_TOMCAT = "tomcat"
const val CONFIG_PREFIX_APP = "app"

fun buildConfigurationProvider(): ConfigurationProvider {
    val applicationPropertiesSource = ClasspathConfigurationSource()
    val source = EnvironmentVariablesConfigurationSource()
    val mergeConfigurationSource = MergeConfigurationSource(applicationPropertiesSource, source)
    val provider = ConfigurationProviderBuilder()
            .withConfigurationSource(mergeConfigurationSource)
            .build()
    return provider
}