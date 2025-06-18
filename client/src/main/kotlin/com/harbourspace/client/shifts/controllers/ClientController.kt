package com.harbourspace.client.shifts.controllers

import com.harbourspace.client.services.*
import com.harbourspace.client.shifts.models.*
import com.harbourspace.client.shifts.repositories.*
import com.harbourspace.client.dtos.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration


@RestController
class ClientController(
    val httpClient: WebClient, val shiftRequestRepository: ShiftRequestRepository,
    val persistShiftsService: PersistShiftsService,
    val shardedShiftRepository: ShardedShiftRepository
) {

    @GetMapping("/clientshifts")
    fun getShifts(): ClientShiftsVm? {
        return httpClient.get()
            .uri("/shifts")
            .retrieve()
            .bodyToMono(ClientShiftsVm::class.java)
            .timeout(Duration.ofMillis(300))
            .retryWhen(
                Retry.backoff(3, Duration.ofMillis(100))
                    .maxBackoff(Duration.ofMillis(1000))
            )
            .block()
    }

    @PostMapping("/clientshifts")
    fun modifyShifts(@RequestBody shiftsVm: ClientShiftsVm): String {
        val batchSize = 5

        shiftsVm.shifts.chunked(batchSize).forEach { batch ->
            Flux.fromIterable(batch)
                .flatMap({ shift -> persistShiftsService.persistShift(shift) }, 5)
                .collectList()
                .block()
        }

        return "{status: 'ok'}"
    }

    @GetMapping("/clientshifts/request/{id}")
    fun getShiftRequestStatus(@PathVariable id: Int): Mono<ResponseEntity<Map<String, String>>> {
        return shiftRequestRepository.findById(id).map { request ->

            ResponseEntity.ok(mapOf("status" to request.status))

        }.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
    }

    @PostMapping("/clientshiftsV2")
    fun submitShifts(@RequestBody shiftsVm: ClientShiftsVm): Mono<Map<String, Any>> {
        val shiftRequest = ShiftRequest(status = "pending")

        return shiftRequestRepository.save(shiftRequest).flatMap { savedRequest ->
            val shifts = shiftsVm.shifts.map { shiftVm ->
                Shift(
                    requestId = savedRequest.id!!,
                    userId = shiftVm.userId,
                    companyId = shiftVm.companyId,
                    startTime = shiftVm.startTime,
                    endTime = shiftVm.endTime,
                    activity = shiftVm.action,
                )
            }

            shardedShiftRepository.saveAll(shifts).collectList().doOnNext {
                persistShiftsService.persistShiftsAsync(savedRequest.id!!)
            }.thenReturn(mapOf("requestId" to savedRequest.id!!))
        }
    }


}