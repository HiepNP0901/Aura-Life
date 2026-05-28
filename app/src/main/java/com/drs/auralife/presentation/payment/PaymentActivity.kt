package com.drs.auralife.presentation.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.R
import com.drs.auralife.domain.model.PaymentItem
import com.drs.auralife.databinding.ActivityPaymentBinding
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    private val premiumViewModel: PremiumViewModel by viewModels()
    private lateinit var paymentAdapter: PaymentAdapter

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        observePremiumStatus()
        premiumViewModel.loadPremiumStatus()
        setupPaymentItems()
        binding.btnChoose.setOnClickListener {
            paymentAdapter.getSelectedItem()?.let { selectedItem ->

                val bottomSheet = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.dialog_payment_confirm, null)
                bottomSheet.setContentView(view)

                view.findViewById<TextView>(R.id.tvTitle).text = selectedItem.title
                view.findViewById<TextView>(R.id.tvDescription).text = selectedItem.description
                view.findViewById<TextView>(R.id.tvPrice).text = selectedItem.price

                view.findViewById<Button>(R.id.btnPay).setOnClickListener {
                    bottomSheet.dismiss()

                    Handler(Looper.getMainLooper()).postDelayed({
                        premiumViewModel.purchasePremium(selectedItem.month)
                    }, PURCHASE_DELAY_MS)
                }

                bottomSheet.show()
            } ?: Toast.makeText(this, getString(R.string.payment_select_plan), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observePremiumStatus() {
        lifecycleScope.launch {
            premiumViewModel.premiumStatus.collect { status ->
                if (status == null) return@collect
                binding.apply {
                    if (status.isPremium) {
                        tvPremium.text = getString(R.string.premium_status)
                        val formattedDate = status.expiryTimestamp?.let {
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                        } ?: ""
                        tvDate.text = getString(R.string.registration_date, formattedDate)
                        tvExpireDate.text = getString(R.string.expiry_date, formattedDate)
                        tvDate.visibility = View.VISIBLE
                        tvExpireDate.visibility = View.VISIBLE
                    } else {
                        tvPremium.text = getString(R.string.free_status)
                        tvDate.visibility = View.GONE
                        tvExpireDate.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launch {
            premiumViewModel.purchaseResult.collect { result ->
                result.onSuccess { success ->
                    if (success) {
                        Toast.makeText(this@PaymentActivity, getString(R.string.purchase_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PaymentActivity, getString(R.string.payment_unavailable), Toast.LENGTH_LONG).show()
                    }
                }.onFailure {
                    Toast.makeText(this@PaymentActivity, getString(R.string.payment_unavailable), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupPaymentItems() {
        val paymentList =
            listOf(
                PaymentItem(
                    month = PLAN_1_MONTH,
                    title = getString(R.string.plan_1_month_title),
                    description = getString(R.string.plan_1_month_desc),
                    price = getString(R.string.plan_1_month_price),
                ),
                PaymentItem(
                    month = PLAN_3_MONTH,
                    title = getString(R.string.plan_3_month_title),
                    description = getString(R.string.plan_3_month_desc),
                    price = getString(R.string.plan_3_month_price),
                ),
                PaymentItem(
                    month = PLAN_6_MONTH,
                    title = getString(R.string.plan_6_month_title),
                    description = getString(R.string.plan_6_month_desc),
                    price = getString(R.string.plan_6_month_price),
                ),
            )

        paymentAdapter = PaymentAdapter(paymentList)
        binding.rvPayment.adapter = paymentAdapter
    }

    companion object {
        private const val PURCHASE_DELAY_MS = 1500L
        private const val PLAN_1_MONTH = 1
        private const val PLAN_3_MONTH = 3
        private const val PLAN_6_MONTH = 6
    }
}
