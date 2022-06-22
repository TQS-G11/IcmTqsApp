package pt.ua.icm.icmtqsproject.ui.admin.view

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.birjuvachhani.locus.Locus
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import pt.ua.icm.icmtqsproject.AddDeliveryActivity
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.api.RetrofitBuilder
import pt.ua.icm.icmtqsproject.data.model.Delivery
import pt.ua.icm.icmtqsproject.ui.admin.adapter.AdminAdapter
import pt.ua.icm.icmtqsproject.ui.admin.viewmodel.AdminPageViewModel
import pt.ua.icm.icmtqsproject.ui.base.AdminViewModelFactory
import pt.ua.icm.icmtqsproject.ui.home.adapter.HomeAdapter
import pt.ua.icm.icmtqsproject.utils.Status

class AdminPage : AppCompatActivity(), AdminAdapter.AdminAdapterCallback {
    private lateinit var viewModel: AdminPageViewModel
    private lateinit var adapter: HomeAdapter

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            AdminViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(AdminPageViewModel::class.java)
    }

    private fun retrieveList(deliveries: List<Delivery>) {
        adapter.apply {
            notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        // Assign function to add button
        val addDeliveryButtons: FloatingActionButton = findViewById(R.id.floatingActionButton)
        addDeliveryButtons.setOnClickListener {
            val intent = Intent(this, AddDeliveryActivity::class.java)
            startActivity(intent)
        }

        // Recycler view api call
        setupViewModel()
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                // Recycler View
                viewModel.getDeliveries().observe(this, Observer {

                    val recyclerView: RecyclerView = findViewById(R.id.AdminRecyclerView)
                    val progressBar: ProgressBar = findViewById(R.id.AdminProgressBar)

                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                println("SUCCESS")
                                println("Data retrieved: " + resource.data)
                                recyclerView.adapter = AdminAdapter(resource.data as ArrayList<Delivery>, this)
                                recyclerView.layoutManager = LinearLayoutManager(this)
                                recyclerView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                retrieveList(resource.data)
                            }
                            Status.ERROR -> {
                                println("ERROR")
                                recyclerView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
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
    }

    // Show dialog with qr code upon clicking item on recycler view
    override fun onAdminAdapterClick(currentItem: Delivery) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.qr_code_popup)

        // QRCode generation
        val encoder = BarcodeEncoder()
        val qrcodeImage: ImageView = dialog.findViewById(R.id.qrcodeImage)
        qrcodeImage.setImageBitmap(encoder.encodeBitmap("https://62af0f88b735b6d16a4c2158.mockapi.io/api/v1/" + currentItem.deliveryId, BarcodeFormat.QR_CODE, 700, 700))

        dialog.show()
    }
}