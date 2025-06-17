package com.harbourspace.client.shifts.repositories

import com.harbourspace.client.shifts.models.Shift
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ShiftRepository : ReactiveCrudRepository<Shift, Int> {
    fun findAllByRequestId(requestId: Int): Flux<Shift>
}

