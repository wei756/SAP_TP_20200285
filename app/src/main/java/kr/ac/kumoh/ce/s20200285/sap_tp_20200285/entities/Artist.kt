package kr.ac.kumoh.ce.s20200285.sap_tp_20200285.entities

import java.util.Date

data class Artist(
    val id: Int,
    val created_at: Date,
    val name: String,
    val name_kr: String,
    val profile_image_url: String,
    val keyword: String,
)
