package pt.ua.icm.icmtqsproject.data.repository

import pt.ua.icm.icmtqsproject.data.api.ApiHelper

class DeliveriesRepository(private val apiHelper: ApiHelper) {
    suspend fun getUsers() = apiHelper.getDeliveries()
}