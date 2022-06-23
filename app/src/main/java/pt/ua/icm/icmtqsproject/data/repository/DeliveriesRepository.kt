package pt.ua.icm.icmtqsproject.data.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.model.Delivery
import java.util.*

class DeliveriesRepository(private val apiHelper: ApiHelper) {

    fun getDeliveries(token:String) : List<Delivery>{
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://213.199.129.9/api/deliveries")
            .get()
            .header("Authentication","Bearer "+token)
            .build()
        println("REQEST: "+request)
        val response = client.newCall(request).execute()
        println("RESPONSE: "+response)

        return emptyList()
    }
}

