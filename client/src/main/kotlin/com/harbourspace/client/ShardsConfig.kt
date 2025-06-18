package com.harbourspace.client

import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.r2dbc.spi.ConnectionFactories
import org.springframework.beans.factory.annotation.Qualifier

@Configuration
class ShardsConfig(
    @Value("\${shard.number}") val shardNumber: Int,
    @Value("\${spring.r2dbc.username}") val username: String,
    @Value("\${spring.r2dbc.password}") val password: String,
    @Value("\${spring.r2dbc.url}") val url: String,
) {

    @Bean
    @Qualifier("shardConnectionFactories")
    fun connectionFactories(): List<ConnectionFactory> {
        return (0 until shardNumber).map { shard ->
            val url = "${url}_shard${shard}"

            ConnectionFactories.get(
                ConnectionFactoryOptions.parse(url)
                    .mutate()
                    .option(ConnectionFactoryOptions.USER, username)
                    .option(ConnectionFactoryOptions.PASSWORD, password)
                    .build()
            )
        }
    }
}
