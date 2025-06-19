package com.harbourspace.client.factories

import com.harbourspace.client.shifts.repositories.ShiftRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.stereotype.Component
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier

@Component
class ShardedRepositoryFactory(
    @Qualifier("shardConnectionFactories")
    val factories: List<ConnectionFactory>
) {
    val shiftRepositories: List<ShiftRepository> = factories.map { connectionFactory ->
        val template = R2dbcEntityTemplate(connectionFactory)
        val factory = R2dbcRepositoryFactory(template)
        factory.getRepository(ShiftRepository::class.java)
    }
}
