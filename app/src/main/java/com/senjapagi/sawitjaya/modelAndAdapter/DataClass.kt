package com.senjapagi.sawitjaya.modelAndAdapter

data class modelReqOrder(
    val id :String,
    val user_id :String,
    val est_weight :String,
    val addr :String,
    val cord_lat:String,
    val cord_lon:String,
    val alt_contact:String,
    val created_at:String,
    val updated_at:String,
    val deleted_at:String,
    val status:String
)
data class modelInvoice(
    val m : String
)
