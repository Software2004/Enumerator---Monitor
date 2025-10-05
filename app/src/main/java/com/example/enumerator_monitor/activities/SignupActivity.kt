package com.example.enumerator_monitor.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.enumerator_monitor.MainActivity
import com.example.enumerator_monitor.R
import com.example.enumerator_monitor.viewmodel.AuthViewModel
import com.example.enumerator_monitor.data.UserRole
import com.example.enumerator_monitor.databinding.ActivitySignupBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var selectedRole: UserRole = UserRole.ENUMERATOR

    companion object {
        private const val TAG = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
        setupRoleSelection()
        setupTextWatchers()
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(this, Observer { isLoading ->
            Log.d(TAG, "isLoading observer triggered. isLoading: $isLoading")
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnSignUp.hideText()
            } else {
                binding.progressBar.gone()
                binding.btnSignUp.showText()
            }
        })

        authViewModel.errorMessage.observe(this, Observer { message ->
            message?.let {
                Log.e(TAG, "errorMessage observer triggered. Message: $it")
                showErrorOnFields(it)
                showSnackbar(it, isError = true)
                authViewModel.clearMessages()
            }
        })

        authViewModel.successMessage.observe(this, Observer { message ->
            Log.i(TAG, "successMessage observer triggered. Message: $message")
            message?.let {
                showSnackbar(it, isError = false)
                authViewModel.clearMessages()
            }
        })

        // FIXED: Removed duplicate observer - keep only one
        authViewModel.currentUser.observe(this, Observer { user ->
            user?.let {
                Log.i(TAG, "currentUser observer triggered. User logged in, navigating to MainActivity.")
                showSnackbar("Account created successfully! Welcome aboard.", isError = false)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })
    }

    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners.")
        binding.btnSignUp.setOnClickListener {
            clearAllErrors()
            signup()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun setupRoleSelection() {
        Log.d(TAG, "Setting up role selection.")
        binding.btnEnumerator.setOnClickListener {
            selectRole(UserRole.ENUMERATOR)
        }

        binding.btnTehsilMonitor.setOnClickListener {
            selectRole(UserRole.TEHSIL_MONITOR)
        }

        // Set initial selection
        selectRole(UserRole.ENUMERATOR)
    }

    private fun selectRole(role: UserRole) {
        Log.d(TAG, "Role selected: $role")
        selectedRole = role

        when (role) {
            UserRole.ENUMERATOR -> {
                binding.btnEnumerator.apply {
                    setTextColor(getColor(R.color.blue))
                    setBackgroundColor(getColor(R.color.white))
                }
                binding.btnTehsilMonitor.apply {
                    setTextColor(getColor(R.color.textColorSecondary))
                    setBackgroundColor(getColor(R.color.background_secondary))
                }
            }

            UserRole.TEHSIL_MONITOR -> {
                binding.btnTehsilMonitor.apply {
                    setTextColor(getColor(R.color.blue))
                    setBackgroundColor(getColor(R.color.white))
                }
                binding.btnEnumerator.apply {
                    setTextColor(getColor(R.color.textColorSecondary))
                    setBackgroundColor(getColor(R.color.background_secondary))
                }
            }
        }
    }

    private fun setupTextWatchers() {
        Log.d(TAG, "Setting up text watchers.")

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilName.isErrorEnabled) {
                    binding.tilName.error = null
                }
            }
        })

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilEmail.isErrorEnabled) {
                    binding.tilEmail.error = null
                }
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilPassword.isErrorEnabled) {
                    binding.tilPassword.error = null
                }
            }
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilConfirmPassword.isErrorEnabled) {
                    binding.tilConfirmPassword.error = null
                }
            }
        })

        binding.etBlockNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilBlockNo.isErrorEnabled) {
                    binding.tilBlockNo.error = null
                }
            }
        })
    }

    private fun signup() {
        Log.d(TAG, "signup function called.")
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val blockNo = binding.etBlockNo.text.toString().trim()

        Log.d(TAG, "Attempting to validate inputs.")
        if (validateInputs(email, password, confirmPassword, name, blockNo)) {
            Log.i(TAG, "Input validation successful. Calling ViewModel to sign up.")
            authViewModel.signup(email, password, name, selectedRole, blockNo)
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String, name: String, blockNo: String): Boolean {
        var isValid = true

        if (name.isBlank()) {
            binding.tilName.error = "Name is required"
            isValid = false
        }

        if (email.isBlank()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isBlank()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        if (blockNo.isBlank()) {
            binding.tilBlockNo.error = "Block number is required"
            isValid = false
        }

        return isValid
    }

    private fun showErrorOnFields(message: String) {
        binding.tilName.error = message
    }

    private fun clearAllErrors() {
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        binding.tilBlockNo.error = null
    }

    private fun showSnackbar(message: String, isError: Boolean = false) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (isError) {
            snackbar.setBackgroundTint(getColor(R.color.colorError))
        } else {
            snackbar.setBackgroundTint(getColor(R.color.green))
        }
        snackbar.show()
    }

    private fun View.visible() {
        this.visibility = View.VISIBLE
    }

    private fun View.gone() {
        this.visibility = View.GONE
    }

    private fun View.inVisible() {
        this.visibility = View.INVISIBLE
    }

    private fun MaterialButton.showText() {
        this.text = "Sign Up"
    }

    private fun MaterialButton.hideText() {
        this.text = ""
    }
}