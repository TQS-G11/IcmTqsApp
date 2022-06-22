package pt.ua.icm.icmtqsproject.ui.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.model.Delivery
import kotlin.math.roundToInt

class HomeAdapter(private val deliveries: ArrayList<Delivery>, private val startPoint: Location, private val homeAdapterCallback: HomeAdapter.HomeAdapterCallback) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewDeliveryAddr: TextView
        private val textViewOriginAddr: TextView
        private val textViewDistance: TextView

        init {
            textViewDeliveryAddr = view.findViewById(R.id.textViewDeliveryAddr)
            textViewOriginAddr = view.findViewById(R.id.textViewOriginAddr)
            textViewDistance = view.findViewById(R.id.textViewDistance)
        }

        @SuppressLint("ResourceAsColor")
        fun bind(delivery: Delivery, startPoint: Location) {
            itemView.apply {
                textViewDeliveryAddr.text = delivery.deliveryAddr
                textViewOriginAddr.text = delivery.originAddr

                // Calculate distance
                val endPoint = Location("locationB")
                endPoint.latitude = delivery.latitude
                endPoint.longitude = delivery.longitude

                // Get distance and String
                println(startPoint)
                println(endPoint)
                val distance: Int = (startPoint.distanceTo(endPoint)/1000).roundToInt()
                println(distance)
                val distanceStr = "$distance Km"

                if (distance > 50){
                    textViewDistance.text = distanceStr
                    textViewDistance.setTextColor(Color.RED)
                } else if (distance in 10..50) {
                    textViewDistance.text = distanceStr
                    textViewDistance.setTextColor(R.color.orange)
                }else {
                    textViewDistance.text = distanceStr
                    textViewDistance.setTextColor(Color.GREEN)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = deliveries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deliveries[position], startPoint)
        holder.itemView.setOnClickListener{
            homeAdapterCallback.onHomeAdapterClick(deliveries[position], startPoint)
        }
    }
    interface HomeAdapterCallback{
        fun onHomeAdapterClick(currentItem:Delivery, startPoint: Location)
    }}