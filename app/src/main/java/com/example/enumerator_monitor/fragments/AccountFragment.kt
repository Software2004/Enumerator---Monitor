package com.example.enumerator_monitor.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.enumerator_monitor.activities.LoginActivity
import com.example.enumerator_monitor.activities.PrivacyPolicyActivity
import com.example.enumerator_monitor.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProfileName.text = "Hammad Hanif"
        binding.tvRole.text = "Enumerator"

        binding.btnPrivacyPolicy.setOnClickListener {
            startActivity(Intent(requireContext(), PrivacyPolicyActivity::class.java))
        }

        binding.btnShareApp.setOnClickListener { shareApp() }
        binding.btnReportIssue.setOnClickListener { reportIssue() }
        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    private fun shareApp() {
        val shareText =
            "I'm using Enumerator Monitor to track field entries. Get it here: https://example.com/app"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Share app"))
    }

    private fun reportIssue() {
        val appVersion = try {
            requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            ).versionName
        } catch (e: Exception) {
            "1.0.0"
        }
        val device = "${Build.MANUFACTURER} ${Build.MODEL} (${Build.VERSION.RELEASE})"
        val body =
            "Device: $device\nApp Version: $appVersion\nUser: ${binding.tvProfileName.text}\n\nDescribe your issue here..."

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("fawad.hanif24@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Enumerator Monitor - Issue Report")
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(requireContext()).setTitle("Logout")
            .setMessage("Are you sure you want to logout?").setPositiveButton("Logout") { _, _ ->
                val i = Intent(requireContext(), LoginActivity::class.java)
                i.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                requireActivity().finish()
            }.setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}