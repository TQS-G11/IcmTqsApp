package pt.ua.icm.icmtqsproject.ui.admin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.model.Delivery


class AdminAdapter(private val deliveries:ArrayList<Delivery>, private val adminAdapterCallback: AdminAdapterCallback) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewDeliveryAddr: TextView
        private val textViewOriginAddr: TextView
        private val textViewCustomerId: TextView

        init {
            textViewDeliveryAddr = view.findViewById(R.id.textViewAdminDeliveryAddr)
            textViewOriginAddr = view.findViewById(R.id.textViewAdminOriginAddr)
            textViewCustomerId = view.findViewById(R.id.textViewAdminCustomerId)
        }

        @SuppressLint("ResourceAsColor")
        fun bind(delivery: Delivery) {
            itemView.apply {
                textViewDeliveryAddr.text = delivery.deliveryAddr
                textViewOriginAddr.text = delivery.originAddr
                textViewCustomerId.text = delivery.customerId
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_admin_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = deliveries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deliveries[position])
        holder.itemView.setOnClickListener {
            adminAdapterCallback.onAdminAdapterClick(deliveries[position])
        }
    }

    interface AdminAdapterCallback{
        fun onAdminAdapterClick(currentItem: Delivery)
    }
}