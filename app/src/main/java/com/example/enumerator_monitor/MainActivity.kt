package com.example.enumerator_monitor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.enumerator_monitor.activities.AddEntryActivity
import com.example.enumerator_monitor.activities.AllEntriesActivity
import com.example.enumerator_monitor.databinding.ActivityMainBinding
import com.example.enumerator_monitor.fragments.AccountFragment
import com.example.enumerator_monitor.fragments.DashboardFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dashboardFragment: DashboardFragment? = null
    private var accountFragment: AccountFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set content view first
        setupFragments(savedInstanceState) // Then setup fragments
        setupNavigation() // Then setup navigation listeners
        if (savedInstanceState == null) {
            // Load initial fragment only when not restoring from a saved state
            loadFragment(R.id.dashboard)
        }
    }

    private fun setupFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // First time creation - create and add fragments
            dashboardFragment = DashboardFragment()
            accountFragment = AccountFragment()

            supportFragmentManager.beginTransaction().add(
                R.id.frameLayout, dashboardFragment!!, DashboardFragment::class.java.simpleName
            ).add(R.id.frameLayout, accountFragment!!, AccountFragment::class.java.simpleName)
                .hide(accountFragment!!).commit() // Hide the second fragment initially
        } else {
            // Configuration change - retrieve existing fragments
            dashboardFragment =
                supportFragmentManager.findFragmentByTag(DashboardFragment::class.java.simpleName) as? DashboardFragment
            accountFragment =
                supportFragmentManager.findFragmentByTag(AccountFragment::class.java.simpleName) as? AccountFragment
        }
    }


    private fun setupNavigation() {
        // Setup BottomNavigationView (portrait)
        binding.btmNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard, R.id.account -> loadFragment(item.itemId)
                else -> startEntryActivity()
            }
            item.itemId != R.id.addEntry
        }
        binding.ivMore.setOnClickListener { startEntryActivity() }


    }

    private fun startEntryActivity() {
        startActivity(Intent(this, AddEntryActivity::class.java))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedFragmentId", binding.btmNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the selected tab and fragment
        loadFragment(savedInstanceState.getInt("selectedFragmentId", R.id.dashboard))
    }

    private fun loadFragment(fragmentId: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        // Hide all fragments first
        dashboardFragment?.let { transaction.hide(it) }
        accountFragment?.let { transaction.hide(it) }

        // Show the selected fragment
        when (fragmentId) {
            R.id.dashboard -> {
                dashboardFragment?.let { transaction.show(it) }
            }

            R.id.account -> {
                accountFragment?.let { transaction.show(it) }
            }
        }
        transaction.commit()
    }
}