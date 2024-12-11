package com.drs.auralife.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.drs.auralife.databinding.ActivityLocationBinding
import com.drs.auralife.ui.auth.LoginActivity

class LocationActivity : AppCompatActivity() {
    private val binding: ActivityLocationBinding by lazy {
        ActivityLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = listOf(
            "United", "States", "Canada", "Australia", "Japan",
            "Germany", "France", "Italy", "Brazil", "India", "China", "Viet Nam"
        )
        val adapter = ArrayAdapter(this, (17367043), locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.doAfterTextChanged {
            if (autoCompleteTextView.text.toString() == "Choose location") {
                autoCompleteTextView.error = "Please choose a location"
                autoCompleteTextView.requestFocus()
            } else {
                autoCompleteTextView.error = null
                autoCompleteTextView.requestFocus()
            }
        }

        binding.locationDoneButton.setOnClickListener {
            if (autoCompleteTextView.text.toString() == "Choose location") {
                autoCompleteTextView.error = "Please choose a location"
                autoCompleteTextView.requestFocus()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}