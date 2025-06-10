package com.harbourspace.client.shifts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
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

        shiftsVm.shifts.forEach { shift ->
            val shiftVm = ClientShiftVm(
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.action,
            )
            httpClient.post()
                .uri("/shifts")
                .bodyValue(shiftVm)
                .retrieve()
                .bodyToMono(String::class.java)
                .retryWhen(
                    Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                )
                .block(Duration.ofSeconds(1))
        }

        return "{status: 'ok'}"
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