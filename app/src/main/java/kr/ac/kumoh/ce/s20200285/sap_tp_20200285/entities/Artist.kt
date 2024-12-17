package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities

import java.util.Date

data class Artist(
    val id: Int,
    val createdAt: Date,
    val name: String,
    val nameKr: String,
    val profileImageUrl: String,
    val keyword: String,
)
