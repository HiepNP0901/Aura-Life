package com.drs.auralife.presentation.payment.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.payment.R
import com.drs.auralife.domain.model.PaymentItem

class PaymentAdapter(
    private val paymentItems: List<PaymentItem>,
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view.findViewById<CardView>(R.id.cv_payment_item)
        private val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        private val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        private val tvPrice = view.findViewById<TextView>(R.id.tv_price)

        fun bind(item: PaymentItem, isSelected: Boolean) {
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvPrice.text = item.price

            cardView.setCardBackgroundColor(if (isSelected) Color.BLUE else Color.WHITE)
            val textColor = if (isSelected) Color.WHITE else Color.BLACK
            tvTitle.setTextColor(textColor)
            tvDescription.setTextColor(textColor)
            tvPrice.setTextColor(if (isSelected) Color.WHITE else Color.GRAY)

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun getItemCount(): Int = paymentItems.size

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(paymentItems[position], position == selectedPosition)
    }

    fun getSelectedItem(): PaymentItem? =
        if (selectedPosition != RecyclerView.NO_POSITION) paymentItems[selectedPosition] else null
}
