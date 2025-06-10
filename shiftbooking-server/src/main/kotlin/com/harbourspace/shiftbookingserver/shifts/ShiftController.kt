package com.harbourspace.shiftbookingserver.shifts

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ShiftController(val repository: ShiftRepository) {

    @FailureSimulator
    @PostMapping("/shifts")
    fun modifyShifts(@RequestBody shiftVm: ShiftVm): String {

        val shift = Shift(
            companyId = shiftVm.companyId,
            userId = shiftVm.userId,
            startTime = shiftVm.startTime,
            endTime = shiftVm.endTime
        )
        when (shiftVm.action) {
            "add" -> repository.save(shift)
        }

        return "{status: 'ok'}"
    }

    @GetMapping("/shifts")
    fun getShifts(): ShiftsViewVm {
        Thread.sleep(2000)
        val shifts = repository.findAll().map { shift ->
            ShiftViewVm(
                shiftId = shift.id,
                companyId = shift.companyId,
                userId = shift.userId,
                startTime = shift.startTime,
                endTime = shift.endTime,
            )
        }
        return ShiftsViewVm(shifts)
    }

    @DeleteMapping("/shifts/{shiftId}")
    fun deleteShift(@RequestBody shiftId: Long): String {
        val shift = repository.findById(shiftId)
        if (shift.isPresent) {
            repository.delete(shift.get())
            return "{status: 'ok'}"
        } else {
            throw IllegalStateException("Shift not found")
        }
    }
}

class ShiftsViewVm(
    val shifts: List<ShiftViewVm>
)

class ShiftVm(
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
    val action: String
)

class ShiftViewVm(
    val shiftId: Long,
    val companyId: String,
    val userId: String,
    val startTime: String,
    val endTime: String,
)
