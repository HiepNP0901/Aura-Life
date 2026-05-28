package com.drs.auralife.presentation.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.drs.auralife.R
import com.drs.auralife.data.firebase.realtime.database.user.premium.PremiumRepository
import com.drs.auralife.data.model.PaymentItem
import com.drs.auralife.databinding.ActivityPaymentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    private lateinit var paymentAdapter: PaymentAdapter

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.apply {
            PremiumRepository.getPremiumStatus {
                if (it.status) {
                    tvPremium.text = "Premium"
                    tvDate.text = "Ngày đăng ký: " + it.date
                    tvExpireDate.text = "Ngày hết hạn: " + it.expireDate
                    tvDate.visibility = View.VISIBLE
                    tvExpireDate.visibility = View.VISIBLE
                } else {
                    tvPremium.text = "Free"
                    tvDate.visibility = View.GONE
                    tvExpireDate.visibility = View.GONE
                }
            }
        }
        setupOnboardingItems()
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
                        PremiumRepository.uploadPremium(selectedItem.month) {
                            if (it.isFailure) {
                                Toast
                                    .makeText(this, "❌ Chức năng thanh toán hiện chưa khả dụng, vui lòng liên hệ Admin để nâng cấp tài khoản.", Toast.LENGTH_LONG)
                                    .show()
                                return@uploadPremium
                            }
                            Toast
                                .makeText(
                                    this,
                                    "✅ Đã mua ${selectedItem.title}",
                                    Toast.LENGTH_SHORT,
                                ).show()

                            binding.apply {
                                PremiumRepository.getPremiumStatus {
                                    if (it.status) {
                                        tvPremium.text = "Premium"
                                        tvDate.text = "Ngày đăng ký: " + it.date
                                        tvExpireDate.text = "Ngày hết hạn: " + it.expireDate
                                        tvDate.visibility = View.VISIBLE
                                        tvExpireDate.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }, 1500)
                }

                bottomSheet.show()
            } ?: Toast.makeText(this, "Chọn một gói trước khi tiếp tục", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOnboardingItems() {
        val paymentList =
            listOf(
                PaymentItem(
                    month = 1,
                    title = "Gói 1 tháng",
                    description = "Gói xem phim tiêu chuẩn",
                    price = "59.000đ/tháng",
                ),
                PaymentItem(
                    month = 3,
                    title = "Gói 3 tháng",
                    description = "Tiết kiệm hơn 10%",
                    price = "159.000đ/3 tháng",
                ),
                PaymentItem(
                    month = 6,
                    title = "Gói 6 tháng",
                    description = "Tiết kiệm hơn 21%",
                    price = "279.000đ/6 tháng",
                ),
            )

        paymentAdapter = PaymentAdapter(paymentList)
        binding.rvPayment.adapter = paymentAdapter
    }
}

