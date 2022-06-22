package pt.ua.icm.icmtqsproject

import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.birjuvachhani.locus.Locus
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.data.model.NewDelivery
import java.util.*


class AddDeliveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_delivery)

        // Api stuff
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Fields Content
        val customerIDValue: EditText = findViewById(R.id.customerID_text)
        val deliveryAddressValue: EditText = findViewById(R.id.delivery_address_text)
        val originAddressValue: EditText = findViewById(R.id.origin_address_text)
        val latValue: EditText = findViewById(R.id.lat_text)
        val longValue: EditText = findViewById(R.id.long_text)


        // Char buttons
        val autoLocationButton: Button = findViewById(R.id.autoLocation)
        val saveButton: Button = findViewById(R.id.SaveDeliveryButton)
        val cancelButton: Button = findViewById(R.id.CancelAddButton)

        // Char buttons
        autoLocationButton.setOnClickListener {
            // Get current location to fill the fields
            Locus.getCurrentLocation(this) { result ->
                result.location?.let {
                    latValue.setText(result.location!!.latitude.toString())
                    longValue.setText(result.location!!.longitude.toString())
                }
                result.error?.let { /* Received error! */ }
            }

        }

        saveButton.setOnClickListener {
            val customerId = customerIDValue.text.toString()
            val deliveryAddress = deliveryAddressValue.text.toString()
            val originAddress = originAddressValue.text.toString()
            val latitude = latValue.text.toString()
            val longitude = longValue.text.toString()


            if(customerId.isNotEmpty() && deliveryAddress.isNotEmpty() && originAddress.isNotEmpty() && latitude.isNotEmpty() && longitude.isNotEmpty()) {
                // Create Delivery
                val newDelivery: NewDelivery = NewDelivery(customerId, deliveryAddress, originAddress, latitude, longitude)
                val json: String = Gson().toJson(newDelivery)

                // Call Api to get register
                val client = OkHttpClient()

                val mediaType = "application/json".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                val request = Request.Builder()
                    .url("http://51.142.110.251/api/v1/deliveries?basicAuth=" + Base64.getEncoder().encodeToString(customerId.toByteArray()))
                    .post(body)
                    .build()
                println(request)

                val response = client.newCall(request).execute()
                println("RESPONSE: " + response)
                if(response.code == 201){
                    Toast.makeText(applicationContext, "Delivery Added", Toast.LENGTH_SHORT).show()
                    this.finish()
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Fill all fields", Toast.LENGTH_LONG).show()
            }
        }

        cancelButton.setOnClickListener {
            this.finish()
        }
    }
}