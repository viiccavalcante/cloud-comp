package com.harbourspace.client.shifts.repositories

import com.harbourspace.client.ShardsRouter
import com.harbourspace.client.shifts.models.Shift
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.harbourspace.client.factories.ShardedRepositoryFactory

@Component
class ShardedShiftRepository(private val router: ShardsRouter, val shardedRepositoryFactory: ShardedRepositoryFactory) {

    fun saveAll(shifts: List<Shift>): Flux<Void> {
        return Flux.fromIterable(shifts)
            .flatMap { shift ->
                val connectionFactory = router.getShardForCompany(shift.companyId)
                Mono.from(connectionFactory.create())
                    .flatMap { conn ->
                        Mono.from(
                            conn.createStatement(
                                """
                                INSERT INTO shifts (
                                    request_id, company_id, user_id, start_time, end_time, activity
                                ) VALUES ($1, $2, $3, $4, $5, $6)
                                """.trimIndent()
                            )
                                .bind(0, shift.requestId)
                                .bind(1, shift.companyId)
                                .bind(2, shift.userId)
                                .bind(3, shift.startTime)
                                .bind(4, shift.endTime)
                                .bind(5, shift.activity!!)
                                .execute()
                        )
                            .flatMap { result -> Mono.from(result.getRowsUpdated()) }
                            .then()
                            .doFinally { conn.close() }
                    }
            }
    }

    fun findAllByRequestId(requestId: Int): Flux<Shift> {
        return Flux.fromIterable(shardedRepositoryFactory.shiftRepositories)
            .flatMap { repo -> repo.findAllByRequestId(requestId) }
    }

}
