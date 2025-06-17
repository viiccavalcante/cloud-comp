package com.harbourspace.client.dtos

import java.time.LocalDateTime

class ClientShiftsVm(
    val shifts: List<ClientShiftVm>
)

class ClientShiftVm(
    val companyId: String,
    val userId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val action: String
)