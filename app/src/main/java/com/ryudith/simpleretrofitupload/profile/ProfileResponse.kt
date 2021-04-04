package com.ryudith.simpleretrofitupload.profile

data class ProfileResponse(
    val name : String,
    val email : String,
    val profile : String,
    val photo : List<String>
)
