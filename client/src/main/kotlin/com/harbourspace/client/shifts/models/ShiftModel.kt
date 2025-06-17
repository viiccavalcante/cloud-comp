package com.harbourspace.client.shifts.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("shifts")
data class Shift(
    @Id val id: Int? = null,
    val requestId: Int,
    val companyId: String,
    val userId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val activity: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)