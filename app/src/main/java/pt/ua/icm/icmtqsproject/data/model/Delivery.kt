package pt.ua.icm.icmtqsproject.data.model

data class Delivery   (
    val customerId:String,
    val deliveryAddr:String,
    val originAddr:String,
    val latitude:Double,
    val longitude:Double,
    val deliveryState:String,
    val deliveryId:Long,
    val riderId:String
)