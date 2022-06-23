package pt.ua.icm.icmtqsproject.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Delivery   (

    val id:Long,
    val company : User,
    val user:Rider,
    val buyer : String,
    val riderId:String,
    val destination:String,
    val notes: String,
    val deliveryStatus : String,
    val origin: String,
    val price :Double,
    val requestedAt: LocalDateTime,
    val acceptedAt:LocalDateTime,
    val driverLat:Double,
    val driverLon:Double,
    val storeLat:Double,
    val storeLon:Double,
    val riderRating:Double
)

data class User (
    val id:Long,
    val username:String,
    val name:String,
    val role:String,
    val companyStatus:String,
    val riderStatus:String,
    val riderRating:Double,
    val ratingCount:Int,
    val img:String,
    val companyDescription:String
    )
