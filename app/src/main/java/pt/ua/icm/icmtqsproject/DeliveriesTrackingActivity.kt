package pt.ua.icm.icmtqsproject

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.api.RetrofitBuilder
import pt.ua.icm.icmtqsproject.data.model.Delivery
import pt.ua.icm.icmtqsproject.data.model.NewDelivery
import pt.ua.icm.icmtqsproject.data.model.Rider
import pt.ua.icm.icmtqsproject.ui.base.ViewModelFactory
import pt.ua.icm.icmtqsproject.ui.home.viewmodel.HomePageViewModel
import pt.ua.icm.icmtqsproject.utils.Status
import java.util.*


class DeliveriesTrackingActivity : AppCompatActivity() {

    private fun doPostApi(deliveryId: String, riderId: String){
        // Call Api to get register
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://51.142.110.251/api/deliveries/rider/update?data=" + deliveryId + "," + riderId + ","+ Base64.getEncoder().encodeToString(riderId.toByteArray()))
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), "{}"))
            .build()
        println(request)

        val response = client.newCall(request).execute()

        if(response.code == 201){
            Toast.makeText(applicationContext, "Status Updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliveries_tracking)
        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val riderId = sharedPreferences.getString("riderId", "").toString()
        val deliveryId = sharedPreferences.getString("deliveryId", "").toString()
        val deliveryAddr = sharedPreferences.getString("deliveryAddr", "").toString()
        val originAddr = sharedPreferences.getString("originAddr", "").toString()

        var deliveryStage = sharedPreferences.getString("deliveryStage", "").toString()

        // Layout stuff
        val deliveryStatus: TextView = findViewById(R.id.deliveryStatus)
        val instruction: TextView = findViewById(R.id.instructionContent)
        val tooltip: TextView = findViewById(R.id.tooltip)
        val instructionLabel :TextView = findViewById(R.id.instructionLabel)
        val stage0img : ImageView = findViewById(R.id.stage0Image)
        stage0img.visibility = View.VISIBLE
        instruction.visibility = View.VISIBLE
        instruction.text = originAddr
        deliveryStatus.text = "Fetching"
        val stage1img : ImageView = findViewById(R.id.stage1Image)

        val stage2img : ImageView = findViewById(R.id.stage2Image)

        // Finish delivery button
        val finishButton: Button = findViewById(R.id.finishButton)
        //finishButton.background.colorFilter = BlendModeColorFilter(R.color.purple_700, BlendMode.HUE)

        println(deliveryAddr)
        println("WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        println(originAddr)
        println(instruction.text)
        println("SDFGSDFGDRGHDFGHDFGDFGHDFGHDFGh")

        finishButton.setOnClickListener {
            // Get Stage from API
            println("STAGE:" + deliveryStage)
            when (deliveryStage) {
                "" -> {
                    doPostApi(deliveryId, riderId)
                    val editor = sharedPreferences.edit()
                    editor.putString("deliveryStage", "FETCHING")
                    editor.apply()
                    deliveryStage = "FETCHING"
                }
                "BID_CHECK" -> {
                    doPostApi(deliveryId, riderId)
                    val editor = sharedPreferences.edit()
                    editor.putString("deliveryStage", "FETCHING")
                    editor.apply()
                    deliveryStage = "FETCHING"
                    instruction.text = originAddr
                    instructionLabel.text = "Pick up at:"
                    deliveryStatus.text="Picked Up"

                    finishButton.text ="Pick up"
                    stage0img.visibility = View.VISIBLE

                }
                "FETCHING" -> {
                    deliveryStatus.text = "In Delivery"
                    deliveryStage = "SHIPPED"
                    finishButton.text = "Deliver"
                    //finishButton.background.colorFilter = BlendModeColorFilter(R.color.green, BlendMode.HUE)
                    tooltip.visibility = View.VISIBLE
                    instruction.text = deliveryAddr
                    instructionLabel.text = "Deliver to:"
                    doPostApi(deliveryId, riderId)
                    val editor = sharedPreferences.edit()
                    editor.putString("deliveryStage", "SHIPPED")

                    editor.apply()
                    stage0img.visibility = View.INVISIBLE
                    stage1img.visibility = View.VISIBLE
                }
                "SHIPPED" -> {
                    val editor = sharedPreferences.edit()
                    editor.putString("deliveryStage", "DELIVERED")
                    editor.apply()
                    deliveryStatus.text = "Arrived"
                    finishButton.text = "Scan QR"
                    stage2img.visibility = View.VISIBLE
                    stage1img.visibility = View.INVISIBLE
                    deliveryStage = "SCAN"
                    // finishButton.background.colorFilter = BlendModeColorFilter(R.color.blue, BlendMode.HUE)
                    instruction.visibility = View.INVISIBLE
                    instructionLabel.visibility = View.INVISIBLE


                }
                "SCAN"->{
                    val intentIntegrator = IntentIntegrator(this)
                    intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
                    intentIntegrator.initiateScan()
                    deliveryStage = "DELIVERED"
                    deliveryStatus.text = "Completed"
                    finishButton.text = "Go Back"
                    tooltip.visibility = View.INVISIBLE

                }
                "DELIVERED" -> {


                    val editor = sharedPreferences.edit()
                    editor.putString("isAssigned", "false")
                    editor.putString("deliveryStage", "BID_CHECK")
                    editor.apply()
                    this.finish()
                }
            }
        }
    }
}

