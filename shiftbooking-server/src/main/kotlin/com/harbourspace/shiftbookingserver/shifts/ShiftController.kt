package com.harbourspace.shiftbookingserver.shifts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ShiftController(val repository: ShiftRepository) {

    @PostMapping("/shifts")
    fun modifyShifts(@RequestBody shifts: ShiftsVm): String {
        shifts.shifts.forEach(
            { shiftVm ->
                val shift = Shift(
                    companyId = shiftVm.companyId,
                    userId = shiftVm.userId,
                    startTime = shiftVm.startTime,
                    endTime = shiftVm.endTime
                )
                when (shiftVm.action) {
                    "add" -> repository.save(shift)
                    "delete" -> repository.deleteById(shift.id)
                }
            }
        )
        return "{status: 'ok'}"
    }

    @GetMapping("/shifts")
    fun getShifts(): ShiftsViewVm {
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
}

class ShiftsVm(
    val shifts: List<ShiftVm>
)

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
