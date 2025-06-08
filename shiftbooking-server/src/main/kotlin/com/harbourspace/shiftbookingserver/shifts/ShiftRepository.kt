package com.harbourspace.shiftbookingserver.shifts

import org.springframework.data.jpa.repository.JpaRepository

interface ShiftRepository : JpaRepository<Shift, Long>