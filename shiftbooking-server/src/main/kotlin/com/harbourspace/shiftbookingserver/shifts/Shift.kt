package com.harbourspace.shiftbookingserver.shifts

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "shifts")
class Shift(
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    open var id: Long = 0,

    @Column(name = "company_id", nullable = false)
    open var companyId: String = "",

    @Column(name = "user_id", nullable = false)
    open var userId: String = "",

    @Column(name = "start_time", nullable = false)
    open var startTime: String = "",

    @Column(name = "end_time", nullable = false)
    open var endTime: String = "",
) {
    // JPA requires a no-arg constructor
    constructor() : this(0, "", "", "", "")
}