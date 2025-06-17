package com.harbourspace.client.shifts.repositories

import com.harbourspace.client.shifts.models.ShiftRequest
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ShiftRequestRepository : ReactiveCrudRepository<ShiftRequest, Int>
