package com.drs.auralife.presentation.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import com.drs.auralife.feature.payment.R
import com.drs.auralife.feature.payment.databinding.FragmentPaymentBinding
import com.drs.auralife.domain.model.PaymentItem
import com.drs.auralife.presentation.payment.adapter.PaymentAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PaymentFragment : Fragment() {

    private val premiumViewModel: PremiumViewModel by viewModels()
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private lateinit var paymentAdapter: PaymentAdapter

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observePremiumStatus()
        premiumViewModel.loadPremiumStatus()
        setupPaymentItems()

        binding.btnChoose.setOnClickListener {
            paymentAdapter.getSelectedItem()?.let { selectedItem ->
                val bottomSheet = BottomSheetDialog(requireContext())
                val dialogView = layoutInflater.inflate(R.layout.dialog_payment_confirm, null)
                bottomSheet.setContentView(dialogView)

                dialogView.findViewById<TextView>(R.id.tvTitle).text = selectedItem.title
                dialogView.findViewById<TextView>(R.id.tvDescription).text = selectedItem.description
                dialogView.findViewById<TextView>(R.id.tvPrice).text = selectedItem.price

                dialogView.findViewById<Button>(R.id.btnPay).setOnClickListener {
                    bottomSheet.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        premiumViewModel.purchasePremium(selectedItem.month)
                    }, 1500)
                }
                bottomSheet.show()
            } ?: Toast.makeText(context, getString(R.string.payment_select_plan), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observePremiumStatus() {
        launchAndRepeatWithViewLifecycle {
                premiumViewModel.state.collect { state ->
                    val status = state.premiumStatus ?: return@collect
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
        launchAndRepeatWithViewLifecycle {
                premiumViewModel.effect.collect { effect ->
                    when (effect) {
                        is PaymentUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is PaymentUiEffect.PurchaseSuccess -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is PaymentUiEffect.PurchaseError -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun setupPaymentItems() {
        val paymentList = listOf(
            PaymentItem(1, getString(R.string.plan_1_month_title), getString(R.string.plan_1_month_desc), getString(R.string.plan_1_month_price)),
            PaymentItem(3, getString(R.string.plan_3_month_title), getString(R.string.plan_3_month_desc), getString(R.string.plan_3_month_price)),
            PaymentItem(6, getString(R.string.plan_6_month_title), getString(R.string.plan_6_month_desc), getString(R.string.plan_6_month_price)),
        )
        paymentAdapter = PaymentAdapter(paymentList)
        binding.rvPayment.adapter = paymentAdapter
    }
}

