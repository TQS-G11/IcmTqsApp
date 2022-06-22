package pt.ua.icm.icmtqsproject.data.model

data class NewDelivery(
    val customerId:String,
    val deliveryAddr:String,
    val originAddr:String,
    val latitude:String,
    val longitude:String
)