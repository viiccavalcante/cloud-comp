package com.harbourspace.client.services

import com.harbourspace.client.shifts.models.Shift
import com.harbourspace.client.shifts.repositories.*
import com.harbourspace.client.dtos.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class PersistShiftsService(
    private val shiftRepository: ShiftRepository,
    private val shiftRequestRepository: ShiftRequestRepository,
    val httpClient: WebClient
) {

    fun persistShiftsAsync(requestId: Int) {
        shiftRepository.findAllByRequestId(requestId).flatMap({ shift ->
            val vm = ClientShiftVm(
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.activity!!
            )
            persistShift(vm)
                .retryWhen(
                    Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5))
                        .maxBackoff(Duration.ofMinutes(5))
                )
        }, 5).collectList().flatMap {
            shiftRequestRepository.findById(requestId)
                .flatMap { req ->
                    shiftRequestRepository.save(req.copy(status = "done"))
                }
        }
            .subscribe()
    }

    fun persistShift(shift: ClientShiftVm): Mono<String> {
        return httpClient.post()
            .uri("/shift")
            .bodyValue(shift)
            .retrieve()
            .onStatus({ it.is5xxServerError }) { response ->
                response.bodyToMono(String::class.java)
                    .flatMap { Mono.error(RuntimeException("HTTP 5xx: $it")) }
            }
            .bodyToMono(String::class.java)
            .timeout(Duration.ofSeconds(60))
            .retryWhen(
                Retry.backoff(4, Duration.ofSeconds(4))
                    .maxBackoff(Duration.ofSeconds(15))
            )
            .doOnError { e ->
                println("Error with shift of user ${shift.userId}: ${e.message}")
            }
    }


}