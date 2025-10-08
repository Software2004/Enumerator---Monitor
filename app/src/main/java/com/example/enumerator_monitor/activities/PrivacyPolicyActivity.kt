package com.example.enumerator_monitor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.enumerator_monitor.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tvContent.text = "We value your privacy. This app stores minimal necessary data on your device and communicates securely with our services. We never sell your data. For issues, contact support." +
                "\n\n- Data Collection: Only authentication and entry metadata.\n- Storage: Local device and secure backend.\n- Sharing: None without consent.\n- Contact: fawad.hanif24@gmail.com"
    }
}