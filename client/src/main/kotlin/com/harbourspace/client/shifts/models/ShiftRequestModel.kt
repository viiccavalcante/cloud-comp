package com.harbourspace.client.shifts.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("requests")
data class ShiftRequest(
    @Id val id: Int? = null,
    val status: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)