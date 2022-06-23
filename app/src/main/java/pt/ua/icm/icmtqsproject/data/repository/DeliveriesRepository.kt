package pt.ua.icm.icmtqsproject.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.model.Delivery
import pt.ua.icm.icmtqsproject.ui.toMap
import java.util.*

class DeliveriesRepository(private val apiHelper: ApiHelper) {

    fun getDeliveries(token:String) : List<Delivery>{
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://213.199.129.9:8082/api/deliveries")
            .get()
            .header("Authorization","Bearer "+token)
            .build()
        val response = client.newCall(request).execute()
        val jsonObj = JSONObject(response.body?.string() ?: "asd").toMap()
        val orders : List<Delivery> = jsonObj["orders"] as List<Delivery>
        print("ORDERS"+orders.toString())
        return orders
    }
}

