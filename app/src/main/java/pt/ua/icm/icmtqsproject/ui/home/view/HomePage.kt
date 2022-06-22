package pt.ua.icm.icmtqsproject.ui.home.view

import android.content.Intent
import android.app.Dialog
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.birjuvachhani.locus.Locus
import com.google.gson.Gson
import io.karn.notify.Notify
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.api.RetrofitBuilder
import pt.ua.icm.icmtqsproject.data.model.Delivery
import pt.ua.icm.icmtqsproject.databinding.ActivityHomePageBinding
import pt.ua.icm.icmtqsproject.DeliveriesTrackingActivity
import pt.ua.icm.icmtqsproject.data.model.Bid
import pt.ua.icm.icmtqsproject.data.model.NewDelivery
import pt.ua.icm.icmtqsproject.ui.base.ViewModelFactory
import pt.ua.icm.icmtqsproject.ui.home.adapter.HomeAdapter
import pt.ua.icm.icmtqsproject.ui.home.viewmodel.HomePageViewModel
import pt.ua.icm.icmtqsproject.utils.Status
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class HomePage : AppCompatActivity(),HomeAdapter.HomeAdapterCallback {
    private lateinit var viewModel: HomePageViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var timer: Timer
    private val noDelay = 0L
    private val everyFiveSeconds = 5000L

    private var hasStartedDelivery :Boolean = false
    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomePageViewModel::class.java)
    }

    private fun retrieveList(deliveries: List<Delivery>) {
        adapter.apply {
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        hasStartedDelivery = false
        // Check if Rider is already assigned to work
        val isAssigned = sharedPreferences.getString("isAssigned", "")
        if (isAssigned.equals("true")){
            val intent = Intent(this, DeliveriesTrackingActivity::class.java)
            hasStartedDelivery = true
            startActivity(intent)
        }

        val binding : ActivityHomePageBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home_page)

        // Recycler view api call
        setupViewModel()
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                // Recycler View
                viewModel.getDeliveries().observe(this, Observer {

                    val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                    val progressBar: ProgressBar = findViewById(R.id.progressBar)

                    // Used to get Distance
                    val startPoint = Location("locationA")
                    startPoint.latitude = result.location!!.latitude
                    startPoint.longitude = result.location!!.longitude

                    // Display data retrieved from API
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                println("SUCCESS")
                                val toDeliver = resource.data?.filter { delivery -> delivery.deliveryState == "BID_CHECK" }
                                recyclerView.adapter = HomeAdapter(toDeliver as ArrayList<Delivery>, startPoint,this)
                                recyclerView.layoutManager = LinearLayoutManager(this)
                                recyclerView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                retrieveList(toDeliver)
                            }
                            Status.ERROR -> {
                                println("ERROR")
                                recyclerView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                                println(it.message)
                            }
                            Status.LOADING -> {
                                println("LOADING")
                                progressBar.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            }
                        }
                    }
                })

            }
            result.error?.let { /* Received error! */ }
        }

        // Check if rider was assigned on any work
        val timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    // Retrieve data from UI
                    viewModel.getDeliveries().observe(this@HomePage, Observer {
                        // Display data retrieved from API
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    println("SUCCESS")
                                    if (resource.data?.any { delivery -> delivery.riderId == sharedPreferences.getString("riderId", "") } == true){
                                        // Notification
                                        Notify
                                            .with(this@HomePage)
                                            .content { // this: Payload.Content.Default
                                                title = "Work assigned successfully"
                                                text = "The delivery you showed interest, was assigned to you."
                                            }
                                            .show()

                                        // Set on preferences
                                        val editor = sharedPreferences.edit()
                                        editor.putString("isAssigned", "true")
                                        val deliveryAssigned: Delivery? = resource.data.find { delivery -> delivery.riderId == sharedPreferences.getString("riderId", "") && delivery.deliveryState != "DELIVERED"  }
                                        if (deliveryAssigned != null) {
                                            editor.putString("deliveryId",deliveryAssigned.deliveryId.toString())
                                        }
                                        if (deliveryAssigned != null) {
                                            editor.putString("deliveryAddr",deliveryAssigned.deliveryAddr)
                                        }
                                        if (deliveryAssigned != null) {
                                            editor.putString("deliveryAddr",deliveryAssigned.originAddr)
                                        }
                                        editor.apply()

                                        onPause()
                                    }
                                }
                                Status.ERROR -> {
                                    println("ERROR")
                                }
                                Status.LOADING -> {
                                    println("LOADING")
                                }
                            }
                        }
                    })
                }
            }
        }

        timer = Timer()
        timer.schedule(timerTask, noDelay, everyFiveSeconds)
    }

    override fun onPause() {
        super.onPause()

        timer.cancel()
        timer.purge()

        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Check if Rider is already assigned to work
        val isAssigned = sharedPreferences.getString("isAssigned", "")
        println("ASSIGNED: " + isAssigned)
        println("hasStartedDelivery: " +  hasStartedDelivery)
        if (isAssigned.equals("true") and !hasStartedDelivery){
            val intent = Intent(this,  DeliveriesTrackingActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onHomeAdapterClick(currentItem: Delivery, startPoint: Location)
    {
        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val riderId: String? = sharedPreferences.getString("riderId", "")

        println(currentItem)

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.home_delivery_popup)

        val origin : TextView =dialog.findViewById(R.id.originAddress)
        val dest: TextView = dialog.findViewById(R.id.deliverAddress)
        val acceptButton : Button = dialog.findViewById(R.id.DeliveryBigButton)

        origin.text = currentItem.originAddr
        dest.text = currentItem.deliveryAddr

        // Get distance
        // Calculate distance
        val endPoint = Location("locationB")
        endPoint.latitude = currentItem.latitude
        endPoint.longitude = currentItem.longitude

        // Get distance and String
        val distance: Double = (startPoint.distanceTo(endPoint)/1000).toDouble()

        acceptButton.setOnClickListener{

            // Create Bid
            val bid: Bid = Bid(riderId.toString(), currentItem.deliveryId, distance)
            val json: String = Gson().toJson(bid)

            // Call Api to get register
            val client = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            val request = Request.Builder()
                .url("http://51.142.110.251/api/v1/deliveries/bid?basicAuth=" + Base64.getEncoder().encodeToString(riderId.toString().toByteArray()))
                .post(body)
                .build()
            // println(json)
            val response = client.newCall(request).execute()
            // println("RESPONSE: " + response)
            if(response.code == 201){
                Toast.makeText(applicationContext, "Bid Added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()
    }
}

