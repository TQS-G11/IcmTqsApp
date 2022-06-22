package pt.ua.icm.icmtqsproject.data.api

import pt.ua.icm.icmtqsproject.data.model.*
import retrofit2.http.GET

interface ApiService {
    @GET("deliveries")
    suspend fun getDeliveries(): List<Delivery>
}