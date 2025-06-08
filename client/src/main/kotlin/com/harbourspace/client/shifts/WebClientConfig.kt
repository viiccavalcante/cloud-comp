package com.harbourspace.client.shifts

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun externalApiClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl("http://localhost:8080")
            .build()
}
