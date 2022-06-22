package pt.ua.icm.icmtqsproject.data.api

import pt.ua.icm.icmtqsproject.data.model.Bid
import pt.ua.icm.icmtqsproject.data.model.LoginRequest
import pt.ua.icm.icmtqsproject.data.model.NewDelivery
import pt.ua.icm.icmtqsproject.data.model.Rider

class ApiHelper(private val apiService: ApiService) {
    suspend fun getDeliveries() = apiService.getDeliveries()
}