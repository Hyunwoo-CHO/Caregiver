package com.hsilveredu.caregiver.database

data class User (
    var uid : String,
    var nickname : String,
    var profile : String,
    var payment : Boolean
)