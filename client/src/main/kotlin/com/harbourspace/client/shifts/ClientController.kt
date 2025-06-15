package com.harbourspace.client.shifts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@RestController
class ClientController(val httpClient: WebClient) {


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
                .flatMap({ shift -> persistShift(shift) }, 5)
                .collectList()
                .block()
        }

        return "{status: 'ok'}"
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

class ClientShiftsVm(
    val shifts: List<ClientShiftVm>
)

class ClientShiftVm(
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
    val action: String
)