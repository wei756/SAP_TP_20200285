package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities

import java.util.Date

data class Schedule(
    val id: Int,
    val key: String,
    val title: String,
    val artist: String,
    val startDate: Date,
    val endDate: Date,
    val place: String,
    val site: String,
)