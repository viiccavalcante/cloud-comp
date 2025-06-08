package com.harbourspace.client.shifts

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@RestController
class ClientController(val httpClient: WebClient) {


    @PostMapping("/clientshifts")
    fun modifyShifts(@RequestBody shiftsVm: ClientShiftsVm): String {

        shiftsVm.shifts.forEach { shift ->
            val shiftVm = ClientShiftVm(
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
                action = shift.action
            )
            httpClient.post()
                .uri("/shift")
                .bodyValue(shiftVm)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
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