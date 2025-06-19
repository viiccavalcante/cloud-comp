package com.harbourspace.client

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class ShardsRouter(
    @Qualifier("shardConnectionFactories")
    private val factories: List<ConnectionFactory>
) {
    fun getShardForCompany(companyId: String): ConnectionFactory {
        val shardIndex = (companyId.hashCode() and Int.MAX_VALUE) % factories.size
        return factories[shardIndex]
    }

    fun getAllFactories(): List<ConnectionFactory> = factories
}

